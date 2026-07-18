package org.betterx.wover.potions.mixin;

import org.betterx.wover.potions.impl.PotionManagerImpl;

import net.minecraft.world.item.alchemy.PotionBrewing;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {
    @Inject(method = "addVanillaMixes", at = @At("TAIL"))
    private static void wover_bootstrapPotions(
            PotionBrewing.Builder builder,
            CallbackInfo ci
    ) {
        PotionManagerImpl.BOOTSTRAP_POTIONS.emit(c -> c.bootstrap(builder));
    }
}
