package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WithFeature;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithFeatureImpl<F extends Feature> extends FeatureConfiguratorImpl implements WithFeature<F> {
   private F feature;

   WithFeatureImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public WithFeature<F> feature(F feature) {
      this.feature = feature;
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.feature == null) {
         this.throwStateError("Feature must be set");
      }

      return this.feature;
   }

   public static class Key<F extends Feature> extends FeatureKey<WithFeature<F>> {
      private final F feature;

      public Key(Identifier id, F feature) {
         super(id);
         this.feature = feature;
      }

      public WithFeature<F> bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new WithFeatureImpl<F>(ctx, this.key).feature(this.feature);
      }
   }
}
