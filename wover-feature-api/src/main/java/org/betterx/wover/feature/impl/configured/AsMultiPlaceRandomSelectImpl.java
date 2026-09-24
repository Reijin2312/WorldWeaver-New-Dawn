package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsMultiPlaceRandomSelect;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import org.betterx.wover.util.Triple;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedList.Builder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsMultiPlaceRandomSelectImpl extends FeatureConfiguratorImpl implements AsMultiPlaceRandomSelect {
   private final List<Triple<BlockStateProvider, Float, Integer>> features = new LinkedList<>();
   private AsMultiPlaceRandomSelect.Placer modFunction;
   private static int lastID = 0;

   AsMultiPlaceRandomSelectImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public AsMultiPlaceRandomSelect addAllStates(Block block, int weight) {
      return this.addAllStates(block, weight, lastID + 1);
   }

   @Override
   public AsMultiPlaceRandomSelect addAll(int weight, Block... blockSet) {
      return this.addAll(weight, lastID + 1, blockSet);
   }

   @Override
   public AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight) {
      return this.addAllStatesFor(prop, block, weight, lastID + 1);
   }

   @Override
   public AsMultiPlaceRandomSelect add(Block block, float weight) {
      return this.add(BlockStateProvider.of(block), weight);
   }

   @Override
   public AsMultiPlaceRandomSelect add(BlockState state, float weight) {
      return this.add(BlockStateProvider.of(state), weight);
   }

   @Override
   public AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight) {
      return this.add(provider, weight, lastID + 1);
   }

   @Override
   public AsMultiPlaceRandomSelect addAllStates(Block block, int weight, int id) {
      Set<BlockState> states = BlockHelper.getPossibleStates(block);
      Builder<BlockState> builder = WeightedList.builder();
      states.forEach(s -> builder.add(block.defaultBlockState(), 1));
      this.add(new WeightedStateProvider(builder.build()), (float)weight, id);
      return this;
   }

   @Override
   public AsMultiPlaceRandomSelect addAll(int weight, int id, Block... blocks) {
      Builder<BlockState> builder = WeightedList.builder();

      for (Block block : blocks) {
         builder.add(block.defaultBlockState(), 1);
      }

      this.add(new WeightedStateProvider(builder.build()), (float)weight, id);
      return this;
   }

   @Override
   public AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight, int id) {
      Collection<Integer> values = prop.getPossibleValues();
      Builder<BlockState> builder = WeightedList.builder();
      values.forEach(s -> builder.add((BlockState)block.defaultBlockState().setValue(prop, s), 1));
      this.add(new WeightedStateProvider(builder.build()), (float)weight, id);
      return this;
   }

   @Override
   public AsMultiPlaceRandomSelect add(Block block, float weight, int id) {
      return this.add(BlockStateProvider.of(block), weight, id);
   }

   @Override
   public AsMultiPlaceRandomSelect add(BlockState state, float weight, int id) {
      return this.add(BlockStateProvider.of(state), weight, id);
   }

   @Override
   public AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight, int id) {
      this.features.add(new Triple(provider, weight, id));
      lastID = Math.max(lastID, id);
      return this;
   }

   @Override
   public AsMultiPlaceRandomSelect placement(AsMultiPlaceRandomSelect.Placer placer) {
      this.modFunction = placer;
      return this;
   }

   private Holder<PlacedFeature> place(BlockStateProvider p, int id) {
      FeaturePlacementBuilder builder = FeatureContentManager.INLINE_BUILDER.simple().block(p).inlinePlace();
      return this.modFunction.place(builder, id);
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.modFunction == null) {
         this.throwStateError("AsMultiPlaceRandomSelect needs a placement.modification Function");
      }

      float sum = this.features.stream().map(p -> (Float)p.second).reduce(0.0F, Float::sum);
      List<WeightedPlacedFeature> features = this.features
         .stream()
         .map(p -> new WeightedPlacedFeature(this.place((BlockStateProvider)p.first, (Integer)p.third), (Float)p.second / sum))
         .toList();
      return new RandomSelectorFeature(features.subList(0, features.size() - 1), features.get(features.size() - 1).feature());
   }

   public static class Key extends FeatureKey<AsMultiPlaceRandomSelect> {
      public Key(Identifier id) {
         super(id);
      }

      public AsMultiPlaceRandomSelect bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new AsMultiPlaceRandomSelectImpl(ctx, this.key);
      }
   }
}
