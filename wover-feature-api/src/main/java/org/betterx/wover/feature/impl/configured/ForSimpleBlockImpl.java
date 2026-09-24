package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.ForSimpleBlock;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForSimpleBlockImpl extends FeatureConfiguratorImpl implements ForSimpleBlock {
   private BlockStateProvider provider;

   ForSimpleBlockImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> featureKey) {
      super(ctx, featureKey);
   }

   @Override
   public ForSimpleBlock block(BlockStateProvider provider) {
      this.provider = provider;
      return this;
   }

   @Override
   public ForSimpleBlock block(Block block) {
      return this.block(BlockStateProvider.of(block));
   }

   @Override
   public ForSimpleBlock block(BlockState state) {
      return this.block(BlockStateProvider.of(state));
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.provider == null) {
         this.throwStateError("No block state provider set.");
      }

      return new SimpleBlockFeature(this.provider);
   }

   public static class Key extends FeatureKey<ForSimpleBlock> {
      public Key(Identifier id) {
         super(id);
      }

      public ForSimpleBlock bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new ForSimpleBlockImpl(ctx, this.key);
      }
   }
}
