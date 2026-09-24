package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.placed.BasePlacedFeatureKey;
import org.betterx.wover.feature.impl.random.RandomPatchFeature;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RandomPatchImpl extends FeatureConfiguratorImpl implements RandomPatch {
   private Holder<PlacedFeature> featureToPlace;
   private int tries = 96;
   private int xzSpread = 7;
   private int ySpread = 3;

   RandomPatchImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> featureKey) {
      super(ctx, featureKey);
   }

   public RandomPatch likeDefaultNetherVegetation() {
      return this.likeDefaultNetherVegetation(8, 4);
   }

   public RandomPatch likeDefaultNetherVegetation(int xzSpread, int ySpread) {
      this.xzSpread = xzSpread;
      this.ySpread = ySpread;
      this.tries = xzSpread * xzSpread;
      return this;
   }

   public RandomPatch tries(int tries) {
      this.tries = tries;
      return this;
   }

   public RandomPatch spreadXZ(int spread) {
      this.xzSpread = spread;
      return this;
   }

   public RandomPatch spreadY(int spread) {
      this.ySpread = spread;
      return this;
   }

   @Override
   public <K extends BasePlacedFeatureKey<K>> RandomPatch featureToPlace(BasePlacedFeatureKey<K> featureToPlace) {
      return this.featureToPlace(featureToPlace.getHolder(this.bootstrapContext != null ? this.bootstrapContext : this.getTransitiveBootstrapContext()));
   }

   @Override
   public RandomPatch featureToPlace(Holder<PlacedFeature> featureToPlace) {
      this.featureToPlace = featureToPlace;
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.featureToPlace == null) {
         this.throwStateError("No PlacedFeature was provided.");
      }

      return new RandomPatchFeature(this.tries, this.xzSpread, this.ySpread, this.featureToPlace);
   }

   public static class Key extends FeatureKey<RandomPatch> {
      public Key(Identifier id) {
         super(id);
      }

      public RandomPatch bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new RandomPatchImpl(ctx, this.key);
      }
   }
}
