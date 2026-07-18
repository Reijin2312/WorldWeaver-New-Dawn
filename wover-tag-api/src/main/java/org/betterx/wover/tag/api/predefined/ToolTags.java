package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.ApiStatus;


/**
 * Tags that are used to mark the type of a tool.
 */
public class ToolTags {
    /**
     * {@code c:axes}
     */
    public static final TagKey<Item> COMMON_AXES = TagManager.ITEMS.makeCommonTag("axes");
    /**
     * {@code c:hoes}
     */
    public static final TagKey<Item> COMMON_HOES = TagManager.ITEMS.makeCommonTag("hoes");
    /**
     * {@code c:pickaxes}
     */
    public static final TagKey<Item> COMMON_PICKAXES = TagManager.ITEMS.makeCommonTag("pickaxes");
    /**
     * {@code c:shears}
     */
    public static final TagKey<Item> COMMON_SHEARS = TagManager.ITEMS.makeCommonTag("shears");
    /**
     * {@code c:shovels}
     */
    public static final TagKey<Item> COMMON_SHOVELS = TagManager.ITEMS.makeCommonTag("shovels");
    /**
     * {@code c:swords}
     */
    public static final TagKey<Item> COMMON_SWORDS = TagManager.ITEMS.makeCommonTag("swords");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private ToolTags() {
    }
}
