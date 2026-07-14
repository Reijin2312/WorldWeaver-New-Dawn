package org.betterx.wover.entrypoint;

import org.betterx.wover.config.api.Configs;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.generator.api.preset.WorldPresets;
import org.betterx.wover.generator.impl.biomesource.BiomeSourceManagerImpl;
import org.betterx.wover.generator.impl.biomesource.WoverBiomeDataImpl;
import org.betterx.wover.generator.impl.chunkgenerator.ChunkGeneratorManagerImpl;
import org.betterx.wover.generator.impl.chunkgenerator.WoverChunkGeneratorImpl;
import org.betterx.wover.generator.impl.preset.PresetRegistryImpl;
import org.betterx.wover.preset.api.WorldPresetManager;

import net.fabricmc.api.ModInitializer;

public class LibWoverWorldGenerator implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-generator", "wover");

    @Override
    public void onInitialize() {
        if (!ModCore.isClient() && Configs.MAIN.forceDefaultWorldPresetOnServer.get()) {
            WorldPresetManager.suggestDefault(WorldPresets.WOVER_WORLD, 2000);
        }
        
        PresetRegistryImpl.ensureStaticallyLoaded();
        WoverBiomeDataImpl.initialize();
        BiomeSourceManagerImpl.initialize();
        ChunkGeneratorManagerImpl.initialize();
        WoverChunkGeneratorImpl.initialize();
    }
}