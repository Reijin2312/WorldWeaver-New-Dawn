package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.math.api.random.RandomHelper;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.conditions.VolumeThresholdCondition;
import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;

public class NetherNoiseCondition implements NumericProvider {
    /**
     * A simple scalar random number provider
     */
    public static final NumericProvider INSTANCE = new NetherNoiseCondition();
    public static final MapCodec<NetherNoiseCondition> CODEC = Codec
            .BYTE.fieldOf("nether_noise")
                 .xmap(
                         (obj) -> (NetherNoiseCondition) INSTANCE,
                         obj -> (byte) 0
                 );


    public NetherNoiseCondition() {
    }


    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

    @Override
    public int getNumber(SurfaceRulesContext context) {
        final int x = context.getBlockX();
        final int y = context.getBlockY();
        final int z = context.getBlockZ();
        final VolumeThresholdCondition noise = Conditions.NETHER_VOLUME_NOISE;
        double value = noise.getNoiseContext().getNoise().eval(x * noise.getScaleX(), y * noise.getScaleY(), z * noise.getScaleZ());
        final RandomSource random = noise.getNoiseContext().randomAt(x, y, z);
        int offset = random.nextInt(20) == 0 ? 3 : 0;


        float cmp = RandomHelper.inRange(random, 0.4F, 0.5F);
        if (value > cmp || value < -cmp) return 2 + offset;

        if (value > noise.getRoughness().sample(random))
            return 0 + offset;

        return 1 + offset;
    }
}
