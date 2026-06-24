package org.betterx.wover.item.mixin.item_stack_setup;

import org.betterx.wover.item.api.ItemStackHelper;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;

@Mixin(ItemInput.class)
public class ItemInputMixin {
    @Shadow
    @Final
    private Holder<Item> item;

    @WrapOperation(
            method = "createItemStack",
            at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;I)Lnet/minecraft/world/item/ItemStack;"),
            require = 0
    )
    @Group(name = "wover_item_input_item_stack_init", min = 1, max = 1)
    public ItemStack wover_init(Holder<Item> item, int count, Operation<ItemStack> original) {
        return ItemStackHelper.callItemStackSetupIfPossible(original.call(item, count));
    }

    @WrapOperation(
            method = "createItemStack",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/DataComponentPatch;)Lnet/minecraft/world/item/ItemStack;"
            ),
            require = 0
    )
    @Group(name = "wover_item_input_item_stack_init", min = 1, max = 1)
    public ItemStack wover_initWithComponents(
            Holder<Item> item,
            int count,
            DataComponentPatch components,
            Operation<ItemStack> original
    ) {
        return ItemStackHelper.callItemStackSetupIfPossible(original.call(item, count, components));
    }
}
