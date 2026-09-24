package org.betterx.wover.biome.api.data;

import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.LibWoverSurface;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeDataRegistry {
   public static final Event<OnBootstrapRegistry<BiomeData>> BOOTSTRAP_BIOME_DATA_REGISTRY = BiomeDataRegistryImpl.BOOTSTRAP_BIOME_DATA_REGISTRY;
   public static final ResourceKey<Registry<BiomeData>> BIOME_DATA_REGISTRY = DatapackRegistryBuilder.createRegistryKey(
      LibWoverSurface.C.id("wover/worldgen/biome_data")
   );

   private BiomeDataRegistry() {
   }

   public static ResourceKey<BiomeData> createKey(Identifier dataID) {
      return BiomeDataRegistryImpl.createKey(dataID);
   }

   public static ResourceKey<BiomeData> createKey(ResourceKey<Biome> biomeKey) {
      return BiomeDataRegistryImpl.createKey(biomeKey.identifier());
   }

   public static ResourceKey<Biome> createBiomeKey(ResourceKey<BiomeData> biomeDataKey) {
      return ResourceKey.create(Registries.BIOME, biomeDataKey.identifier());
   }
}
