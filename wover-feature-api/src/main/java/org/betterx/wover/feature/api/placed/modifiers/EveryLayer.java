package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.block.api.BlockHelper;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public class EveryLayer implements PlacementModifier {
   private static final EveryLayer INSTANCE = new EveryLayer(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
   private static final EveryLayer INSTANCE_MIN_4 = new EveryLayer(4, Integer.MAX_VALUE, true);
   private static final EveryLayer UNDER_INSTANCE = new EveryLayer(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
   private static final EveryLayer UNDER_INSTANCE_MIN_4 = new EveryLayer(4, Integer.MAX_VALUE, false);
   public static final MapCodec<EveryLayer> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(o -> o.minHeight),
            Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(o -> o.maxHeight),
            Codec.BOOL.optionalFieldOf("top", true).forGetter(o -> o.onTop)
         )
         .apply(instance, EveryLayer::new)
   );
   @Internal
   public static final MapCodec<EveryLayer> CODEC_LEGACY_UNDER = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(o -> o.minHeight),
            Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(o -> o.maxHeight),
            Codec.BOOL.optionalFieldOf("top", false).forGetter(o -> o.onTop)
         )
         .apply(instance, EveryLayer::new)
   );
   private final int minHeight;
   private final int maxHeight;
   private final boolean onTop;

   private EveryLayer(int minHeight, int maxHeight, boolean onTop) {
      this.minHeight = minHeight;
      this.maxHeight = maxHeight;
      this.onTop = onTop;
   }

   public static EveryLayer on() {
      return INSTANCE;
   }

   public static EveryLayer onTopMin4() {
      return INSTANCE_MIN_4;
   }

   public static EveryLayer onTopInRange(int minHeight, int maxHeight) {
      return new EveryLayer(minHeight, maxHeight, true);
   }

   public static EveryLayer underneath() {
      return UNDER_INSTANCE;
   }

   public static EveryLayer underneathMin4() {
      return UNDER_INSTANCE_MIN_4;
   }

   public static EveryLayer underneathInRange(int minHeight, int maxHeight) {
      return new EveryLayer(minHeight, maxHeight, false);
   }

   public void modify(PlacementContext ctx, RandomSource random, BlockPos pos, Consumer<BlockPos> consumer) {
      int z = pos.getZ();
      int x = pos.getX();
      int levelHeight = ctx.getHeight(Types.MOTION_BLOCKING, x, z);
      int minLevelHeight = ctx.getMinY();
      int y = Math.min(levelHeight, this.maxHeight);
      int minHeight = Math.max(minLevelHeight, this.minHeight);

      int layerY;
      do {
         layerY = this.onTop ? findOnGroundYPosition(ctx, x, y, z, minHeight) : findUnderGroundYPosition(ctx, x, y, z, minHeight);
         if (layerY != Integer.MAX_VALUE) {
            consumer.accept(new BlockPos(x, layerY, z));
            y = layerY - 1;
         }
      } while (layerY != Integer.MAX_VALUE);
   }

   @NotNull
   public MapCodec<EveryLayer> codec() {
      return CODEC;
   }

   private static int findOnGroundYPosition(PlacementContext ctx, int x, int startY, int z, int minHeight) {
      MutableBlockPos mPos = new MutableBlockPos(x, startY, z);
      BlockState nowState = ctx.getBlockState(mPos);

      for (int y = startY; y >= minHeight + 1; y--) {
         mPos.setY(y - 1);
         BlockState belowState = ctx.getBlockState(mPos);
         if (BlockHelper.isTerrain(belowState) && BlockHelper.isFreeOrFluid(nowState) && !belowState.is(Blocks.BEDROCK)) {
            return mPos.getY() + 1;
         }

         nowState = belowState;
      }

      return Integer.MAX_VALUE;
   }

   private static int findUnderGroundYPosition(PlacementContext ctx, int x, int startY, int z, int minHeight) {
      MutableBlockPos mPos = new MutableBlockPos(x, startY, z);
      BlockState nowState = ctx.getBlockState(mPos);

      for (int y = startY; y >= minHeight + 1; y--) {
         mPos.setY(y - 1);
         BlockState belowState = ctx.getBlockState(mPos);
         if (BlockHelper.isTerrain(nowState) && BlockHelper.isFreeOrFluid(belowState) && !nowState.is(Blocks.BEDROCK)) {
            return mPos.getY();
         }

         nowState = belowState;
      }

      return Integer.MAX_VALUE;
   }
}
