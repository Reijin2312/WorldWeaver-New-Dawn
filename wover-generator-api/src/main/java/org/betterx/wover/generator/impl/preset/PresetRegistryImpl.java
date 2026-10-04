package org.betterx.wover.generator.impl.preset;

import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.api.preset.WorldPresets;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.generator.impl.chunkgenerator.WoverChunkGenerator;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.preset.api.WorldPresetTags;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class PresetRegistryImpl {
    public static final ResourceKey<WorldPreset> WOVER_WORLD =
            WorldPresetManager.createKey(LibWoverWorldGenerator.C.id("normal"));
    public static final ResourceKey<WorldPreset> WOVER_WORLD_LARGE =
            WorldPresetManager.createKey(LibWoverWorldGenerator.C.id("large"));
    public static final ResourceKey<WorldPreset> WOVER_WORLD_AMPLIFIED =
            WorldPresetManager.createKey(LibWoverWorldGenerator.C.id("amplified"));
    public static final ResourceKey<WorldPreset> WOVER_WORLD_SUPERFLAT =
            WorldPresetManager.createKey(LibWoverWorldGenerator.C.id("superflat"));
    public static final ResourceKey<WorldPreset> BCL_WORLD_17 =
            WorldPresetManager.createKey(LegacyHelper.BCLIB_CORE.id("legacy_17"));

    public static void bootstrapWorldPresets(WorldPresetBootstrapContext ctx) {
        ctx.register(WorldPresets.WOVER_WORLD, createNormal(ctx));
        ctx.register(WorldPresets.WOVER_WORLD_LARGE, createLarge(ctx));
        ctx.register(WorldPresets.WOVER_WORLD_AMPLIFIED, createAmplified(ctx));
        ctx.register(WorldPresets.WOVER_WORLD_SUPERFLAT, createSuperflat(ctx));

        if (LegacyHelper.isLegacyEnabled()) {
            final ResourceKey<WorldPreset> legacyNormal = WorldPresetManager.createKey(
                    LegacyHelper.BCLIB_CORE.convertNamespace(WorldPresets.WOVER_WORLD));
            final ResourceKey<WorldPreset> legacyLarge = WorldPresetManager.createKey(
                    LegacyHelper.BCLIB_CORE.convertNamespace(WorldPresets.WOVER_WORLD_LARGE));
            final ResourceKey<WorldPreset> legacyAmplified = WorldPresetManager.createKey(
                    LegacyHelper.BCLIB_CORE.convertNamespace(WorldPresets.WOVER_WORLD_AMPLIFIED));

            ctx.register(BCL_WORLD_17, createLegacy(ctx));
            ctx.register(legacyNormal, createNormal(ctx));
            ctx.register(legacyLarge, createLarge(ctx));
            ctx.register(legacyAmplified, createAmplified(ctx));
        }
    }

    public static void bootstrapWorldPresetTags(TagBootstrapContext<WorldPreset> context) {
        context.addOptional(
                WorldPresetTags.NORMAL,
                WorldPresets.WOVER_WORLD,
                WorldPresets.WOVER_WORLD_AMPLIFIED,
                WorldPresets.WOVER_WORLD_LARGE,
                WorldPresets.WOVER_WORLD_SUPERFLAT
        );
    }

    @NotNull
    public static LevelStem makeWoverNetherStem(
            WorldPresetBootstrapContext.StemContext context,
            WoverNetherConfig config
    ) {
        return new LevelStem(
                context.dimension,
                new WoverChunkGenerator(new WoverNetherBiomeSource(config), context.generatorSettings)
        );
    }

    public static LevelStem makeWoverEndStem(
            WorldPresetBootstrapContext.StemContext context,
            WoverEndConfig config
    ) {
        return new LevelStem(
                context.dimension,
                new WoverChunkGenerator(new WoverEndBiomeSource(config), context.generatorSettings)
        );
    }

    private static WorldPreset createLegacy(WorldPresetBootstrapContext ctx) {
        return buildPreset(
                ctx.overworldStem,
                ctx.netherContext, WoverNetherConfig.MINECRAFT_17,
                ctx.endContext, WoverEndConfig.MINECRAFT_17
        );
    }

    private static WorldPreset createAmplified(WorldPresetBootstrapContext ctx) {
        Holder<NoiseGeneratorSettings> amplifiedSettings =
                ctx.noiseSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
        WorldPresetBootstrapContext.StemContext amplifiedNetherContext =
                WorldPresetBootstrapContext.StemContext.of(
                        ctx.netherContext.dimension,
                        ctx.netherContext.structureSets,
                        ctx.noiseSettings.getOrThrow(WoverChunkGenerator.AMPLIFIED_NETHER)
                );

        return buildPreset(
                ctx.makeNoiseBasedOverworld(ctx.overworldStem.generator().getBiomeSource(), amplifiedSettings),
                amplifiedNetherContext, WoverNetherConfig.MINECRAFT_18_AMPLIFIED,
                ctx.endContext, WoverEndConfig.MINECRAFT_20_AMPLIFIED
        );
    }

    private static WorldPreset createLarge(WorldPresetBootstrapContext ctx) {
        Holder<NoiseGeneratorSettings> largeSettings =
                ctx.noiseSettings.getOrThrow(NoiseGeneratorSettings.LARGE_BIOMES);
        return buildPreset(
                ctx.makeNoiseBasedOverworld(ctx.overworldStem.generator().getBiomeSource(), largeSettings),
                ctx.netherContext, WoverNetherConfig.MINECRAFT_18_LARGE,
                ctx.endContext, WoverEndConfig.MINECRAFT_20_LARGE
        );
    }

    private static WorldPreset createNormal(WorldPresetBootstrapContext ctx) {
        return buildPreset(
                ctx.overworldStem,
                ctx.netherContext, WoverNetherConfig.DEFAULT,
                ctx.endContext, WoverEndConfig.DEFAULT
        );
    }

    private static WorldPreset createSuperflat(WorldPresetBootstrapContext ctx) {
        return buildPreset(
                ctx.makeDefaultFlatOverworld(),
                ctx.netherContext, WoverNetherConfig.DEFAULT,
                ctx.endContext, WoverEndConfig.DEFAULT
        );
    }

    private static WorldPreset buildPreset(
            LevelStem overworldStem,
            WorldPresetBootstrapContext.StemContext netherContext,
            WoverNetherConfig netherConfig,
            WorldPresetBootstrapContext.StemContext endContext,
            WoverEndConfig endConfig
    ) {
        return WorldPresetManager.of(Map.of(
                LevelStem.OVERWORLD, overworldStem,
                LevelStem.NETHER, WorldPresets.makeWoverNetherStem(netherContext, netherConfig),
                LevelStem.END, WorldPresets.makeWoverEndStem(endContext, endConfig)
        ));
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // Forces static keys to initialize before datapack registry bootstrapping.
    }
}
