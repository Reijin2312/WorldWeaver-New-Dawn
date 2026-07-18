package org.betterx.wover.block.mixin;

import org.betterx.wover.block.impl.BlockHolderBridge;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockAccessorMixin implements BlockHolderBridge {
    @Mutable
    @Final
    @Shadow
    private Holder.Reference<Block> builtInRegistryHolder;

    @Override
    public Holder.Reference<Block> wover$getBuiltInRegistryHolder() {
        return builtInRegistryHolder;
    }

    @Override
    public void wover$setBuiltInRegistryHolder(Holder.Reference<Block> holder) {
        this.builtInRegistryHolder = holder;
    }
}
