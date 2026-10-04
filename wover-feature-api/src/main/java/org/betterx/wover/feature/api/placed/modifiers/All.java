package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class All implements PlacementModifier {
   private static final All INSTANCE = new All();
   public static final MapCodec<All> CODEC = MapCodec.unit(All::new);

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      for (int i = 0; i < 255; i++) {
         consumer.accept(blockPos.offset(i & 15, 0, i >> 4));
      }
   }

   public static PlacementModifier simple() {
      return INSTANCE;
   }

   @NotNull
   public MapCodec<All> codec() {
      return CODEC;
   }
}
