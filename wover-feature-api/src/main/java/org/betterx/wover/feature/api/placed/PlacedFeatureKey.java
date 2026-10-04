package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

public interface PlacedFeatureKey extends BasePlacedFeatureKey<PlacedFeatureKey> {
   FeaturePlacementBuilder place(BootstrapContext<PlacedFeature> var1, ResourceKey<Feature> var2);

   FeaturePlacementBuilder place(BootstrapContext<PlacedFeature> var1, Holder<Feature> var2);

   <B extends FeatureConfigurator> FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> var1, FeatureKey<B> var2);

   FeatureContentManager.InlineBuilder inlineConfiguration(@NotNull BootstrapContext<PlacedFeature> var1);
}
