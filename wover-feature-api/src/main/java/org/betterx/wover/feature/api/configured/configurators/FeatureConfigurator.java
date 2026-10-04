package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;

public interface FeatureConfigurator {
   Holder<Feature> register();

   FeaturePlacementBuilder inlinePlace();

   Holder<Feature> directHolder();
}
