package org.betterx.wover.feature.api.placed;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.impl.placed.BoundPlacedFeatureKeyImpl;
import org.betterx.wover.feature.impl.placed.PlacedFeatureKeyImpl;
import org.betterx.wover.feature.impl.placed.PlacedFeatureManagerImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlacedFeatureManager {
   public static final Event<OnBootstrapRegistry<PlacedFeature>> BOOTSTRAP_PLACED_FEATURES = PlacedFeatureManagerImpl.BOOTSTRAP_PLACED_FEATURES;

   public static PlacedFeatureKey createKey(Identifier location) {
      return new PlacedFeatureKeyImpl(location);
   }

   public static BoundPlacedFeatureKey createKey(Identifier location, ResourceKey<Feature> feature) {
      return new BoundPlacedFeatureKeyImpl(location, feature);
   }

   public static <B extends FeatureConfigurator> BoundPlacedFeatureKey createKey(Identifier location, FeatureKey<B> featureKey) {
      return new BoundPlacedFeatureKeyImpl(location, featureKey);
   }

   public static <B extends FeatureConfigurator> BoundPlacedFeatureKey createKey(FeatureKey<B> featureKey) {
      return new BoundPlacedFeatureKeyImpl(featureKey.key.identifier(), featureKey);
   }

   @Nullable
   public static Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> getter, @NotNull ResourceKey<PlacedFeature> key) {
      return PlacedFeatureManagerImpl.getHolder(getter, key);
   }

   @Nullable
   public static Holder<PlacedFeature> getHolder(@Nullable BootstrapContext<?> context, @NotNull ResourceKey<PlacedFeature> key) {
      return PlacedFeatureManagerImpl.getHolder(context.lookup(Registries.PLACED_FEATURE), key);
   }

   private PlacedFeatureManager() {
   }
}
