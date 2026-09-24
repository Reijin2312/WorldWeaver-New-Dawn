package org.betterx.wover.biome.impl.data;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.common.registry.api.CustomRegistryData;
import org.betterx.wover.common.registry.api.CustomRegistryData.DataKey;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.state.api.WorldState;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BiomeDataRegistryImpl {
   public static final EventImpl<OnBootstrapRegistry<BiomeData>> BOOTSTRAP_BIOME_DATA_REGISTRY = new EventImpl("BOOTSTRAP_BIOME_DATA_REGISTRY");
   private static final DataKey<Map<ResourceKey<BiomeData>, BiomeData>> TEMP_BIOME_DATA = CustomRegistryData.createKey(LibWoverBiome.C.id("temp_biome_data"));
   private static boolean didInit = false;

   private static void onBootstrap(BootstrapContext<BiomeData> ctx) {
      BOOTSTRAP_BIOME_DATA_REGISTRY.emit(c -> c.bootstrap(ctx));
   }

   public static BiomeData getFromRegistryOrTemp(ResourceKey<Biome> key) {
      return getFromRegistryOrTemp(key, BiomeData::tempOf);
   }

   @Nullable
   public static BiomeData getFromRegistryOrTemp(ResourceKey<Biome> key, Function<ResourceKey<Biome>, BiomeData> factory) {
      if (WorldState.allStageRegistryAccess() == null) {
         return null;
      } else {
         Registry<BiomeData> registry = WorldState.allStageRegistryAccess().lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
         return getFromRegistryOrTemp(registry, key, factory);
      }
   }

   public static BiomeData getFromRegistryOrTemp(Registry<BiomeData> registry, ResourceKey<Biome> key) {
      return getFromRegistryOrTemp(registry, key, BiomeData::tempOf);
   }

   @Nullable
   public static BiomeData getFromRegistryOrTemp(Registry<BiomeData> registry, ResourceKey<Biome> key, Function<ResourceKey<Biome>, BiomeData> defaultFactory) {
      ResourceKey<BiomeData> dataKey = createKey(key.identifier());
      if (registry != null) {
         Optional<BiomeData> oData = registry.getOptional(dataKey);
         if (oData.isPresent()) {
            return oData.get();
         }
      }

      if (registry instanceof CustomRegistryData tempRegistryData) {
         Map<ResourceKey<BiomeData>, BiomeData> customData = (Map<ResourceKey<BiomeData>, BiomeData>)tempRegistryData.wover_computeDataIfAbsent(
            TEMP_BIOME_DATA, k -> new HashMap()
         );
         return customData.computeIfAbsent(dataKey, k -> BiomeData.tempOf(key));
      } else {
         return defaultFactory.apply(key);
      }
   }

   @Internal
   public static void initialize() {
      if (!didInit) {
         didInit = true;
         DatapackRegistryBuilder.register(BiomeDataRegistry.BIOME_DATA_REGISTRY, BiomeCodecRegistryImpl.CODEC, BiomeDataRegistryImpl::onBootstrap);
      }
   }

   public static ResourceKey<BiomeData> createKey(Identifier ruleID) {
      return ResourceKey.create(BiomeDataRegistry.BIOME_DATA_REGISTRY, ruleID);
   }
}
