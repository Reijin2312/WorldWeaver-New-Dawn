package org.betterx.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record SequenceFeature(List<Holder<PlacedFeature>> features) implements Feature {
   public static final MapCodec<SequenceFeature> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ExtraCodecs.nonEmptyList(PlacedFeature.CODEC.listOf()).fieldOf("features").forGetter(SequenceFeature::features))
         .apply(instance, SequenceFeature::new)
   );

   public static SequenceFeature createSequence(List<Holder<PlacedFeature>> features) {
      return new SequenceFeature(features);
   }

   public MapCodec<SequenceFeature> codec() {
      return CODEC;
   }

   public Stream<Holder<Feature>> getSubFeatures() {
      return this.features.stream().flatMap(f -> ((PlacedFeature)f.value()).getFeatures());
   }

   public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
      boolean placed = false;

      for (Holder<PlacedFeature> f : this.features) {
         placed |= ((PlacedFeature)f.value()).place(level, generator, random, pos);
      }

      return placed;
   }
}
