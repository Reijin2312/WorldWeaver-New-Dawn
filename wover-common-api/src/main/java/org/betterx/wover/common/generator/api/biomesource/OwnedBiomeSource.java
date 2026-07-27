package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;

/**
 * Server-safe contract for biome sources that distinguish their own biomes from externally
 * supplied biomes.
 */
public interface OwnedBiomeSource {
    Collection<Holder<Biome>> ownedPossibleBiomes();
}
