package org.betterx.wover.surface.impl.conditions;

import org.betterx.wover.math.api.noise.OpenSimplexNoise;
import org.betterx.wover.math.api.MathHelper;
import org.betterx.wover.surface.api.conditions.SurfaceNoiseCondition;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThresholdConditionImpl extends SurfaceNoiseCondition {
    private static final Map<Long, Context> NOISES = new ConcurrentHashMap<>();
    public static final MapCodec<ThresholdConditionImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Codec.LONG.fieldOf("seed").forGetter(p -> p.noiseContext.seed),
                    Codec.DOUBLE.fieldOf("threshold").orElse(0.0).forGetter(p -> p.threshold),
                    FloatProviders.CODEC.fieldOf("roughness").orElse(ConstantFloat.of(0)).forGetter(p -> p.roughness),
                    Codec.DOUBLE.fieldOf("scale_x").orElse(0.1).forGetter(p -> p.scaleX),
                    Codec.DOUBLE.fieldOf("scale_z").orElse(0.1).forGetter(p -> p.scaleZ)
            )
            .apply(instance, ThresholdConditionImpl::new));
    private final Context noiseContext;
    private final double threshold;
    private final FloatProvider roughness;
    private final double scaleX;
    private final double scaleZ;

    public ThresholdConditionImpl(
            long noiseSeed,
            double threshold,
            FloatProvider roughness,
            double scaleX,
            double scaleZ
    ) {
        this.threshold = threshold;
        this.roughness = roughness;
        this.scaleX = scaleX;
        this.scaleZ = scaleZ;

        noiseContext = NOISES.computeIfAbsent(noiseSeed, Context::new);
    }

    @Override
    public boolean test(SurfaceRulesContext context) {
        final int x = context.getBlockX(), z = context.getBlockZ();
        return noiseContext.eval(x * scaleX, z * scaleZ)
                + roughness.sample(noiseContext.randomAt(x, z)) > threshold;
    }

    @Override
    public MapCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    static class Context {
        public final OpenSimplexNoise noise;
        public final long seed;
        private final ThreadLocal<double[]> memo = ThreadLocal.withInitial(() -> new double[]{Double.NaN, Double.NaN, 0});

        Context(long seed) {
            this.seed = seed;
            this.noise = new OpenSimplexNoise(seed);
        }
        double eval(double x, double z) {
            double[] last = memo.get();
            if (last[0] == x && last[1] == z) return last[2];
            double value = noise.eval(x, z);
            last[0] = x; last[1] = z; last[2] = value;
            return value;
        }
        RandomSource randomAt(int x, int z) {
            return RandomSource.create(MathHelper.getSeed(Long.hashCode(seed), x, 0, z));
        }
    }
}
