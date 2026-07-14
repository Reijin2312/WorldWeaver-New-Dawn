package org.betterx.wover.entrypoint;

import org.betterx.wover.biome.impl.BiomeManagerImpl;
import org.betterx.wover.biome.impl.data.BiomeCodecRegistryImpl;
import org.betterx.wover.biome.impl.modification.BiomeModificationRegistryImpl;
import org.betterx.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;
import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class LibWoverBiome implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-biome", "wover");

    @Override
    public void onInitialize() {
        BiomeManagerImpl.initialize();
        BiomeCodecRegistryImpl.initialize();
        //BiomeDataRegistryImpl.initialize(); //done in the wover.datapack.registry entrypoint
        BiomePredicateRegistryImpl.initialize();
        BiomeModificationRegistryImpl.initialize();
    }
}