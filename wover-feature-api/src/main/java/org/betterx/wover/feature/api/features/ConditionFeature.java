package org.betterx.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class ConditionFeature implements Feature {
   public static final MapCodec<ConditionFeature> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            PlacementModifier.CODEC.fieldOf("filter").forGetter(p -> p.filter),
            PlacedFeature.CODEC.fieldOf("filter_pass").forGetter(p -> p.okFeature),
            PlacedFeature.CODEC.optionalFieldOf("filter_fail").forGetter(p -> p.failFeature)
         )
         .apply(instance, ConditionFeature::new)
   );
   public final PlacementModifier filter;
   public final Holder<PlacedFeature> okFeature;
   public final Optional<Holder<PlacedFeature>> failFeature;

   public ConditionFeature(@NotNull PlacementFilter filter, @NotNull Holder<PlacedFeature> okFeature) {
      this(filter, okFeature, Optional.empty());
   }

   public ConditionFeature(@NotNull PlacementFilter filter, @NotNull Holder<PlacedFeature> okFeature, @NotNull Holder<PlacedFeature> failFeature) {
      this(filter, okFeature, Optional.of(failFeature));
   }

   private ConditionFeature(@NotNull PlacementModifier filter, @NotNull Holder<PlacedFeature> okFeature, @NotNull Optional<Holder<PlacedFeature>> failFeature) {
      this.filter = filter;
      this.okFeature = okFeature;
      this.failFeature = failFeature;
   }

   public MapCodec<ConditionFeature> codec() {
      return CODEC;
   }

   public Stream<Holder<Feature>> getSubFeatures() {
      return Stream.concat(
         ((PlacedFeature)this.okFeature.value()).getFeatures(), this.failFeature.stream().flatMap(f -> ((PlacedFeature)f.value()).getFeatures())
      );
   }

   public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
      PlacementContext c = new PlacementContext(level, generator, Optional.empty());
      boolean[] passed = new boolean[1];
      this.filter.modify(c, random, pos, p -> passed[0] = true);
      Holder<PlacedFeature> state = passed[0] ? this.okFeature : this.failFeature.orElse(null);
      return state != null ? ((PlacedFeature)state.value()).place(level, generator, random, pos) : false;
   }
}
