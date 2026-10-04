package org.betterx.wover.entrypoint;


import org.betterx.wover.config.api.Configs;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverCore implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-core", "wover");

    @Override
    public void onInitialize() {
        DatapackRegistryBuilderImpl.registerDatapackRegistries();
        Configs.saveConfigs();
    }
}
