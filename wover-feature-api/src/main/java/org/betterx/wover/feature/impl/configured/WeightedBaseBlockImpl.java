package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.configured.configurators.BaseWeightedBlock;
import java.util.Collection;
import java.util.Set;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedList.Builder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public abstract class WeightedBaseBlockImpl<W extends BaseWeightedBlock<W>> extends FeatureConfiguratorImpl implements BaseWeightedBlock<W> {
   Builder<BlockState> stateBuilder = WeightedList.builder();

   WeightedBaseBlockImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public W add(Block block, int weight) {
      return this.add(block.defaultBlockState(), weight);
   }

   @Override
   public W add(BlockState state, int weight) {
      this.stateBuilder.add(state, weight);
      return (W)this;
   }

   @Override
   public W addAllStates(Block block, int weight) {
      Set<BlockState> states = BlockHelper.getPossibleStates(block);
      states.forEach(s -> this.add(block.defaultBlockState(), Math.max(1, weight / states.size())));
      return (W)this;
   }

   @Override
   public W addAllStatesFor(IntegerProperty prop, Block block, int weight) {
      Collection<Integer> values = prop.getPossibleValues();
      values.forEach(s -> this.add((BlockState)block.defaultBlockState().setValue(prop, s), Math.max(1, weight / values.size())));
      return (W)this;
   }
}
