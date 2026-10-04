package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public interface ForSimpleBlock extends FeatureConfigurator {
   ForSimpleBlock block(BlockStateProvider var1);

   ForSimpleBlock block(Block var1);

   ForSimpleBlock block(BlockState var1);
}
