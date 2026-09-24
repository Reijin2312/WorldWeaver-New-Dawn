package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WeightedBlockPatch;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import org.betterx.wover.feature.impl.random.RandomPatchFeature;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeightedBlockPatchImpl extends WeightedBaseBlockImpl<WeightedBlockPatch> implements WeightedBlockPatch {
   private BlockPredicate groundType = null;
   private boolean isEmpty = true;
   private int tries = 96;
   private int xzSpread = 7;
   private int ySpread = 3;

   WeightedBlockPatchImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public WeightedBlockPatch isEmpty() {
      return this.isEmpty(true);
   }

   @Override
   public WeightedBlockPatch isEmpty(boolean value) {
      this.isEmpty = value;
      return this;
   }

   @Override
   public WeightedBlockPatch isOn(BlockPredicate predicate) {
      this.groundType = predicate;
      return this;
   }

   @Override
   public WeightedBlockPatch isEmptyAndOn(BlockPredicate predicate) {
      return this.isEmpty().isOn(predicate);
   }

   public WeightedBlockPatch likeDefaultNetherVegetation() {
      return this.likeDefaultNetherVegetation(8, 4);
   }

   public WeightedBlockPatch likeDefaultNetherVegetation(int xzSpread, int ySpread) {
      this.xzSpread = xzSpread;
      this.ySpread = ySpread;
      this.tries = xzSpread * xzSpread;
      return this;
   }

   public WeightedBlockPatch tries(int v) {
      this.tries = v;
      return this;
   }

   public WeightedBlockPatch spreadXZ(int v) {
      this.xzSpread = v;
      return this;
   }

   public WeightedBlockPatch spreadY(int v) {
      this.ySpread = v;
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      FeaturePlacementBuilder blockFeature = FeatureContentManager.INLINE_BUILDER
         .simple()
         .block(new WeightedStateProvider(this.stateBuilder.build()))
         .inlinePlace();
      if (this.isEmpty) {
         blockFeature.isEmpty();
      }

      if (this.groundType != null) {
         blockFeature.isOn(this.groundType);
      }

      return new RandomPatchFeature(this.tries, this.xzSpread, this.ySpread, blockFeature.directHolder());
   }

   public static class Key extends FeatureKey<WeightedBlockPatch> {
      public Key(Identifier id) {
         super(id);
      }

      public WeightedBlockPatch bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new WeightedBlockPatchImpl(ctx, this.key);
      }
   }

   public static class KeyBonemeal extends FeatureKey<WeightedBlockPatch> {
      public KeyBonemeal(Identifier id) {
         super(id);
      }

      public WeightedBlockPatch bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new WeightedBlockPatchImpl(ctx, this.key).likeDefaultBonemeal();
      }
   }
}
