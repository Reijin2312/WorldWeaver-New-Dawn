package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.math.api.MathHelper;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;

import java.util.Objects;

public final class RandomIntProvider implements NumericProvider {
    public static final MapCodec<RandomIntProvider> CODEC = Codec
            .INT.fieldOf("range")
                .xmap(RandomIntProvider::new, obj -> obj.range);
    public final int range;
    private final int seed;


    RandomIntProvider(int range) {
        this.range = range;
        seed = (int) MathHelper.getSeed(range);
    }

    public static RandomIntProvider max(int range) {
        return new RandomIntProvider(range);
    }

    @Override
    public int getNumber(SurfaceRulesContext context) {
        return RandomSource.create(MathHelper.getSeed(seed, context.getBlockX(), context.getBlockY(), context.getBlockZ())).nextInt(range);
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RandomIntProvider) obj;
        return this.range == that.range;
    }

    @Override
    public int hashCode() {
        return Objects.hash(range);
    }

    @Override
    public String toString() {
        return "RandomIntProvider[" +
                "range=" + range + ']';
    }
}
