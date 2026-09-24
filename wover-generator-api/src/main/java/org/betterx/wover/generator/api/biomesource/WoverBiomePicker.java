package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.util.RandomizedWeightedList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.jetbrains.annotations.Nullable;

public class WoverBiomePicker {
   private static final Comparator<BiomeData> BY_BIOME_ID = Comparator.comparing(data -> data.biomeKey.identifier().toString());
   private final Map<BiomeData, WoverBiomePicker.PickableBiome> registeredBiomes = new LinkedHashMap<>();
   public final HolderGetter<Biome> biomeRegistry;
   private final Set<WoverBiomePicker.PickableBiome> biomes = new LinkedHashSet<>();
   public final WoverBiomePicker.PickableBiome fallbackBiome;
   private RandomizedWeightedList<WoverBiomePicker.PickableBiome>.SearchTree tree;

   public WoverBiomePicker(ResourceKey<Biome> fallbackBiome) {
      this(
         WorldState.allStageRegistryAccess() == null ? null : (HolderLookup)WorldState.allStageRegistryAccess().lookup(Registries.BIOME).orElse(null),
         fallbackBiome
      );
   }

   public WoverBiomePicker(HolderLookup<Biome> biomeRegistry, ResourceKey<Biome> fallbackBiome) {
      this.biomeRegistry = biomeRegistry;
      this.fallbackBiome = this.create(BiomeData.tempOf(fallbackBiome));
   }

   public static void consumeSubBiomesForSource(BiomeData sourceBiome, BiConsumer<BiomeData, Float> consumeChild) {
      Registry<BiomeData> reg = WoverBiomeData.getDataRegistry("biome alternatives", sourceBiome.biomeKey);
      reg.entrySet()
         .stream()
         .map(Entry::getValue)
         .filter(data -> data instanceof WoverBiomeData b && sourceBiome.isSame(b.parent))
         .sorted(BY_BIOME_ID)
         .forEach(data -> {
            WoverBiomeData b = (WoverBiomeData)data;
            consumeChild.accept(b, b.genChance);
         });
   }

   private boolean isAllowed(BiomeData biomeData) {
      return biomeData != null;
   }

   private BiomeData nullIfNotAllowed(BiomeData biomeData) {
      return this.isAllowed(biomeData) ? biomeData : null;
   }

   private WoverBiomePicker.PickableBiome create(BiomeData biomeData) {
      if (biomeData == null) {
         return null;
      } else {
         WoverBiomePicker.PickableBiome e = this.registeredBiomes.get(biomeData);
         if (e != null && e.biomeData.isTemp() && !biomeData.isTemp()) {
            this.registeredBiomes.remove(e);
            e = null;
         }

         return e != null ? e : new WoverBiomePicker.PickableBiome(biomeData);
      }
   }

   public void addBiome(BiomeData biome) {
      if (this.isAllowed(biome)) {
         this.biomes.add(this.create(biome));
      }
   }

   public WoverBiomePicker.PickableBiome getBiome(WorldgenRandom random) {
      return (WoverBiomePicker.PickableBiome)this.tree.getRandomValue(random);
   }

   public boolean isEmpty() {
      return this.biomes.isEmpty();
   }

   public void rebuild() {
      RandomizedWeightedList<WoverBiomePicker.PickableBiome> list = new RandomizedWeightedList();
      this.biomes
         .stream()
         .filter(biome -> biome.isValid)
         .sorted(Comparator.comparing(biome -> biome.biomeData, BY_BIOME_ID))
         .forEach(biome -> list.add(biome, biome.biomeData.genChance()));
      if (list.isEmpty()) {
         list.add(this.fallbackBiome, 1.0);
      }

      if (WorldState.allStageRegistryAccess() != null) {
         int beforeSize = this.registeredBiomes.size();
         ArrayList<WoverBiomePicker.PickableBiome> beforeList = new ArrayList<>(this.registeredBiomes.values());

         for (WoverBiomePicker.PickableBiome builtBiome : beforeList) {
            consumeSubBiomesForSource(builtBiome.biomeData, (biomeData, weight) -> builtBiome.subbiomes.add(this.create(biomeData), weight.floatValue()));
         }

         if (this.registeredBiomes.size() != beforeSize) {
            LibWoverWorldGenerator.C.log.verbose("Added " + (this.registeredBiomes.size() - beforeSize) + " Biomes");

            for (WoverBiomePicker.PickableBiome builtBiome : new ArrayList<>(this.registeredBiomes.values())) {
               if (!beforeList.contains(builtBiome)) {
                  LibWoverWorldGenerator.C.log.verbose(" - " + builtBiome.biomeData.biomeKey.identifier() + ", subbiomes=" + builtBiome.subbiomes.size());
               }
            }
         }
      }

      this.tree = list.buildSearchTree();
   }

