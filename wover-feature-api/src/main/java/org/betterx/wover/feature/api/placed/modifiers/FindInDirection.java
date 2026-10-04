package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class FindInDirection implements PlacementModifier {
   public static final MapCodec<FindInDirection> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).optionalFieldOf("dir", List.of(Direction.DOWN)).forGetter(a -> a.directions),
            Codec.intRange(1, 32).optionalFieldOf("dist", 12).forGetter(p -> p.maxSearchDistance),
            Codec.BOOL.optionalFieldOf("random_select", true).forGetter(p -> p.randomSelect),
            Codec.INT.optionalFieldOf("offset_in_dir", 0).forGetter(p -> p.offsetInDir),
            BlockPredicate.CODEC.optionalFieldOf("surface_predicate", BlockPredicates.ONLY_GROUND).forGetter(p -> p.surfacePredicate)
         )
         .apply(instance, FindInDirection::new)
   );
   private static final FindInDirection DOWN = new FindInDirection(Direction.DOWN, 6, 0, BlockPredicates.ONLY_GROUND);
   private static final FindInDirection UP = new FindInDirection(Direction.UP, 6, 0, BlockPredicates.ONLY_GROUND);
   private final List<Direction> directions;
   private final int maxSearchDistance;
   private final int offsetInDir;
   private final boolean randomSelect;
   private final IntProvider provider;
   private final BlockPredicate surfacePredicate;

   public FindInDirection(Direction direction, int maxSearchDistance, int offsetInDir, BlockPredicate surfacePredicate) {
      this(List.of(direction), maxSearchDistance, false, offsetInDir, surfacePredicate);
   }

   public FindInDirection(List<Direction> directions, int maxSearchDistance, int offsetInDir, BlockPredicate surfacePredicate) {
      this(directions, maxSearchDistance, directions.size() > 1, offsetInDir, surfacePredicate);
   }

   public FindInDirection(List<Direction> directions, int maxSearchDistance, boolean randomSelect, int offsetInDir, BlockPredicate surfacePredicate) {
      this.directions = directions;
      this.maxSearchDistance = maxSearchDistance;
      this.provider = UniformInt.of(0, this.directions.size() - 1);
      this.randomSelect = randomSelect;
      this.offsetInDir = offsetInDir;
      this.surfacePredicate = surfacePredicate;
   }

   public static PlacementModifier down() {
      return DOWN;
   }

   public static PlacementModifier up() {
      return UP;
   }

   public static PlacementModifier down(int dist) {
      return dist == DOWN.maxSearchDistance && 0 == DOWN.offsetInDir ? DOWN : new FindInDirection(Direction.DOWN, dist, 0, BlockPredicates.ONLY_GROUND);
   }

   public static PlacementModifier up(int dist) {
      return dist == UP.maxSearchDistance && 0 == UP.offsetInDir ? UP : new FindInDirection(Direction.UP, dist, 0, BlockPredicates.ONLY_GROUND);
   }

   public static PlacementModifier down(int dist, int offset) {
      return dist == DOWN.maxSearchDistance && 0 == DOWN.offsetInDir ? DOWN : new FindInDirection(Direction.DOWN, dist, offset, BlockPredicates.ONLY_GROUND);
   }

   public static PlacementModifier up(int dist, int offset) {
      return dist == UP.maxSearchDistance && offset == UP.offsetInDir ? UP : new FindInDirection(Direction.UP, dist, offset, BlockPredicates.ONLY_GROUND);
   }

   public Direction randomDirection(RandomSource random) {
      return this.directions.get(this.provider.sample(random));
   }

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      if (this.randomSelect) {
         this.submitSingle(placementContext, blockPos, consumer, this.randomDirection(randomSource));
      } else {
         for (Direction d : this.directions) {
            this.submitSingle(placementContext, blockPos, consumer, d);
         }
      }
   }

   private static int roomInChunk(BlockPos blockPos, Direction dir) {
      return switch (dir) {
         case EAST -> 15 - SectionPos.sectionRelative(blockPos.getX());
         case WEST -> SectionPos.sectionRelative(blockPos.getX());
         case SOUTH -> 15 - SectionPos.sectionRelative(blockPos.getZ());
         case NORTH -> SectionPos.sectionRelative(blockPos.getZ());
         case UP, DOWN -> Integer.MAX_VALUE;
         default -> throw new MatchException(null, null);
      };
   }

   private void submitSingle(PlacementContext placementContext, BlockPos blockPos, Consumer<BlockPos> consumer, Direction searchDirection) {
      int searchDist = Math.min(this.maxSearchDistance, roomInChunk(blockPos, searchDirection));
      int oppositeDist = Math.min(this.maxSearchDistance, roomInChunk(blockPos, searchDirection.getOpposite()));
      MutableBlockPos POS = blockPos.mutable();
      if (BlockHelper.findOnSurroundingSurface(placementContext.getLevel(), POS, searchDirection, searchDist, oppositeDist, this.surfacePredicate)) {
         if (this.offsetInDir != 0) {
            consumer.accept(POS.move(searchDirection, this.offsetInDir).immutable());
         } else {
            consumer.accept(POS.immutable());
         }
      }
   }

   @NotNull
   public MapCodec<FindInDirection> codec() {
      return CODEC;
   }
}
