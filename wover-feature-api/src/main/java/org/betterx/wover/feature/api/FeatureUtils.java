package org.betterx.wover.feature.api;

import org.betterx.wover.feature.impl.configured.FeatureContentManagerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public class FeatureUtils {
   public static boolean placeInWorld(Feature feature, WorldGenLevel level, BlockPos pos, RandomSource random, boolean unchanged) {
      return FeatureContentManagerImpl.placeInWorld(feature, level, pos, random, null, unchanged);
   }

   public static boolean placeInWorld(Feature feature, WorldGenLevel level, BlockPos pos, RandomSource random, ChunkGenerator generator, boolean unchanged) {
      return FeatureContentManagerImpl.placeInWorld(feature, level, pos, random, generator, unchanged);
   }

   private FeatureUtils() {
   }
}
