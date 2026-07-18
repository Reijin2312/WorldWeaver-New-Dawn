package org.betterx.wover.item.mixin.item_stack_setup;

import org.betterx.wover.item.api.ItemStackHelper;

import java.util.List;
import java.util.function.BiFunction;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootItemFunctions.class)
public class LootItemFunctionMixin {
    @Inject(
            method = "compose(Ljava/util/List;)Ljava/util/function/BiFunction;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void wover_setupItemStack(
            List<? extends BiFunction<ItemStack, LootContext, ItemStack>> functions,
            CallbackInfoReturnable<BiFunction<ItemStack, LootContext, ItemStack>> cir
    ) {
        BiFunction<ItemStack, LootContext, ItemStack> original = cir.getReturnValue();
        cir.setReturnValue((stack, context) -> original.apply(
                ItemStackHelper.callItemStackSetupIfPossible(stack),
                context
        ));
    }
}
