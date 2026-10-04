package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsBlockColumn;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedList.Builder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.BlockColumnFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.BlockColumnFeature.Layer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsBlockColumnImpl extends FeatureConfiguratorImpl implements AsBlockColumn {
   private final List<Layer> layers = new LinkedList<>();
   private Direction direction = Direction.UP;
   private BlockPredicate allowedPlacement = BlockPredicate.ONLY_IN_AIR_PREDICATE;
   private boolean prioritizeTip = false;

   AsBlockColumnImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public AsBlockColumn add(int height, Block block) {
      return this.add(ConstantInt.of(height), BlockStateProvider.of(block));
   }

   @Override
   public AsBlockColumn add(int height, BlockState state) {
      return this.add(ConstantInt.of(height), BlockStateProvider.of(state));
   }

   @Override
   public AsBlockColumn add(int height, BlockStateProvider state) {
      return this.add(ConstantInt.of(height), state);
   }

   @Override
   public final AsBlockColumn addRandom(int height, BlockState... states) {
      return this.addRandom(ConstantInt.of(height), states);
   }

   @Override
   public final AsBlockColumn addRandom(IntProvider height, BlockState... states) {
      Builder<BlockState> builder = WeightedList.builder();

      for (BlockState state : states) {
         builder.add(state, 1);
      }

      return this.add(height, new WeightedStateProvider(builder.build()));
   }

   @Override
   public AsBlockColumn add(IntProvider height, Block block) {
      return this.add(height, BlockStateProvider.of(block));
   }

   @Override
   public AsBlockColumn add(IntProvider height, BlockState state) {
      return this.add(height, BlockStateProvider.of(state));
   }

   @Override
   public AsBlockColumn add(IntProvider height, BlockStateProvider state) {
      this.layers.add(new Layer(height, net.minecraft.core.Holder.direct(state)));
      return this;
   }

   @Override
   public AsBlockColumn addTripleShape(BlockState state, IntProvider midHeight) {
      return this.add(1, (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM))
         .add(midHeight, (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE))
         .add(1, (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP));
   }

   @Override
   public AsBlockColumn addTripleShapeUpsideDown(BlockState state, IntProvider midHeight) {
      return this.add(1, (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP))
         .add(midHeight, (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE))
         .add(1, (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM));
   }

   @Override
   public AsBlockColumn addBottomShapeUpsideDown(BlockState state, IntProvider midHeight) {
      return this.add(midHeight, (BlockState)state.setValue(BlockProperties.BOTTOM, false)).add(1, (BlockState)state.setValue(BlockProperties.BOTTOM, true));
   }

   @Override
   public AsBlockColumn addBottomShape(BlockState state, IntProvider midHeight) {
      return this.add(1, (BlockState)state.setValue(BlockProperties.BOTTOM, true)).add(midHeight, (BlockState)state.setValue(BlockProperties.BOTTOM, false));
   }

   @Override
   public AsBlockColumn addTopShapeUpsideDown(BlockState state, IntProvider midHeight) {
      return this.add(1, (BlockState)state.setValue(BlockProperties.TOP, true)).add(midHeight, (BlockState)state.setValue(BlockProperties.TOP, false));
   }

   @Override
   public AsBlockColumn addTopShape(BlockState state, IntProvider midHeight) {
      return this.add(midHeight, (BlockState)state.setValue(BlockProperties.TOP, false)).add(1, (BlockState)state.setValue(BlockProperties.TOP, true));
   }

   @Override
   public AsBlockColumn direction(Direction dir) {
      this.direction = dir;
      return this;
   }

   @Override
   public AsBlockColumn prioritizeTip() {
      return this.prioritizeTip(true);
   }

   @Override
   public AsBlockColumn prioritizeTip(boolean prio) {
      this.prioritizeTip = prio;
      return this;
   }

   @Override
   public AsBlockColumn allowedPlacement(BlockPredicate predicate) {
      this.allowedPlacement = predicate;
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      return new BlockColumnFeature(this.layers, this.direction, this.allowedPlacement, this.prioritizeTip);
   }

   public static class Key extends FeatureKey<AsBlockColumn> {
      public Key(Identifier id) {
         super(id);
      }

      public AsBlockColumn bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new AsBlockColumnImpl(ctx, this.key);
      }
   }
}
