package org.betterx.wover.surface.api.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

import java.util.function.Function;


/**
 * Interface for Number Providers registered with the {@link NumericProviderRegistry}.
 */
public interface NumericProvider {
    /**
     * Codec for a Numeric Providers that delegates to the
     * Codec returned by {@link #pcodec()}.
     */
    Codec<NumericProvider> CODEC = NumericProviderRegistry
            .NUMERIC_PROVIDER.byNameCodec()
                             .dispatch(
                                     NumericProvider::pcodec,
                                     Function.identity()
                             );

    /**
     * Get the next number for the given context.
     *
     * @param context The context.
     * @return The resulting value.
     */
    int getNumber(MaterialRuleContext context);

    /**
     * Get the codec for this type Numeric Provider.
     *
     * @return The codec.
     */
    MapCodec<? extends NumericProvider> pcodec();
}
