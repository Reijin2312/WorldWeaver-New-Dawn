package org.betterx.wover.biome.impl.data;

import org.betterx.wover.biome.api.data.BiomeCodecRegistry;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.LibWoverBiome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

public class BiomeCodecRegistryImpl {
    public static final Registry<MapCodec<? extends BiomeData>> BIOME_CODECS = BiomeCodecRegistry.BIOME_CODECS;

    public static final Codec<BiomeData> CODEC = BIOME_CODECS
            .byNameCodec()
            .dispatch(b -> b.codec().codec(), Function.identity());

    public static synchronized MapCodec<? extends BiomeData> register(
            Registry<MapCodec<? extends BiomeData>> registry,
            ResourceLocation location,
            KeyDispatchDataCodec<? extends BiomeData> keyDispatchDataCodec
    ) {
        return BuiltInRegistryManager.register(registry, location, keyDispatchDataCodec.codec());
    }

    @ApiStatus.Internal
    public static synchronized void initialize() {
        onBootstrap(BIOME_CODECS);
    }

    private static MapCodec<? extends BiomeData> onBootstrap(Registry<MapCodec<? extends BiomeData>> registry) {
        final var biomeData = LibWoverBiome.C.id("vanilla_data");
        if (registry.containsKey(biomeData)) {
            return registry.get(biomeData);
        }

        return register(registry, biomeData, BiomeData.KEY_CODEC);
    }
}
