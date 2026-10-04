package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.features.PillarFeature;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.NotNull;

public interface AsPillar extends FeatureConfigurator {
   AsPillar transformer(@NotNull PillarFeature.KnownTransformers var1);

   AsPillar allowedPlacement(BlockPredicate var1);

   AsPillar direction(Direction var1);

   AsPillar blockState(Block var1);

   AsPillar blockState(BlockState var1);

   AsPillar blockState(BlockStateProvider var1);

   AsPillar maxHeight(int var1);

   AsPillar maxHeight(IntProvider var1);

   AsPillar minHeight(int var1);

   AsPillar minHeight(IntProvider var1);
}
