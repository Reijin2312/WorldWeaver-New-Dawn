package org.betterx.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record MarkPostProcessingFeature() implements Feature {
   public static final MapCodec<MarkPostProcessingFeature> CODEC = MapCodec.unit(MarkPostProcessingFeature::new);

   public MapCodec<MarkPostProcessingFeature> codec() {
      return CODEC;
   }

   public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
      level.getChunk(pos.getX() >> 4, pos.getZ() >> 4).markPosForPostProcessing(new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15));
      return true;
   }
}
