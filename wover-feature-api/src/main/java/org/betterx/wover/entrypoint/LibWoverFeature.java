package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.impl.FeatureManagerImpl;
import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverFeature implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-feature", "wover");

    @Override
    public void onInitialize() {
        PlacementModifiersImpl.ensureStaticInitialization();
        FeatureManagerImpl.ensureStaticInitialization();
        //FeatureConfiguratorImpl.initialize(); //done in the wover.datapack.registry entrypoint
        //PlacedFeatureManagerImpl.initialize(); //done in the wover.datapack.registry entrypoint
    }
}