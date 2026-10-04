package org.betterx.wover.item.mixin.item_stack_setup;

import org.betterx.wover.item.api.ItemStackHelper;

import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootItemFunction.class)
public interface LootItemFunctionMixin {
    @Inject(
            method = "decorate",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void wover_setupItemStack(
            Optional<Holder<LootItemFunction>> function,
            Consumer<ItemStack> consumer,
            LootContext context,
            CallbackInfoReturnable<Consumer<ItemStack>> cir
    ) {
        Consumer<ItemStack> original = cir.getReturnValue();
        cir.setReturnValue(stack -> original.accept(ItemStackHelper.callItemStackSetupIfPossible(stack)));
    }
}
