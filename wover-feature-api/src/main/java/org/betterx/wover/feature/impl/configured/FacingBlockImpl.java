package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.FacingBlock;
import org.betterx.wover.feature.api.features.PlaceFacingBlockFeature;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedList.Builder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FacingBlockImpl extends FeatureConfiguratorImpl implements FacingBlock {
   private final Builder<BlockState> stateBuilder = WeightedList.builder();
   BlockState firstState;
   private int count = 0;
   private List<Direction> directions = BlockHelper.HORIZONTAL;

   FacingBlockImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public FacingBlock allHorizontal() {
      this.directions = BlockHelper.HORIZONTAL;
      return this;
   }

   @Override
   public FacingBlock allVertical() {
      this.directions = BlockHelper.VERTICAL;
      return this;
   }

   @Override
   public FacingBlock allDirections() {
      this.directions = BlockHelper.ALL;
      return this;
   }

   @Override
   public FacingBlock add(Block block) {
      return this.add(block, 1);
   }

   @Override
   public FacingBlock add(BlockState state) {
      return this.add(state, 1);
   }

   public FacingBlock add(Block block, int weight) {
      return this.add(block.defaultBlockState(), weight);
   }

   public FacingBlock add(BlockState state, int weight) {
      if (this.firstState == null) {
         this.firstState = state;
      }

      this.count++;
      this.stateBuilder.add(state, weight);
      return this;
   }

   public FacingBlock addAllStates(Block block, int weight) {
      Set<BlockState> states = BlockHelper.getPossibleStates(block);
      states.forEach(s -> this.add(s, Math.max(1, weight / states.size())));
      return this;
   }

   public FacingBlock addAllStatesFor(IntegerProperty prop, Block block, int weight) {
      Collection<Integer> values = prop.getPossibleValues();
      values.forEach(s -> this.add((BlockState)block.defaultBlockState().setValue(prop, s), Math.max(1, weight / values.size())));
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      BlockStateProvider provider = null;
      if (this.count == 1) {
         provider = new SimpleStateProvider(this.firstState);
      } else {
         WeightedList<BlockState> list = this.stateBuilder.build();
         if (!list.isEmpty()) {
            provider = new WeightedStateProvider(list);
         }
      }

      if (provider == null) {
         throw new IllegalStateException("Facing Blocks need a State Provider.");
      } else {
         return new PlaceFacingBlockFeature(provider, this.directions);
      }
   }

   public static class Key extends FeatureKey<FacingBlock> {
      public Key(Identifier id) {
         super(id);
      }

      public FacingBlock bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new FacingBlockImpl(ctx, this.key);
      }
   }
}
