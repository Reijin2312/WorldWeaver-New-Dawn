package org.betterx.wover.feature.api.placed;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

public interface BoundPlacedFeatureKey extends BasePlacedFeatureKey<BoundPlacedFeatureKey> {
   FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> var1);

   FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> var1, @NotNull HolderGetter<Feature> var2);
}
