package org.betterx.wover.block.mixin;

import org.betterx.wover.block.impl.ModelProviderExclusions;

import net.minecraft.world.level.block.Block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

/*
 * The vanilla Model provider will fail, if it does not include a model for every registered block
 * We provide a method to exclude blocks from that check...
 */
@Mixin(net.minecraft.data.models.ModelProvider.class)
public class ModelProviderMixin {
    @WrapOperation(method = "lambda$run$3", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
    private static boolean wover_notExcluded(Map instance, Object o, Operation<Boolean> original) {
        boolean res = original.call(instance, o) || ModelProviderExclusions.isExcluded((Block) o);
        return res;
    }
}
