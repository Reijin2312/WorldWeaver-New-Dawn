package org.betterx.wover.generator.impl.biomesource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.biome.api.data.BiomeCodecRegistry;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus.Internal;

public class WoverBiomeDataImpl {
   @Internal
   public static void initialize() {
      BiomeCodecRegistry.register(LibWoverWorldGenerator.C.id("wover_data"), WoverBiomeData.CODEC);
   }

   public static class CodecAttributes<T extends WoverBiomeData> {
      public RecordCodecBuilder<T, Float> t0 = Codec.FLOAT.optionalFieldOf("terrainHeight", 0.1F).forGetter(o -> o.terrainHeight);
      public RecordCodecBuilder<T, Float> t1 = Codec.FLOAT.optionalFieldOf("genChance", 1.0F).forGetter(o -> o.genChance);
      public RecordCodecBuilder<T, Integer> t2 = Codec.INT.optionalFieldOf("edgeSize", 0).forGetter(o -> o.edgeSize);
      public RecordCodecBuilder<T, Boolean> t3 = Codec.BOOL.optionalFieldOf("vertical", false).forGetter(o -> o.vertical);
      public RecordCodecBuilder<T, Optional<ResourceKey<Biome>>> t4 = ResourceKey.codec(Registries.BIOME)
         .optionalFieldOf("edge")
         .forGetter(o -> Optional.ofNullable(o.edge));
      public RecordCodecBuilder<T, Optional<ResourceKey<Biome>>> t5 = ResourceKey.codec(Registries.BIOME)
         .optionalFieldOf("parent")
         .forGetter(o -> Optional.ofNullable(o.parent));

   }
}

