package org.betterx.wover.recipe.impl;

import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContextAccess;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public final class WoverRecipeProviderAccess {
    private static final ThreadLocal<BootstrapContextAccess> LOOKUP_CONTEXT = new ThreadLocal<>();

    private WoverRecipeProviderAccess() {
    }

    public static void withLookup(BootstrapContextAccess lookup, Runnable action) {
        BootstrapContextAccess previous = LOOKUP_CONTEXT.get();
        LOOKUP_CONTEXT.set(lookup);
        try {
            action.run();
        } finally {
            if (previous == null) {
                LOOKUP_CONTEXT.remove();
            } else {
                LOOKUP_CONTEXT.set(previous);
            }
        }
    }

    public static HolderGetter<Item> itemLookup() {
        BootstrapContextAccess provider = LOOKUP_CONTEXT.get();
        if (provider != null) {
            return provider.lookup(Registries.ITEM);
        }

        return BuiltInRegistries.ITEM;
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        HolderGetter<Item> lookup = itemLookup();
        if (lookup.get(tag).isPresent()) {
            return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(lookup, tag));
        }

        // Keep recipe datagen running even if optional integration tags are unavailable.
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item());
    }
}
