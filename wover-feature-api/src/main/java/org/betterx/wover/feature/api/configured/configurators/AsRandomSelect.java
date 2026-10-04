package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface AsRandomSelect extends FeatureConfigurator {
   AsRandomSelect add(PlacedFeatureKey var1, float var2);

   AsRandomSelect add(Holder<PlacedFeature> var1, float var2);

   AsRandomSelect defaultFeature(PlacedFeatureKey var1);

   AsRandomSelect defaultFeature(Holder<PlacedFeature> var1);
}
