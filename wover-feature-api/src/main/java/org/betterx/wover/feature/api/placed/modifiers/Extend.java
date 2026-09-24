package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class Extend implements PlacementModifier {
   public static final MapCodec<Extend> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Direction.CODEC.fieldOf("direction").orElse(Direction.DOWN).forGetter(cfg -> cfg.direction),
            IntProviders.codec(0, 16).fieldOf("length").orElse(UniformInt.of(0, 3)).forGetter(cfg -> cfg.length)
         )
         .apply(instance, Extend::new)
   );
   private final Direction direction;
   private final IntProvider length;

   public Extend(Direction direction, IntProvider length) {
      this.direction = direction;
      this.length = length;
   }

   public void modify(PlacementContext placementContext, RandomSource random, BlockPos blockPos, Consumer<BlockPos> consumer) {
      int count = this.length.sample(random);
      consumer.accept(blockPos);

      for (int y = 1; y < count + 1; y++) {
         consumer.accept(blockPos.relative(this.direction, y));
      }
   }

   @NotNull
   public MapCodec<Extend> codec() {
      return CODEC;
   }
}
