package org.betterx.wover.generator.impl.preset;

import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.generator.impl.chunkgenerator.WoverChunkGenerator;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext.StemContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public class PresetRegistryImpl {
   public static final ResourceKey<WorldPreset> WOVER_WORLD = WorldPresetManager.createKey(LibWoverWorldGenerator.C.id("normal"));
   public static final ResourceKey<WorldPreset> WOVER_WORLD_LARGE = WorldPresetManager.createKey(LibWoverWorldGenerator.C.id("large"));
   public static final ResourceKey<WorldPreset> WOVER_WORLD_AMPLIFIED = WorldPresetManager.createKey(LibWoverWorldGenerator.C.id("amplified"));
   public static final ResourceKey<WorldPreset> BCL_WORLD_17 = WorldPresetManager.createKey(LegacyHelper.BCLIB_CORE.id("legacy_17"));

   @NotNull
   public static LevelStem makeWoverNetherStem(StemContext context, WoverNetherConfig config) {
      WoverNetherBiomeSource netherSource = new WoverNetherBiomeSource(config);
      return new LevelStem(context.dimension, new WoverChunkGenerator(netherSource, context.generatorSettings));
   }

   public static LevelStem makeWoverEndStem(StemContext context, WoverEndConfig config) {
      WoverEndBiomeSource endSource = new WoverEndBiomeSource(config);
      return new LevelStem(context.dimension, new WoverChunkGenerator(endSource, context.generatorSettings));
   }

   @Internal
   public static void ensureStaticallyLoaded() {
   }
}

