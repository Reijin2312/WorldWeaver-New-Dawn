package org.betterx.wover.block.impl;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

/**
 * Bridge interface to access the intrinsic holder of a Block.
 * Implemented by mixin in {@code org.betterx.wover.block.mixin.BlockAccessorMixin}.
 */
public interface BlockHolderBridge {
    Holder.Reference<Block> wover$getBuiltInRegistryHolder();
    void wover$setBuiltInRegistryHolder(Holder.Reference<Block> holder);
}