   @Override
   public String toString() {
      return "BiomePicker{biomes="
         + this.biomes.size()
         + " ("
         + this.registeredBiomes.size()
         + "), biomeRegistry="
         + this.biomeRegistry
         + ", type="
         + super.toString()
         + "}";
   }

   @Nullable
   public static Holder<Biome> getBiomeAt(WorldGenLevel world, BlockPos testPos) {
      ChunkPos chunkPos = ChunkPos.containing(testPos);
      ChunkAccess chunk = world.getChunkSource().getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.BIOMES, false);
      return chunk != null ? chunk.getNoiseBiome(
         QuartPos.fromBlock(testPos.getX()),
         QuartPos.fromBlock(testPos.getY()),
         QuartPos.fromBlock(testPos.getZ())
      ) : null;
   }

   public class PickableBiome {
      public final BiomeData biomeData;
      public final Holder<Biome> biome;
      private final RandomizedWeightedList<WoverBiomePicker.PickableBiome> subbiomes;
      public final WoverBiomePicker.PickableBiome edge;
      public final WoverBiomePicker.PickableBiome parent;
      public final boolean isValid;
      public final int edgeSize;
      public final boolean isVertical;

      private PickableBiome(BiomeData biomeData) {
         Objects.requireNonNull(WoverBiomePicker.this);
         super();
         WoverBiomePicker.this.registeredBiomes.put(biomeData, this);
         this.biomeData = biomeData;
         this.biome = WoverBiomePicker.this.biomeRegistry != null ? WoverBiomePicker.this.biomeRegistry.getOrThrow(biomeData.biomeKey) : null;
         this.isValid = this.biome != null && this.biome.isBound();
         this.subbiomes = new RandomizedWeightedList();
         if (biomeData instanceof WoverBiomeData wData) {
            this.subbiomes.add(this, wData.genChance);
            this.edge = WoverBiomePicker.this.create(WoverBiomePicker.this.nullIfNotAllowed(wData.getEdgeData()));
            this.parent = WoverBiomePicker.this.create(wData.getParentData());
            this.edgeSize = wData.edgeSize;
            this.isVertical = wData.vertical;
         } else {
            this.subbiomes.add(this, 1.0);
            this.edge = null;
            this.parent = null;
            this.edgeSize = 0;
            this.isVertical = false;
         }
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            WoverBiomePicker.PickableBiome entry = (WoverBiomePicker.PickableBiome)o;
            return this.biomeData.equals(entry.biomeData);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.biomeData);
      }

      public WoverBiomePicker.PickableBiome getSubBiome(WorldgenRandom random) {
         return (WoverBiomePicker.PickableBiome)this.subbiomes.getRandomValue(random);
      }

      public WoverBiomePicker.PickableBiome getEdge() {
         return this.edge;
      }

      public WoverBiomePicker.PickableBiome getParentBiome() {
         return this.parent;
      }

      public boolean isSame(WoverBiomePicker.PickableBiome e) {
         return this.biomeData.isSame(e.biomeData);
      }

      @Override
      public String toString() {
         return "PickableBiome{key="
            + this.biomeData.biomeKey.identifier()
            + ", alternatives="
            + this.subbiomes.size()
            + ", edge="
            + (this.edge != null ? this.edge.biomeData.biomeKey.identifier() : "null")
            + ", parent="
            + (this.parent != null ? this.parent.biomeData.biomeKey.identifier() : "null")
            + ", isValid="
            + this.isValid
            + "}";
      }
   }
}

