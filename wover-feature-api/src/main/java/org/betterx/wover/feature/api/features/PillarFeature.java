package org.betterx.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.NotNull;

public class PillarFeature implements Feature {
   public static final MapCodec<PillarFeature> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            IntProviders.CODEC.fieldOf("min_height").forGetter(o -> o.minHeight),
            IntProviders.CODEC.fieldOf("max_height").forGetter(o -> o.maxHeight),
            Direction.CODEC.fieldOf("direction").orElse(Direction.UP).forGetter(o -> o.direction),
            BlockPredicate.CODEC.fieldOf("allowed_placement").forGetter(o -> o.allowedPlacement),
            BlockStateProvider.CODEC.fieldOf("state").forGetter(o -> o.stateProvider),
            PillarFeature.KnownTransformers.CODEC.fieldOf("transform").forGetter(o -> o.transformer)
         )
         .apply(instance, PillarFeature::new)
   );
   public final IntProvider maxHeight;
   public final IntProvider minHeight;
   public final Holder<BlockStateProvider> stateProvider;
   public final PillarFeature.KnownTransformers transformer;
   public final Direction direction;
   public final BlockPredicate allowedPlacement;

   public PillarFeature(
      IntProvider minHeight,
      IntProvider maxHeight,
      Direction direction,
      BlockPredicate allowedPlacement,
      Holder<BlockStateProvider> stateProvider,
      PillarFeature.KnownTransformers transformer
   ) {
      this.minHeight = minHeight;
      this.maxHeight = maxHeight;
      this.stateProvider = stateProvider;
      this.transformer = transformer;
      this.direction = direction;
      this.allowedPlacement = allowedPlacement;
   }

   public MapCodec<PillarFeature> codec() {
      return CODEC;
   }

   public BlockState transform(int currentHeight, int maxHeight, BlockPos pos, RandomSource rnd, WorldGenLevel level) {
      BlockState state = this.stateProvider.value().getState(level, rnd, pos);
      return this.transformer.stateTransform.apply(currentHeight, maxHeight, state, pos, rnd);
   }

   public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource rnd, BlockPos origin) {
      int maxHeight = this.maxHeight.sample(rnd);
      int minHeight = this.minHeight.sample(rnd);
      MutableBlockPos posnow = origin.mutable();
      posnow.move(this.direction);

      for (int height = 1; height < maxHeight; height++) {
         if (!this.allowedPlacement.test(level, posnow)) {
            maxHeight = height;
            break;
         }

         posnow.move(this.direction);
      }

      if (maxHeight < minHeight) {
         return false;
      } else if (!this.transformer.canPlace.at(minHeight, maxHeight, origin, posnow, level, this.allowedPlacement, rnd)) {
         return false;
      } else {
         posnow = origin.mutable();

         for (int var10 = 0; var10 < maxHeight; var10++) {
            BlockState state = this.transform(var10, maxHeight - 1, posnow, rnd, level);
            level.setBlock(posnow, state, 18);
            posnow.move(this.direction);
         }

         return true;
      }
   }

   public static enum KnownTransformers implements StringRepresentable {
      SIZE_DECREASE(
         "size_decrease",
         (height, maxHeight, state, pos, rnd) -> (BlockState)state.setValue(BlockProperties.SIZE, Math.max(0, Math.min(7, maxHeight - height)))
      ),
      SIZE_INCREASE("size_increase", (height, maxHeight, state, pos, rnd) -> (BlockState)state.setValue(BlockProperties.SIZE, Math.max(0, Math.min(7, height)))),
      BOTTOM_GROW("bottom_grow", (height, maxHeight, state, pos, rnd) -> (BlockState)state.setValue(BlockProperties.BOTTOM, height == maxHeight)),
      BOTTOM("bottom", (height, maxHeight, state, pos, rnd) -> (BlockState)state.setValue(BlockProperties.BOTTOM, height == 0)),
      TRIPLE_SHAPE_FILL(
         "triple_shape_fill",
         (height, maxHeight, state, pos, rnd) -> {
            if (height == 0) {
               return (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM);
            } else {
               return height == maxHeight
                  ? (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP)
                  : (BlockState)state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE);
            }
         },
         (minHeight, maxHeight, startPos, aboveTop, level, allow, rnd) -> !allow.test(level, aboveTop)
      );

      public static final EnumCodec<PillarFeature.KnownTransformers> CODEC = StringRepresentable.fromEnum(PillarFeature.KnownTransformers::values);
      public final String name;
      public final PillarFeature.StateTransform stateTransform;
      public final PillarFeature.PlacePredicate canPlace;

      private KnownTransformers(String name, PillarFeature.StateTransform stateTransform) {
         this(name, stateTransform, PillarFeature.PlacePredicate.ALLWAYS);
      }

      private KnownTransformers(String name, PillarFeature.StateTransform stateTransform, PillarFeature.PlacePredicate canPlace) {
         this.name = name;
         this.stateTransform = stateTransform;
         this.canPlace = canPlace;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }
   }

   @FunctionalInterface
   public interface PlacePredicate {
      PillarFeature.PlacePredicate ALLWAYS = (min, max, start, above, level, allow, rnd) -> true;

      boolean at(int var1, int var2, BlockPos var3, BlockPos var4, WorldGenLevel var5, BlockPredicate var6, RandomSource var7);
   }

   @FunctionalInterface
   public interface StateTransform {
      BlockState apply(int var1, int var2, BlockState var3, BlockPos var4, RandomSource var5);
   }
}
