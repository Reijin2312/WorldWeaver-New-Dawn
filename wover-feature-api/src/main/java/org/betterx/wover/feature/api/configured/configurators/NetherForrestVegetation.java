package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public interface NetherForrestVegetation extends BaseWeightedBlock<NetherForrestVegetation> {
   NetherForrestVegetation spreadWidth(int var1);

   NetherForrestVegetation spreadHeight(int var1);

   NetherForrestVegetation provider(WeightedStateProvider var1);
}
