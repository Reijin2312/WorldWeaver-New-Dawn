package org.betterx.wover.core.mixin.registry;

import org.betterx.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(VanillaRegistries.class)
public class VanillaRegistriesMixin {
    @Shadow
    @Final
    private static RegistrySetBuilder WORLD_BUILDER;

    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/RegistrySetBuilder;add(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/registries/SingleRegistryBootstrap;)Lnet/minecraft/core/RegistrySetBuilder;"
            )
    )
    private static void wover_augmentVanillaRegistryBootstraps(Args args) {
        ResourceKey key = args.get(0);
        SingleRegistryBootstrap bootstrap = args.get(1);
        args.set(1, DatapackRegistryBuilderImpl.augmentVanillaBootstrap(key, bootstrap));
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void wover_addCustomDatapackRegistries(CallbackInfo ci) {
        DatapackRegistryBuilderImpl.bootstrap((key, bootstrap) ->
                WORLD_BUILDER.add((ResourceKey) key, bootstrap));
    }
}
