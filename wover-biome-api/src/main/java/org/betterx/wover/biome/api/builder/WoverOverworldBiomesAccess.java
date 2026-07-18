package org.betterx.wover.biome.api.builder;

import net.minecraft.data.worldgen.biome.OverworldBiomes;

public final class WoverOverworldBiomesAccess {
    private WoverOverworldBiomesAccess() {
    }

    public static int calculateSkyColor(float temperature) {
        return OverworldBiomes.calculateSkyColor(temperature);
    }
}
