package org.betterx.wover.core.mixin.registry;

import org.betterx.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaRegistries.class)
public class VanillaRegistriesMixin {
    @Shadow
    @Final
    private static RegistrySetBuilder WORLD_BUILDER;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void wover_addCustomDatapackRegistries(CallbackInfo ci) {
        DatapackRegistryBuilderImpl.bootstrap((key, bootstrap) ->
                WORLD_BUILDER.add((ResourceKey) key, bootstrap));
    }
}
