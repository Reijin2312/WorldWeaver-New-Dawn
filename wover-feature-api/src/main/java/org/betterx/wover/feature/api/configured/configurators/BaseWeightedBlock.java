package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface BaseWeightedBlock<W extends BaseWeightedBlock<W>> extends FeatureConfigurator {
   W add(Block var1, int var2);

   W add(BlockState var1, int var2);

   W addAllStates(Block var1, int var2);

   W addAllStatesFor(IntegerProperty var1, Block var2, int var3);
}
