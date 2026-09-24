package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class Stencil implements PlacementModifier {
   public static final MapCodec<Stencil> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ExtraCodecs.nonEmptyList(Codec.BOOL.listOf()).fieldOf("structures").orElse(convert(Stencil.STENCIL)).forGetter(a -> a.stencil),
            Codec.INT.fieldOf("one_in").orElse(1).forGetter(a -> a.selectOneIn)
         )
         .apply(instance, Stencil::new)
   );
   private static final Boolean[] STENCIL = new Boolean[]{
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      true,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      true,
      false,
      false,
      true,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      false,
      false,
      false,
      true,
      false,
      false,
      true,
      true,
      true,
      false,
      false,
      true,
      true,
      true,
      true,
      false,
      true,
      true,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      false,
      false,
      true,
      true,
      true,
      false,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      true,
      true,
      true,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      false,
      true,
      true,
      true,
      true,
      true,
      true,
      true,
      false,
      false,
      true,
      true,
      false,
      true,
      true,
      false,
      false,
      false,
      true,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      true,
      true,
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      true,
      true,
      false,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      true,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      true,
      true,
      false,
      false,
      true,
      false,
      false,
      false,
      true,
      true,
      true,
      true,
      true,
      true,
      false,
      true,
      false,
      true,
      true,
      true,
      true,
      true,
      true,
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      true,
      true,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      true,
      true,
      false,
      false
   };
   private static final Stencil DEFAULT = new Stencil(STENCIL, 1);
   private static final Stencil DEFAULT4 = new Stencil(STENCIL, 4);
   private final List<Boolean> stencil;
   private final int selectOneIn;

   private static List<Boolean> convert(Boolean[] s) {
      return Arrays.stream(s).toList();
   }

   public Stencil(Boolean[] stencil, int selectOneIn) {
      this(convert(stencil), selectOneIn);
   }

   private Stencil(List<Boolean> stencil, int selectOneIn) {
      if (stencil.size() != 256) {
         throw new IllegalArgumentException("Stencil must be 16x16");
      } else {
         this.stencil = stencil;
         this.selectOneIn = selectOneIn;
      }
   }

   public static Stencil all() {
      return DEFAULT;
   }

   public static Stencil oneIn4() {
      return DEFAULT4;
   }

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      for (int x = 0; x < 16; x++) {
         for (int y = 0; y < 16; y++) {
            if (this.stencil.get(x << 4 | y) && (this.selectOneIn <= 1 || randomSource.nextInt(this.selectOneIn) == 0)) {
               consumer.accept(blockPos.offset(x, 0, y));
            }
         }
      }
   }

   @NotNull
   public MapCodec<Stencil> codec() {
      return CODEC;
   }
}
