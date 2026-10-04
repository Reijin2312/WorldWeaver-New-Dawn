package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface FacingBlock extends BaseWeightedBlock<FacingBlock> {
   FacingBlock allHorizontal();

   FacingBlock allVertical();

   FacingBlock allDirections();

   FacingBlock add(Block var1);

   FacingBlock add(BlockState var1);
}
