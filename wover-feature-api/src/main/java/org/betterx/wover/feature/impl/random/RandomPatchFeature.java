package org.betterx.wover.feature.impl.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record RandomPatchFeature(int tries, int xzSpread, int ySpread, Holder<PlacedFeature> feature) implements Feature {
   public static final MapCodec<RandomPatchFeature> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(cfg -> cfg.tries),
            Codec.intRange(0, 1024).fieldOf("xz_spread").orElse(7).forGetter(cfg -> cfg.xzSpread),
            Codec.intRange(0, 256).fieldOf("y_spread").orElse(3).forGetter(cfg -> cfg.ySpread),
            PlacedFeature.CODEC.fieldOf("feature").forGetter(cfg -> cfg.feature)
         )
         .apply(instance, RandomPatchFeature::new)
   );

   public MapCodec<RandomPatchFeature> codec() {
      return CODEC;
   }

   public Stream<Holder<Feature>> getSubFeatures() {
      return ((PlacedFeature)this.feature.value()).getFeatures();
   }

   public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
      MutableBlockPos mutable = new MutableBlockPos();
      int xzSpread = this.xzSpread + 1;
      int ySpread = this.ySpread + 1;
      int placed = 0;

      for (int i = 0; i < this.tries; i++) {
         mutable.setWithOffset(
            origin,
            random.nextInt(xzSpread) - random.nextInt(xzSpread),
            random.nextInt(ySpread) - random.nextInt(ySpread),
            random.nextInt(xzSpread) - random.nextInt(xzSpread)
         );
         if (((PlacedFeature)this.feature.value()).place(level, generator, random, mutable)) {
            placed++;
         }
      }

      return placed > 0;
   }
}
