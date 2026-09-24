package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class Merge implements PlacementModifier {
   public static final MapCodec<Merge> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ExtraCodecs.nonEmptyList(PlacementModifier.CODEC.listOf()).fieldOf("modifiers").forGetter(a -> a.modifiers))
         .apply(instance, Merge::new)
   );
   private final List<PlacementModifier> modifiers;

   public Merge(List<PlacementModifier> subModifiers) {
      this.modifiers = subModifiers;
   }

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      for (PlacementModifier p : this.modifiers) {
         p.modify(placementContext, randomSource, blockPos, consumer);
      }
   }

   @NotNull
   public MapCodec<Merge> codec() {
      return CODEC;
   }
}
