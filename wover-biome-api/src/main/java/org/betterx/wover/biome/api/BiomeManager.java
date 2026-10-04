package org.betterx.wover.biome.api;

import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.BiomeManagerImpl;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.state.api.WorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BiomeManager {
   public static final Event<OnBootstrapRegistry<Biome>> BOOTSTRAP_BIOME_REGISTRY = BiomeManagerImpl.BOOTSTRAP_BIOME_REGISTRY;
   public static final Event<OnBootstrapBiomes> BOOTSTRAP_BIOMES_WITH_DATA = BiomeManagerImpl.BOOTSTRAP_BIOMES_WITH_DATA;

   public static BiomeKey<BiomeBuilder.Vanilla> vanilla(Identifier location) {
      return BiomeManagerImpl.vanilla(location);
   }

   public static BiomeKey<BiomeBuilder.Wrapped> wrapped(@NotNull ResourceKey<Biome> key) {
      return BiomeManagerImpl.wrapped(key);
   }

   @Nullable
   public static BiomeData biomeData(Identifier biome) {
      return biomeData(WorldState.registryAccess(), biome);
   }

   public static BiomeData biomeData(Provider registryAccess, Identifier biome) {
      return registryAccess == null
         ? null
         : registryAccess.lookup(Registries.BIOME)
            .flatMap(r -> r.get(ResourceKey.create(Registries.BIOME, biome)))
            .map(h -> biomeDataForHolder(registryAccess, h))
            .orElse(null);
   }

   @Nullable
   public static BiomeData biomeDataForHolder(Holder<Biome> biome) {
      return biomeDataForHolder(WorldState.registryAccess(), biome);
   }

   @Nullable
   public static BiomeData biomeDataForHolder(Provider acc, Holder<Biome> biome) {
      if (acc != null) {
         RegistryLookup<BiomeData> reg = acc.lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
         Identifier id = biome.unwrapKey().<Identifier>map(ResourceKey::identifier).orElse(null);
         if (id != null) {
            return reg.get(BiomeDataRegistry.createKey(id)).<BiomeData>map(Reference::value).orElse(null);
         }
      }

      return null;
   }

   public static void setBiome(ChunkAccess chunk, BlockPos pos, Holder<Biome> biome) {
      int sectionY = pos.getY() - chunk.getMinY() >> 4;
      if (chunk.getSection(sectionY).getBiomes() instanceof PalettedContainer<Holder<Biome>> palette) {
         palette.set((pos.getX() & 15) >> 2, (pos.getY() & 15) >> 2, (pos.getZ() & 15) >> 2, biome);
      } else {
         LibWoverBiome.C.LOG.warn("Unable to change Biome at " + pos);
      }
   }

   public static void setBiome(LevelAccessor level, BlockPos pos, Holder<Biome> biome) {
      ChunkAccess chunk = level.getChunk(pos);
      setBiome(chunk, pos, biome);
   }
}
