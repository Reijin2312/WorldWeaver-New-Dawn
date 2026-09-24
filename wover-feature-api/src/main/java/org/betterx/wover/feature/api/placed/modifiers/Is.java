package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import org.jetbrains.annotations.NotNull;

public class Is implements PlacementFilter {
   public static final MapCodec<Is> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(cfg -> cfg.predicate), Vec3i.CODEC.optionalFieldOf("offset").forGetter(cfg -> cfg.offset)
         )
         .apply(instance, Is::new)
   );
   private final BlockPredicate predicate;
   private final Optional<Vec3i> offset;

   public Is(BlockPredicate predicate, Optional<Vec3i> offset) {
      this.predicate = predicate;
      this.offset = offset;
   }

   public static Is simple(BlockPredicate predicate) {
      return new Is(predicate, Optional.empty());
   }

   public static Is below(BlockPredicate predicate) {
      return new Is(predicate, Optional.of(Direction.DOWN.getUnitVec3i()));
   }

   public static Is above(BlockPredicate predicate) {
      return new Is(predicate, Optional.of(Direction.UP.getUnitVec3i()));
   }

   public boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
      WorldGenLevel level = ctx.getLevel();
      return this.predicate.test(level, this.offset.<BlockPos>map(v -> pos.offset(v.getX(), v.getY(), v.getZ())).orElse(pos));
   }

   @NotNull
   public MapCodec<Is> codec() {
      return CODEC;
   }
}
