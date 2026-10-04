package org.betterx.wover.feature.api.features;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedList.Builder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public abstract class PlaceBlockFeature implements Feature {
   protected final Holder<BlockStateProvider> stateProvider;

   protected static <T extends PlaceBlockFeature> RecordCodecBuilder<T, Holder<BlockStateProvider>> blockStateCodec() {
      return BlockStateProvider.CODEC.fieldOf("entries").forGetter(o -> o.stateProvider);
   }

   protected static WeightedList<BlockState> buildWeightedList(List<BlockState> states) {
      Builder<BlockState> builder = WeightedList.builder();

      for (BlockState s : states) {
         builder.add(s, 1);
      }

      return builder.build();
   }

   protected static WeightedList<BlockState> buildWeightedList(BlockState state) {
      return WeightedList.<BlockState>builder().add(state, 1).build();
   }

   public PlaceBlockFeature(Block block) {
      this(block.defaultBlockState());
   }

   public PlaceBlockFeature(BlockState state) {
      this(BlockStateProvider.of(state));
   }

   public PlaceBlockFeature(List<BlockState> states) {
      this(buildWeightedList(states));
   }

   public PlaceBlockFeature(WeightedList<BlockState> blocks) {
      this.stateProvider = Holder.direct(new WeightedStateProvider(blocks));
   }

   public PlaceBlockFeature(BlockStateProvider blocks) {
      this(Holder.direct(blocks));
   }

   public PlaceBlockFeature(Holder<BlockStateProvider> blocks) {
      this.stateProvider = blocks;
   }

   public BlockState getRandomBlock(WorldGenLevel level, RandomSource random, BlockPos pos) {
      return this.stateProvider.value().getState(level, random, pos);
   }

   public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
      BlockState state = this.getRandomBlock(level, random, pos);
      return this.placeBlock(level, pos, state);
   }

   protected abstract boolean placeBlock(WorldGenLevel var1, BlockPos var2, BlockState var3);
}
