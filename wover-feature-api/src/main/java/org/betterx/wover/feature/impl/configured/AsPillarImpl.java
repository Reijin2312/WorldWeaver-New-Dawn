package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsPillar;
import org.betterx.wover.feature.api.features.PillarFeature;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsPillarImpl extends FeatureConfiguratorImpl implements AsPillar {
   private IntProvider maxHeight;
   private IntProvider minHeight;
   private BlockStateProvider stateProvider;
   private PillarFeature.KnownTransformers transformer;
   private Direction direction = Direction.UP;
   private BlockPredicate allowedPlacement = BlockPredicate.ONLY_IN_AIR_PREDICATE;

   AsPillarImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public AsPillar allowedPlacement(BlockPredicate predicate) {
      this.allowedPlacement = predicate;
      return this;
   }

   @Override
   public AsPillar transformer(@NotNull PillarFeature.KnownTransformers transformer) {
      this.transformer = transformer;
      return this;
   }

   @Override
   public AsPillar direction(Direction direction) {
      this.direction = direction;
      return this;
   }

   @Override
   public AsPillar blockState(Block block) {
      return this.blockState(BlockStateProvider.of(block.defaultBlockState()));
   }

   @Override
   public AsPillar blockState(BlockState state) {
      return this.blockState(BlockStateProvider.of(state));
   }

   @Override
   public AsPillar blockState(BlockStateProvider provider) {
      this.stateProvider = provider;
      return this;
   }

   @Override
   public AsPillar maxHeight(int max) {
      this.maxHeight = ConstantInt.of(max);
      return this;
   }

   @Override
   public AsPillar maxHeight(IntProvider max) {
      this.maxHeight = max;
      return this;
   }

   @Override
   public AsPillar minHeight(int min) {
      this.minHeight = ConstantInt.of(min);
      return this;
   }

   @Override
   public AsPillar minHeight(IntProvider min) {
      this.minHeight = min;
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.transformer == null) {
         this.throwStateError("A Pillar Feature needs a transformer");
      }

      if (this.stateProvider == null) {
         this.throwStateError("A Pillar Feature needs a stateProvider");
      }

      if (this.maxHeight == null) {
         this.throwStateError("A Pillar Feature needs a height");
      }

      if (this.minHeight == null) {
         this.minHeight = ConstantInt.of(0);
      }

      return new PillarFeature(
         this.minHeight,
         this.maxHeight,
         this.direction,
         this.allowedPlacement,
         net.minecraft.core.Holder.direct(this.stateProvider),
         this.transformer
      );
   }

   public static class Key extends FeatureKey<AsPillar> {
      public Key(Identifier id) {
         super(id);
      }

      public AsPillar bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new AsPillarImpl(ctx, this.key);
      }
   }
}
