package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IsBasin implements PlacementFilter {
   public static final MapCodec<IsBasin> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(cfg -> cfg.predicate),
            BlockPredicate.CODEC.optionalFieldOf("top_predicate").forGetter(cfg -> Optional.ofNullable(cfg.topPredicate))
         )
         .apply(instance, IsBasin::new)
   );
   @NotNull
   private final BlockPredicate predicate;
   @Nullable
   private final BlockPredicate topPredicate;

   private IsBasin(@NotNull BlockPredicate predicate, @NotNull Optional<BlockPredicate> topPredicate) {
      this(predicate, topPredicate.orElse(null));
   }

   private IsBasin(@NotNull BlockPredicate predicate, @Nullable BlockPredicate topPredicate) {
      this.predicate = predicate;
      this.topPredicate = topPredicate;
   }

   public static PlacementFilter simple(BlockPredicate predicate) {
      return new IsBasin(predicate, (BlockPredicate)null);
   }

   public static IsBasin openTop(BlockPredicate predicate) {
      return new IsBasin(predicate, BlockPredicate.ONLY_IN_AIR_PREDICATE);
   }

   public boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
      WorldGenLevel level = ctx.getLevel();
      return this.topPredicate != null && !this.topPredicate.test(level, pos.above())
         ? false
         : this.predicate.test(level, pos.below())
            && this.predicate.test(level, pos.west())
            && this.predicate.test(level, pos.east())
            && this.predicate.test(level, pos.north())
            && this.predicate.test(level, pos.south());
   }

   @NotNull
   public MapCodec<IsBasin> codec() {
      return CODEC;
   }
}
