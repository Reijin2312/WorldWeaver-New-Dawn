package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.entrypoint.LibWoverFeature;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class Debug implements PlacementModifier {
   public static final Debug INSTANCE = new Debug("Placing at {}");
   public static final MapCodec<Debug> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(Codec.STRING.fieldOf("caption").orElse("Placing at {}").forGetter(cfg -> cfg.caption)).apply(instance, Debug::new)
   );
   private final String caption;

   public Debug(String caption) {
      this.caption = caption;
   }

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      LibWoverFeature.C.log.info(this.caption, new Object[]{blockPos});
      consumer.accept(blockPos);
   }

   @NotNull
   public MapCodec<Debug> codec() {
      return CODEC;
   }
}
