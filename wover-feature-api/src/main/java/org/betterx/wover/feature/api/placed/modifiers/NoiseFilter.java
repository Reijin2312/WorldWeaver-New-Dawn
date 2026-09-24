package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;

public class NoiseFilter implements PlacementFilter {
   public static final MapCodec<NoiseFilter> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(o -> o.noise),
            Codec.DOUBLE.optionalFieldOf("min_noise_level", -Double.MAX_VALUE).forGetter(o -> o.minNoiseLevel),
            Codec.DOUBLE.optionalFieldOf("max_noise_level", Double.MAX_VALUE).forGetter(o -> o.maxNoiseLevel),
            Codec.FLOAT.optionalFieldOf("scale_xz", 1.0F).forGetter(o -> o.scaleXZ),
            Codec.FLOAT.optionalFieldOf("scale_y", 1.0F).forGetter(o -> o.scaleY)
         )
         .apply(instance, NoiseFilter::new)
   );
   private final ResourceKey<NormalNoise> noise;
   private final double minNoiseLevel;
   private final double maxNoiseLevel;
   private final float scaleXZ;
   private final float scaleY;

   public NoiseFilter(ResourceKey<NormalNoise> noise, double minNoiseLevel, double maxNoiseLevel, float scaleXZ, float scaleY) {
      this.noise = noise;
      this.minNoiseLevel = minNoiseLevel;
      this.maxNoiseLevel = maxNoiseLevel;
      this.scaleXZ = scaleXZ;
      this.scaleY = scaleY;
   }

   public boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
      RandomState randomState = ctx.getLevel().getLevel().getChunkSource().randomState();
      Noise normalNoise = randomState.getOrCreateNoise(this.noise);
      double v = normalNoise.get(pos.getX() * this.scaleXZ, pos.getY() * this.scaleY, pos.getZ() * this.scaleXZ);
      return v > this.minNoiseLevel && v < this.maxNoiseLevel;
   }

   @NotNull
   public MapCodec<NoiseFilter> codec() {
      return CODEC;
   }
}
