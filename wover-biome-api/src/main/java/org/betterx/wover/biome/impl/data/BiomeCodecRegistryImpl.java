package org.betterx.wover.biome.impl.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.entrypoint.LibWoverSurface;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BiomeCodecRegistryImpl {
   public static final ResourceKey<Registry<MapCodec<? extends BiomeData>>> BIOME_CODEC_REGISTRY = DatapackRegistryBuilder.createRegistryKey(
      LibWoverSurface.C.id("wover/biome_codec")
   );
   public static final Registry<MapCodec<? extends BiomeData>> BIOME_CODECS = BuiltInRegistryManager.createRegistry(
      BIOME_CODEC_REGISTRY, BiomeCodecRegistryImpl::onBootstrap
   );
   public static final Codec<BiomeData> CODEC = BIOME_CODECS.byNameCodec().dispatch(BiomeData::codec, Function.identity());

   public static MapCodec<? extends BiomeData> register(
      Registry<MapCodec<? extends BiomeData>> registry, Identifier location, MapCodec<? extends BiomeData> codec
   ) {
      return (MapCodec<? extends BiomeData>)BuiltInRegistryManager.register(registry, location, codec);
   }

   @Internal
   public static void initialize() {
      onBootstrap(BIOME_CODECS);
   }

   private static MapCodec<? extends BiomeData> onBootstrap(Registry<MapCodec<? extends BiomeData>> registry) {
      Identifier biomeData = LibWoverBiome.C.id("vanilla_data");
      return registry.containsKey(biomeData)
         ? (MapCodec)((Reference)registry.get(biomeData).orElseThrow()).value()
         : register(registry, biomeData, BiomeData.CODEC);
   }
}
