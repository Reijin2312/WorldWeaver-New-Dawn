package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsSequence;
import org.betterx.wover.feature.api.features.SequenceFeature;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsSequenceImpl extends FeatureConfiguratorImpl implements AsSequence {
   private final List<Holder<PlacedFeature>> features = new LinkedList<>();

   AsSequenceImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public AsSequence add(PlacedFeatureKey featureKey) {
      this.features.add(featureKey.getHolder(this.bootstrapContext != null ? this.bootstrapContext : this.getTransitiveBootstrapContext()));
      return this;
   }

   @Override
   public AsSequence add(Holder<PlacedFeature> holder) {
      this.features.add(holder);
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.features.isEmpty()) {
         this.throwStateError("Sequence must have at least one feature");
      }

      return SequenceFeature.createSequence(this.features);
   }

   public static class Key extends FeatureKey<AsSequence> {
      public Key(Identifier id) {
         super(id);
      }

      public AsSequence bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new AsSequenceImpl(ctx, this.key);
      }
   }
}
