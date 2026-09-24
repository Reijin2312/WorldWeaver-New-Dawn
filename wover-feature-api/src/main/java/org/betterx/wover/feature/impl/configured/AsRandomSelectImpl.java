package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsRandomSelect;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsRandomSelectImpl extends FeatureConfiguratorImpl implements AsRandomSelect {
   private final List<WeightedPlacedFeature> features = new LinkedList<>();
   private Holder<PlacedFeature> defaultFeature;

   AsRandomSelectImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public AsRandomSelect add(PlacedFeatureKey feature, float weight) {
      return this.add(feature.getHolder(this.bootstrapContext), weight);
   }

   @Override
   public AsRandomSelect add(Holder<PlacedFeature> feature, float weight) {
      this.features.add(new WeightedPlacedFeature(feature, weight));
      return this;
   }

   @Override
   public AsRandomSelect defaultFeature(PlacedFeatureKey feature) {
      return this.defaultFeature(feature.getHolder(this.bootstrapContext));
   }

   @Override
   public AsRandomSelect defaultFeature(Holder<PlacedFeature> feature) {
      this.defaultFeature = feature;
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      return new RandomSelectorFeature(this.features, this.defaultFeature);
   }

   public static class Key extends FeatureKey<AsRandomSelect> {
      public Key(Identifier id) {
         super(id);
      }

      public AsRandomSelect bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new AsRandomSelectImpl(ctx, this.key);
      }
   }
}
