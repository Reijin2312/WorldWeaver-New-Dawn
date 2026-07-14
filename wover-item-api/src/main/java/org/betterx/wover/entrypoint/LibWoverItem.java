package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.item.impl.AutoItemRegistryTagProvider;

import net.fabricmc.api.ModInitializer;

public class LibWoverItem implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-item", "wover");

    @Override
    public void onInitialize() {
        //EnchantmentManagerImpl.initialize(); //done in the wover.datapack.registry entrypoint
        WoverDataGenEntryPoint.registerAutoProvider(AutoItemRegistryTagProvider::new);
    }
}