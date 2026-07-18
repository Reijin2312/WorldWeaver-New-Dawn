package org.betterx.wover.datagen.api;

import net.minecraft.data.loot.LootTableProvider;

/**
 * Marker interface for loot table data providers so they can be grouped into a single
 * {@link LootTableProvider} to avoid duplicate provider registrations.
 */
public interface WoverLootProvider {
    /**
     * Returns the sub-provider entry that should be added to the aggregated loot table provider.
     */
    LootTableProvider.SubProviderEntry toSubProviderEntry();
}
