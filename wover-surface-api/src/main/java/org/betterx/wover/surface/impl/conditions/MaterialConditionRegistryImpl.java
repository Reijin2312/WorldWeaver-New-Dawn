package org.betterx.wover.surface.impl.conditions;

import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.LibWoverSurface;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.surface.api.conditions.ConditionManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MaterialConditionRegistryImpl {
    public static final ResourceKey<MapCodec<? extends MaterialCondition>> THRESHOLD_CONDITION
            = ConditionManager.createKey(LibWoverSurface.C.id("threshold_condition"));
    public static final ResourceKey<MapCodec<? extends MaterialCondition>> VOLUME_THRESHOLD_CONDITION
            = ConditionManager.createKey(LibWoverSurface.C.id("volume_threshold_condition"));
    public static final ResourceKey<MapCodec<? extends MaterialCondition>> ROUGH_NOISE_CONDITION
            = ConditionManager.createKey(LibWoverSurface.C.id("rough_noise_condition"));

    public static ResourceKey<MapCodec<? extends MaterialCondition>> register(
            ResourceKey<MapCodec<? extends MaterialCondition>> key,
            MapCodec<? extends MaterialCondition> codec,
            boolean withBCLibLegacy
    ) {
        BuiltInRegistryManager.register(BuiltInRegistries.MATERIAL_CONDITION_TYPE, key, codec);
        if (withBCLibLegacy && LegacyHelper.isLegacyEnabled()) {
            BuiltInRegistryManager.register(
                    BuiltInRegistries.MATERIAL_CONDITION_TYPE,
                    LegacyHelper.BCLIB_CORE.convertNamespace(key.identifier()),
                    LegacyHelper.wrap(codec)
            );
        }

        return key;
    }

    @NotNull
    public static ResourceKey<MapCodec<? extends MaterialCondition>> createKey(Identifier location) {
        return ResourceKey.create(
                BuiltInRegistries.MATERIAL_CONDITION_TYPE.key(),
                location
        );
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        register(THRESHOLD_CONDITION, ThresholdConditionImpl.CODEC, true);
        register(VOLUME_THRESHOLD_CONDITION, VolumeThresholdConditionImpl.CODEC, true);
        register(ROUGH_NOISE_CONDITION, RoughNoiseConditionImpl.CODEC, true);
    }
}
