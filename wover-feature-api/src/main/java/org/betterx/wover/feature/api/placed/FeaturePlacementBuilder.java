package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.placed.modifiers.ExtendXYZ;
import org.betterx.wover.feature.impl.placed.FeaturePlacementBuilderImpl;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public interface FeaturePlacementBuilder {
   FeaturePlacementBuilder count(int var1);

   FeaturePlacementBuilder countMax(int var1);

   FeaturePlacementBuilder countRange(int var1, int var2);

   FeaturePlacementBuilder all();

   FeaturePlacementBuilder stencil();

   FeaturePlacementBuilder stencilOneIn4();

   FeaturePlacementBuilder onEveryLayer(int var1);

   FeaturePlacementBuilder onEveryLayerMax(int var1);

   FeaturePlacementBuilder onEveryLayer();

   FeaturePlacementBuilder onEveryLayerMin4();

   FeaturePlacementBuilder underEveryLayer();

   FeaturePlacementBuilder underEveryLayerMin4();

   FeaturePlacementBuilder onceEvery(int var1);

   FeaturePlacementBuilder onlyInBiome();

   FeaturePlacementBuilder noiseIn(double var1, double var3, float var5, float var6);

   FeaturePlacementBuilder noiseAbove(double var1, float var3, float var4);

   FeaturePlacementBuilder noiseBelow(double var1, float var3, float var4);

   FeaturePlacementBuilder squarePlacement();

   FeaturePlacementBuilder randomHeight10FromFloorCeil();

   FeaturePlacementBuilder randomHeight4FromFloorCeil();

   FeaturePlacementBuilder randomHeight8FromFloorCeil();

   FeaturePlacementBuilder randomHeightFromFloorToMaxTerrain();

   FeaturePlacementBuilder randomHeight();

   FeaturePlacementBuilder spreadHorizontal(IntProvider var1);

   FeaturePlacementBuilder spreadVertical(IntProvider var1);

   FeaturePlacementBuilder spread(IntProvider var1, IntProvider var2);

   FeaturePlacementBuilder offset(Direction var1);

   FeaturePlacementBuilder offset(Vec3i var1);

   FeaturePlacementBuilder offset(int var1, int var2, int var3);

   FeaturePlacementBuilder offset(IntProvider var1, IntProvider var2, IntProvider var3);

   FeaturePlacementBuilder noiseBasedCount(float var1, int var2, int var3);

   FeaturePlacementBuilder extendDown(int var1, int var2);

   FeaturePlacementBuilder inBasinOf(BlockPredicate... var1);

   FeaturePlacementBuilder inOpenBasinOf(BlockPredicate... var1);

   FeaturePlacementBuilder is(BlockPredicate... var1);

   FeaturePlacementBuilder isAbove(BlockPredicate... var1);

   FeaturePlacementBuilder isUnder(BlockPredicate... var1);

   FeaturePlacementBuilder findSolidFloor(int var1);

   FeaturePlacementBuilder findSolidCeil(int var1);

   FeaturePlacementBuilder findSolidSurface(Direction var1, int var2);

   FeaturePlacementBuilder findSolidSurface(List<Direction> var1, int var2, boolean var3);

   FeaturePlacementBuilder onWalls(int var1, int var2);

   FeaturePlacementBuilder onHeightmap(Types var1);

   FeaturePlacementBuilder projectToSurface();

   FeaturePlacementBuilder heightmap();

   FeaturePlacementBuilder heightmapTopSolid();

   FeaturePlacementBuilder heightmapWorldSurface();

   FeaturePlacementBuilder heightmapOceanFloor();

   FeaturePlacementBuilder extendXZ(int var1, float var2, float var3, boolean var4);

   FeaturePlacementBuilder extendXZ(IntProvider var1, FloatProvider var2, FloatProvider var3, boolean var4);

   FeaturePlacementBuilderImpl extendXYZ(int var1, float var2, float var3, int var4, boolean var5, ExtendXYZ.HeightPropagation var6);

   FeaturePlacementBuilder extendXYZ(
      IntProvider var1, FloatProvider var2, FloatProvider var3, FloatProvider var4, boolean var5, ExtendXYZ.HeightPropagation var6
   );

   FeaturePlacementBuilder extendZigZagXZ(int var1);

   FeaturePlacementBuilder extendZigZagXYZ(int var1, int var2);

   FeaturePlacementBuilder isEmpty();

   FeaturePlacementBuilder is(BlockPredicate var1);

   FeaturePlacementBuilder isNextTo(BlockPredicate var1);

   FeaturePlacementBuilder belowIsNextTo(BlockPredicate var1);

   FeaturePlacementBuilder isNextTo(BlockPredicate var1, Vec3i var2);

   FeaturePlacementBuilder isOn(BlockPredicate var1);

   FeaturePlacementBuilder isEmptyAndOn(BlockPredicate var1);

   FeaturePlacementBuilder isEmptyAndOnNylium();

   FeaturePlacementBuilder isEmptyAndOnNetherGround();

   FeaturePlacementBuilder isUnder(BlockPredicate var1);

   FeaturePlacementBuilder isEmptyAndUnder(BlockPredicate var1);

   FeaturePlacementBuilder isEmptyAndUnderNylium();

   FeaturePlacementBuilder isEmptyAndUnderNetherGround();

   FeaturePlacementBuilder isFullShape();

   FeaturePlacementBuilder vanillaNetherGround(int var1);

   FeaturePlacementBuilder betterNetherGround(int var1);

   FeaturePlacementBuilder betterNetherCeiling(int var1);

   FeaturePlacementBuilder betterNetherOnWall(int var1);

   FeaturePlacementBuilder betterNetherInWall(int var1);

   FeaturePlacementBuilder debug(String var1);

   FeaturePlacementBuilder modifier(PlacementModifier... var1);

   FeaturePlacementBuilder modifier(List<PlacementModifier> var1);

   FeaturePlacementBuilder scatter(int var1, int var2, int var3);

   FeaturePlacementBuilder scatter(int var1, int var2, int var3, BlockPredicate var4);

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   RandomPatch inRandomPatch();

   Holder<PlacedFeature> register();

   Holder<PlacedFeature> directHolder();
}
