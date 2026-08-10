package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.complex.api.equipment.ToolTier.ToolValues;
import org.betterx.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

public class ToolTiers {
    public static ToolTier WOOD_TOOL = ToolTier
            .builder("wooden")
            .toolTier(ToolMaterial.WOOD)
            .blockTag(MineableTags.NEEDS_WOOD_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.2f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(7, -3.0f))
            .toolValues(ToolSlot.SPEAR_SLOT, new ToolValues(new ToolTier.SpearTuning(0.65f, 0.7f, 0.75f, 5.0f, 14.0f, 10.0f, 5.1f, 15.0f, 4.6f)))
            .build();

    public static ToolTier STONE_TOOL = ToolTier
            .builder("stone")
            .toolTier(ToolMaterial.STONE)
            .blockTag(BlockTags.NEEDS_STONE_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(7, -3.2f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-1, -2.0f))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-1, -2.5f))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(9, -5.0f))
            .toolValues(ToolSlot.SPEAR_SLOT, new ToolValues(new ToolTier.SpearTuning(0.75f, 0.82f, 0.7f, 4.5f, 13.0f, 9.0f, 5.1f, 13.75f, 4.6f)))
            .build();

    public static ToolTier GOLD_TOOL = ToolTier
            .builder("golden")
            .toolTier(ToolMaterial.GOLD)
            .blockTag(MineableTags.NEEDS_GOLD_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.0f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(0, -3.5f)).
            toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -4.0f))
            .toolValues(ToolSlot.SPEAR_SLOT, new ToolValues(new ToolTier.SpearTuning(0.95f, 0.7f, 0.7f, 3.5f, 13.0f, 8.5f, 5.1f, 13.75f, 4.6f)))
            .build();

    public static ToolTier IRON_TOOL = ToolTier
            .builder("iron")
            .toolTier(ToolMaterial.IRON)
            .blockTag(BlockTags.NEEDS_IRON_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.1f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-2, -1.0f))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-2, -4.0f))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -4.3f))
            .toolValues(ToolSlot.SPEAR_SLOT, new ToolValues(new ToolTier.SpearTuning(0.95f, 0.95f, 0.6f, 2.5f, 11.0f, 6.75f, 5.1f, 11.25f, 4.6f)))
            .build();

    public static ToolTier DIAMOND_TOOL = ToolTier
            .builder("diamond")
            .toolTier(ToolMaterial.DIAMOND)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-3, 0.0f))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-3, -0.5f))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(7, -4.0f))
            .toolValues(ToolSlot.SPEAR_SLOT, new ToolValues(new ToolTier.SpearTuning(1.05f, 1.075f, 0.5f, 3.0f, 10.0f, 6.5f, 5.1f, 10.0f, 4.6f)))
            .build();

    public static ToolTier NETHERITE_TOOL = ToolTier
            .builder("netherite")
            .toolTier(ToolMaterial.NETHERITE)
            .blockTag(MineableTags.NEEDS_NETHERITE_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-4, 0.0f))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-4, -0.5f))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -3.2f))
            .toolValues(ToolSlot.SPEAR_SLOT, new ToolValues(new ToolTier.SpearTuning(1.15f, 1.2f, 0.4f, 2.5f, 9.0f, 5.5f, 5.1f, 8.75f, 4.6f)))
            .build();


}
