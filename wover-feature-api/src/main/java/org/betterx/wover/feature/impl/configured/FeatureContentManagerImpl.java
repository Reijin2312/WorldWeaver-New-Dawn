package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.features.GrowableFeature;
import org.betterx.wover.feature.impl.random.RandomPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Nullable;

public class FeatureContentManagerImpl {
   public static boolean placeInWorld(
      Feature feature, WorldGenLevel level, BlockPos pos, RandomSource random, @Nullable ChunkGenerator chunkGenerator, boolean asIs
   ) {
      if (!asIs) {
         if (feature instanceof RandomPatchFeature rnd) {
            feature = (Feature)((PlacedFeature)rnd.feature().value()).feature().value();
         }

         if (feature instanceof GrowableFeature growable) {
            return growable.grow(level, pos, random);
         }
      }

      if (chunkGenerator == null && level instanceof ServerLevel sLevel) {
         chunkGenerator = sLevel.getChunkSource().getGenerator();
      }

      return feature.place(level, chunkGenerator, random, pos);
   }
}
