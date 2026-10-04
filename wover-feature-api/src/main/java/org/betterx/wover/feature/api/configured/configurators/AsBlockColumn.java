package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public interface AsBlockColumn extends FeatureConfigurator {
   AsBlockColumn add(int var1, Block var2);

   AsBlockColumn add(int var1, BlockState var2);

   AsBlockColumn add(int var1, BlockStateProvider var2);

   AsBlockColumn addRandom(int var1, BlockState... var2);

   AsBlockColumn addRandom(IntProvider var1, BlockState... var2);

   AsBlockColumn add(IntProvider var1, Block var2);

   AsBlockColumn add(IntProvider var1, BlockState var2);

   AsBlockColumn add(IntProvider var1, BlockStateProvider var2);

   AsBlockColumn addTripleShape(BlockState var1, IntProvider var2);

   AsBlockColumn addTripleShapeUpsideDown(BlockState var1, IntProvider var2);

   AsBlockColumn addBottomShapeUpsideDown(BlockState var1, IntProvider var2);

   AsBlockColumn addBottomShape(BlockState var1, IntProvider var2);

   AsBlockColumn addTopShapeUpsideDown(BlockState var1, IntProvider var2);

   AsBlockColumn addTopShape(BlockState var1, IntProvider var2);

   AsBlockColumn direction(Direction var1);

   AsBlockColumn prioritizeTip();

   AsBlockColumn prioritizeTip(boolean var1);

   AsBlockColumn allowedPlacement(BlockPredicate var1);
}
