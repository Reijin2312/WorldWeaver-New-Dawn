package org.betterx.wover.feature.api.features;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;

public interface GrowableFeature {
   boolean grow(ServerLevelAccessor var1, BlockPos var2, RandomSource var3);
}
