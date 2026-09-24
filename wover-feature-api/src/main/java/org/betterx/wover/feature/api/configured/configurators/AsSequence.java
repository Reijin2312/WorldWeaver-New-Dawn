package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface AsSequence extends FeatureConfigurator {
   AsSequence add(PlacedFeatureKey var1);

   AsSequence add(Holder<PlacedFeature> var1);
}
