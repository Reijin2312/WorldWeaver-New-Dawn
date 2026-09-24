package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.NetherForrestVegetation;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedList.Builder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OverlayFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherForrestVegetationImpl extends FeatureConfiguratorImpl implements NetherForrestVegetation {
   private Builder<BlockState> blocks;
   private WeightedStateProvider stateProvider;
   private int spreadWidth = 8;
   private int spreadHeight = 4;
   private static final BlockPredicate ON_NYLIUM = BlockPredicate.matchesTag(Direction.DOWN.getUnitVec3i(), BlockTags.NYLIUM);

   NetherForrestVegetationImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public NetherForrestVegetation spreadWidth(int width) {
      this.spreadWidth = width;
      return this;
   }

   @Override
   public NetherForrestVegetation spreadHeight(int height) {
      this.spreadHeight = height;
      return this;
   }

   public NetherForrestVegetation addAllStates(Block block, int weight) {
      Set<BlockState> states = BlockHelper.getPossibleStates(block);
      states.forEach(s -> this.add(block.defaultBlockState(), Math.max(1, weight / states.size())));
      return this;
   }

   public NetherForrestVegetation addAllStatesFor(IntegerProperty prop, Block block, int weight) {
      Collection<Integer> values = prop.getPossibleValues();
      values.forEach(s -> this.add((BlockState)block.defaultBlockState().setValue(prop, s), Math.max(1, weight / values.size())));
      return this;
   }

   public NetherForrestVegetation add(Block block, int weight) {
      return this.add(block.defaultBlockState(), weight);
   }

   public NetherForrestVegetation add(BlockState state, int weight) {
      if (this.stateProvider != null) {
         throw new IllegalStateException("You can not add new state once a WeightedStateProvider was built. (" + state + ", " + weight + ")");
      } else {
         if (this.blocks == null) {
            this.blocks = WeightedList.builder();
         }

         this.blocks.add(state, weight);
         return this;
      }
   }

   @Override
   public NetherForrestVegetation provider(WeightedStateProvider provider) {
      if (this.blocks != null) {
         this.throwStateError("You can not set a WeightedStateProvider after states were added manually.");
      }

      this.stateProvider = provider;
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.stateProvider == null && this.blocks == null) {
         this.throwStateError("A nether forrest vegetation feature needs at least one BlockState");
      }

      if (this.stateProvider == null) {
         this.stateProvider = new WeightedStateProvider(this.blocks.build());
      }

      PlacedFeature spread = new PlacedFeature(
         Holder.direct(new SimpleBlockFeature(this.stateProvider)),
         List.of(
            BlockPredicateFilter.forPredicate(ON_NYLIUM),
            CountPlacement.of(this.spreadWidth * this.spreadWidth),
            OffsetPlacement.ofTriangle(this.spreadWidth - 1, this.spreadHeight - 1),
            BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
         )
      );
      return new OverlayFeature(HolderSet.direct(new Holder[]{Holder.direct(spread)}));
   }

   public static class Key extends FeatureKey<NetherForrestVegetation> {
      public Key(Identifier id) {
         super(id);
      }

      public NetherForrestVegetation bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new NetherForrestVegetationImpl(ctx, this.key);
      }
   }

   public static class KeyBonemeal extends FeatureKey<NetherForrestVegetation> {
      public KeyBonemeal(Identifier id) {
         super(id);
      }

      public NetherForrestVegetation bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new NetherForrestVegetationImpl(ctx, this.key).spreadHeight(1).spreadWidth(3);
      }
   }
}
