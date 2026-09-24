package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

@Deprecated(
   since = "26.1.0",
   forRemoval = true
)
public interface WeightedBlockPatch extends BaseWeightedBlock<WeightedBlockPatch>, BasePatch<WeightedBlockPatch> {
   WeightedBlockPatch isEmpty();

   WeightedBlockPatch isEmpty(boolean var1);

   WeightedBlockPatch isOn(BlockPredicate var1);

   WeightedBlockPatch isEmptyAndOn(BlockPredicate var1);
}
