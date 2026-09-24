package org.betterx.wover.biome.api.data;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.impl.data.BiomeCodecRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class BiomeCodecRegistry {
   public static final ResourceKey<Registry<MapCodec<? extends BiomeData>>> BIOME_CODEC_REGISTRY = BiomeCodecRegistryImpl.BIOME_CODEC_REGISTRY;
   public static final Registry<MapCodec<? extends BiomeData>> BIOME_CODECS = BiomeCodecRegistryImpl.BIOME_CODECS;

   public static MapCodec<? extends BiomeData> register(Identifier location, MapCodec<? extends BiomeData> codec) {
      return BiomeCodecRegistryImpl.register(BiomeCodecRegistryImpl.BIOME_CODECS, location, codec);
   }
}
