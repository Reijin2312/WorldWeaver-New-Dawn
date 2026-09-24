package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class ExtendXYZ implements PlacementModifier {
   public static final MapCodec<ExtendXYZ> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            IntProviders.codec(0, 16).fieldOf("radius").forGetter(cfg -> cfg.radius),
            FloatProviders.codec(0.0F, 2.0F).optionalFieldOf("center_density", ConstantFloat.of(1.0F)).forGetter(cfg -> cfg.centerDensity),
            FloatProviders.codec(0.0F, 2.0F).optionalFieldOf("border_density", ConstantFloat.of(0.05F)).forGetter(cfg -> cfg.borderDensity),
            Codec.BOOL.optionalFieldOf("square", false).forGetter(cfg -> cfg.square),
            FloatProviders.codec(0.0F, 30.0F).optionalFieldOf("height", ConstantFloat.of(1.0F)).forGetter(cfg -> cfg.heightScale),
            ExtendXYZ.HeightPropagation.CODEC.optionalFieldOf("height_propagation", ExtendXYZ.HeightPropagation.NONE).forGetter(cfg -> cfg.heightPropagation)
         )
         .apply(instance, ExtendXYZ::new)
   );
   private final IntProvider radius;
   private final FloatProvider centerDensity;
   private final FloatProvider borderDensity;
   private final boolean square;
   private final FloatProvider heightScale;
   private final ExtendXYZ.HeightPropagation heightPropagation;

   public ExtendXYZ(
      IntProvider radius,
      FloatProvider centerDensity,
      FloatProvider borderDensity,
      boolean square,
      FloatProvider heightScale,
      ExtendXYZ.HeightPropagation heightPropagation
   ) {
      this.radius = radius;
      this.centerDensity = centerDensity;
      this.borderDensity = borderDensity;
      this.square = square;
      this.heightScale = heightScale;
      this.heightPropagation = heightPropagation;
   }

   public ExtendXYZ(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity, boolean square) {
      this(radius, centerDensity, borderDensity, square, ConstantFloat.of(1.0F), ExtendXYZ.HeightPropagation.NONE);
   }

   public static ExtendXYZ circle(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity) {
      return new ExtendXYZ(radius, centerDensity, borderDensity, false);
   }

   public static ExtendXYZ spikedCircle(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity, FloatProvider heightScale) {
      return new ExtendXYZ(radius, centerDensity, borderDensity, false, heightScale, ExtendXYZ.HeightPropagation.SPIKES_DOWN);
   }

   public static ExtendXYZ square(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity) {
      return new ExtendXYZ(radius, centerDensity, borderDensity, true);
   }

   private static void propagateSphere(
      RandomSource randomSource,
      Consumer<BlockPos> consumer,
      float maxR2,
      int maxDepth,
      int scale,
      float d0,
      float d1,
      float currentR2,
      float currentDensity,
      BlockPos pos,
      boolean didAdd
   ) {
      int depth = (int)(maxDepth * (1.0F - currentR2 / maxR2));
      propagateDown(randomSource, consumer, d0, d1, currentDensity, pos, depth, scale);
   }

   private static void propagateSquare(
      RandomSource randomSource,
      Consumer<BlockPos> consumer,
      float maxR2,
      int maxDepth,
      int scale,
      float d0,
      float d1,
      float currentR2,
      float currentDensity,
      BlockPos pos,
      boolean didAdd
   ) {
      propagateDown(randomSource, consumer, d0, d1, currentDensity, pos, maxDepth, scale);
   }

   private static void propagateSpikesConnected(
      RandomSource randomSource,
      Consumer<BlockPos> consumer,
      float maxR2,
      int maxDepth,
      int scale,
      float d0,
      float d1,
      float currentR2,
      float currentDensity,
      BlockPos pos,
      boolean didAdd
   ) {
      if (didAdd) {
         propagateDownConnected(randomSource, consumer, d0, d1, currentDensity * currentDensity, pos, maxDepth, scale);
      }
   }

   private static void propagateDownConnected(
      RandomSource randomSource, Consumer<BlockPos> consumer, float d0, float d1, float currentDensity, BlockPos pos, int depth, int scale
   ) {
      int depth2 = depth * depth;

      for (int y = 1; y <= depth; y++) {
         float lambda = (float)(y * y) / depth2;
         float densityDown = (d0 + (d1 - d0) * lambda) * currentDensity;
         if (densityDown <= 0.001) {
            return;
         }

         if (randomSource.nextFloat() > densityDown) {
            return;
         }

         consumer.accept(pos.above(scale * y));
      }
   }

   private static void propagateDown(
      RandomSource randomSource, Consumer<BlockPos> consumer, float d0, float d1, float currentDensity, BlockPos pos, int depth, int scale
   ) {
      int depth2 = depth * depth;

      for (int y = 1; y <= depth; y++) {
         float lambda = (float)(y * y) / depth2;
         float densityDown = (d0 + (d1 - d0) * lambda) * currentDensity;
         if (!(densityDown <= 0.001) && !(randomSource.nextFloat() > densityDown)) {
            consumer.accept(pos.above(y * scale));
         }
      }
   }

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      int r = this.radius.sample(randomSource);
      float r2 = r * r;
      float d0 = this.centerDensity.sample(randomSource);
      float d1 = this.borderDensity.sample(randomSource);
      int height = (int)(r * this.heightScale.sample(randomSource));

      for (int x = -r; x <= r; x++) {
         for (int z = -r; z <= r; z++) {
            float currentR2 = x * x + z * z;
            if (this.square || !(currentR2 > r2)) {
               float lambda = currentR2 / r2;
               float density = d0 + (d1 - d0) * lambda;
               BlockPos now = blockPos.offset(x, 0, z);
               if (!(density >= 1.0F) && (!(density > 0.001F) || !(density >= randomSource.nextFloat()))) {
                  this.heightPropagation
                     .propagationFunction
                     .propagate(randomSource, consumer, r2, height, this.heightPropagation.scale, d0, d1, currentR2, density, now, false);
               } else {
                  consumer.accept(now);
                  this.heightPropagation
                     .propagationFunction
                     .propagate(randomSource, consumer, r2, height, this.heightPropagation.scale, d0, d1, currentR2, density, now, true);
               }
            }
         }
      }
   }

   @NotNull
   public MapCodec<ExtendXYZ> codec() {
      return CODEC;
   }

   public static enum HeightPropagation implements StringRepresentable {
      NONE(1, (randomSource, builder, maxR2, maxDepth, scale1, d0, d1, currentR2, density, pos, didAdd) -> {}),
      BOX_DOWN(-1, ExtendXYZ::propagateSquare),
      BOX_UP(1, ExtendXYZ::propagateSquare),
      SPHERE_DOWN(-1, ExtendXYZ::propagateSphere),
      SPHERE_UP(1, ExtendXYZ::propagateSphere),
      SPIKES_DOWN(-1, ExtendXYZ::propagateSpikesConnected),
      SPIKES_UP(1, ExtendXYZ::propagateSpikesConnected);

      public static final Codec<ExtendXYZ.HeightPropagation> CODEC = StringRepresentable.fromEnum(ExtendXYZ.HeightPropagation::values);
      private final int scale;
      private final ExtendXYZ.PropagationFunction propagationFunction;

      private HeightPropagation(int scale, ExtendXYZ.PropagationFunction propagationFunction) {
         this.scale = scale;
         this.propagationFunction = propagationFunction;
      }

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase();
      }
   }

   private interface PropagationFunction {
      void propagate(
         RandomSource var1,
         Consumer<BlockPos> var2,
         float var3,
         int var4,
         int var5,
         float var6,
         float var7,
         float var8,
         float var9,
         BlockPos var10,
         boolean var11
      );
   }
}
