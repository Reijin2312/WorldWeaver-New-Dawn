package org.betterx.wover.surface.impl.noise;

import org.betterx.wover.surface.api.noise.NoiseParameterManager;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.Noise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;

public class NoiseRegistryImpl {
    public static ResourceKey<NormalNoise> createKey(Identifier loc) {
        return ResourceKey.create(Registries.NOISE, loc);
    }

    private static Noise createNoise(
            Registry<NormalNoise> registry,
            RandomSource randomSource,
            ResourceKey<NormalNoise> resourceKey
    ) {
        Holder<NormalNoise> holder = registry.getOrThrow(resourceKey);
        return holder.value().create(randomSource);
    }

    private static final Map<ResourceKey<NormalNoise>, Noise> noiseIntances = new ConcurrentHashMap<>();

    public static Noise getOrCreateNoise(
            RegistryAccess registryAccess,
            RandomSource randomSource,
            ResourceKey<NormalNoise> noise
    ) {
        final Registry<NormalNoise> registry = registryAccess.lookupOrThrow(Registries.NOISE);
        return noiseIntances.computeIfAbsent(
                noise,
                (key) -> NoiseRegistryImpl.createNoise(registry, randomSource, noise)
        );
    }

    public static void register(
            BootstrapContext<NormalNoise> bootstapContext,
            ResourceKey<NormalNoise> resourceKey,
            int firstOctave,
            double firstAmplitude,
            double... amplitudes
    ) {
        int octaveCount = Math.max(1, amplitudes.length);
        NormalNoise.Builder builder = NormalNoise.builder()
                .setBaseOctave(firstOctave)
                .setBaseAmplitude(firstAmplitude)
                .setOctaveCount(octaveCount);
        for (int i = 0; i < amplitudes.length; i++) {
            builder.setAmplitudeModifier(i, amplitudes[i]);
        }
        bootstapContext.register(resourceKey, builder.build());
    }


    @ApiStatus.Internal
    public static void bootstrap(BootstrapContext<NormalNoise> bootstapContext) {
        register(bootstapContext, NoiseParameterManager.ROUGHNESS_NOISE, 2, 1.0D, 1.0, 1.0, 1.0, 1.0);
    }
}
