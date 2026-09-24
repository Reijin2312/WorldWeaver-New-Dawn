package org.betterx.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class PlaceFacingBlockFeature extends PlaceBlockFeature {
   public static final MapCodec<PlaceFacingBlockFeature> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            blockStateCodec(), ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("dir").orElse(List.of(Direction.NORTH)).forGetter(a -> a.directions)
         )
         .apply(instance, PlaceFacingBlockFeature::new)
   );
   private final List<Direction> directions;

   public PlaceFacingBlockFeature(Block block, List<Direction> dir) {
      this(block.defaultBlockState(), dir);
   }

   public PlaceFacingBlockFeature(BlockState state, List<Direction> dir) {
      this(BlockStateProvider.of(state), dir);
   }

   public PlaceFacingBlockFeature(List<BlockState> states, List<Direction> dir) {
      this(buildWeightedList(states), dir);
   }

   public PlaceFacingBlockFeature(WeightedList<BlockState> blocks, List<Direction> dir) {
      this(new WeightedStateProvider(blocks), dir);
   }

   public PlaceFacingBlockFeature(BlockStateProvider provider, List<Direction> dir) {
      super(provider);
      this.directions = dir;
   }

   public PlaceFacingBlockFeature(Holder<BlockStateProvider> provider, List<Direction> dir) {
      super(provider);
      this.directions = dir;
   }

   public MapCodec<PlaceFacingBlockFeature> codec() {
      return CODEC;
   }

   @Override
   public boolean placeBlock(WorldGenLevel level, BlockPos pos, BlockState targetState) {
      for (Direction dir : this.directions) {
         BlockPos testPos = pos.relative(dir);
         BlockState lookupState = (BlockState)targetState.setValue(HorizontalDirectionalBlock.FACING, dir);
         if (level.getBlockState(testPos).isAir() && lookupState.canSurvive(level, testPos)) {
            level.setBlock(testPos, lookupState, 18);
            return true;
         }
      }

      return false;
   }
}
