package org.betterx.wover.generator.api.biomesource.end;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.generator.impl.biomesource.end.BiomeDeciderImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeDecider {
   protected WoverBiomePicker picker;
   protected BiomeMap map;
   private final BiomeDecider.BiomePredicate predicate;

   public static void registerHighPriorityDecider(Identifier location, BiomeDecider decider) {
      BiomeDeciderImpl.registerHighPriorityDecider(location, decider);
   }

   public static void registerDecider(Identifier location, BiomeDecider decider) {
      BiomeDeciderImpl.registerDecider(location, decider);
   }

   protected BiomeDecider(BiomeDecider.BiomePredicate predicate) {
      this(null, null, predicate);
   }

   protected BiomeDecider(HolderLookup<Biome> biomeRegistry, ResourceKey<Biome> fallbackBiome, BiomeDecider.BiomePredicate predicate) {
      this.predicate = predicate;
      this.map = null;
      if (biomeRegistry == null) {
         this.picker = null;
      } else {
         this.picker = new WoverBiomePicker(biomeRegistry, fallbackBiome);
      }
   }

   public abstract boolean canProvideFor(BiomeSource var1);

   public abstract BiomeDecider createInstance(WoverBiomeSource var1);

   public void createMap(BiomeDecider.BiomeMapBuilderFunction mapBuilder) {
      this.map = mapBuilder.create(this.picker, -1);
   }

   public void createMap(BiomeDecider.BiomeMapBuilderFunction mapBuilder, long seed) {
      this.createMap(mapBuilder);
   }

   @Nullable
   public TagKey<Biome> pickerTag() {
      return null;
   }

   @Nullable
   public WoverBiomePicker picker() {
      return this.picker;
   }

   public void clearMapCache() {
      this.map.clearCache();
   }

   public boolean addToPicker(BiomeData biome) {
      if (this.predicate.test(biome)) {
         this.picker.addBiome(biome);
         return true;
      } else {
         return false;
      }
   }

   public void rebuild() {
      if (this.picker != null) {
         this.picker.rebuild();
      }
   }

   public TagKey<Biome> suggestType(
      TagKey<Biome> originalType, TagKey<Biome> suggestedType, int maxHeight, int blockX, int blockY, int blockZ, int quarterX, int quarterY, int quarterZ
   ) {
      return this.suggestType(originalType, suggestedType, 0.0, maxHeight, blockX, blockY, blockZ, quarterX, quarterY, quarterZ);
   }

   public abstract TagKey<Biome> suggestType(
      TagKey<Biome> var1, TagKey<Biome> var2, double var3, int var5, int var6, int var7, int var8, int var9, int var10, int var11
   );

   public abstract boolean canProvideBiome(TagKey<Biome> var1);

   public WoverBiomePicker.PickableBiome provideBiome(TagKey<Biome> suggestedType, int posX, int posY, int posZ) {
      return this.map.getBiome(posX, posY, posZ);
   }

   @FunctionalInterface
   public interface BiomeMapBuilderFunction {
      BiomeMap create(WoverBiomePicker var1, int var2);
   }

   @FunctionalInterface
   public interface BiomePredicate {
      boolean test(BiomeData var1);
   }
}

