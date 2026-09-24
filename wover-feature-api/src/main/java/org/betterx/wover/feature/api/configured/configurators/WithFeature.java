package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.feature.Feature;

public interface WithFeature<F extends Feature> extends FeatureConfigurator {
   WithFeature<F> feature(F var1);
}
