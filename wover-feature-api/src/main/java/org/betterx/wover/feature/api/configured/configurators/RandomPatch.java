package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.BasePlacedFeatureKey;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@Deprecated(
   since = "26.1.0",
   forRemoval = true
)
public interface RandomPatch extends FeatureConfigurator, BasePatch<RandomPatch> {
   <K extends BasePlacedFeatureKey<K>> RandomPatch featureToPlace(BasePlacedFeatureKey<K> var1);

   RandomPatch featureToPlace(Holder<PlacedFeature> var1);
}
