package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.math.api.valueproviders.Vec3iProvider;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class OffsetProvider implements PlacementModifier {
   public static final MapCodec<OffsetProvider> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(Vec3iProvider.codec(-16, 16).fieldOf("offset").forGetter(cfg -> cfg.offset)).apply(instance, OffsetProvider::new)
   );
   private final Vec3iProvider offset;

   public OffsetProvider(Vec3iProvider offset) {
      this.offset = offset;
   }

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      consumer.accept(blockPos.offset(this.offset.sample(randomSource)));
   }

   @NotNull
   public MapCodec<OffsetProvider> codec() {
      return CODEC;
   }
}
