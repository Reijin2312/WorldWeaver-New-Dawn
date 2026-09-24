package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public interface AsOre extends FeatureConfigurator {
   AsOre add(Block var1, Block var2);

   AsOre add(Block var1, BlockState var2);

   AsOre add(RuleTest var1, Block var2);

   AsOre add(RuleTest var1, BlockState var2);

   AsOre veinSize(int var1);

   AsOre discardChanceOnAirExposure(float var1);
}
