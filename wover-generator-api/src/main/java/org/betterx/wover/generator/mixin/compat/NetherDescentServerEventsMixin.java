package org.betterx.wover.generator.mixin.compat;

import org.betterx.wover.generator.impl.compat.NetherDescentCompat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** Prevents Nether Descent from warning that no supported biome API is installed when WoVer handles its biomes. */
@Pseudo
@Mixin(targets = "net.potionstudios.netherdescent.event.ServerEventsHandler", remap = false)
public abstract class NetherDescentServerEventsMixin {
    @ModifyExpressionValue(
            method = "onPlayerJoin",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/potionstudios/netherdescent/PlatformHandler;isModLoaded(Ljava/lang/String;)Z",
                    ordinal = 0,
                    remap = false
            ),
            require = 0
    )
    private static boolean wover$recognizeNativeCompatibility(boolean original) {
        return original || NetherDescentCompat.isActive();
    }
}
