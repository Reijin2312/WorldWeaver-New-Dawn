package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface AsMultiPlaceRandomSelect extends FeatureConfigurator {
   AsMultiPlaceRandomSelect addAllStates(Block var1, int var2);

   AsMultiPlaceRandomSelect addAll(int var1, Block... var2);

   AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty var1, Block var2, int var3);

   AsMultiPlaceRandomSelect add(Block var1, float var2);

   AsMultiPlaceRandomSelect add(BlockState var1, float var2);

   AsMultiPlaceRandomSelect add(BlockStateProvider var1, float var2);

   AsMultiPlaceRandomSelect addAllStates(Block var1, int var2, int var3);

   AsMultiPlaceRandomSelect addAll(int var1, int var2, Block... var3);

   AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty var1, Block var2, int var3, int var4);

   AsMultiPlaceRandomSelect add(Block var1, float var2, int var3);

   AsMultiPlaceRandomSelect add(BlockState var1, float var2, int var3);

   AsMultiPlaceRandomSelect add(BlockStateProvider var1, float var2, int var3);

   AsMultiPlaceRandomSelect placement(AsMultiPlaceRandomSelect.Placer var1);

   @FunctionalInterface
   public interface Placer {
      Holder<PlacedFeature> place(FeaturePlacementBuilder var1, int var2);
   }
}
