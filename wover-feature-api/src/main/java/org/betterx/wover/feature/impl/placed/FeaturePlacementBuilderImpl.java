package org.betterx.wover.feature.impl.placed;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import org.betterx.wover.feature.api.placed.modifiers.All;
import org.betterx.wover.feature.api.placed.modifiers.Debug;
import org.betterx.wover.feature.api.placed.modifiers.EveryLayer;
import org.betterx.wover.feature.api.placed.modifiers.Extend;
import org.betterx.wover.feature.api.placed.modifiers.ExtendXYZ;
import org.betterx.wover.feature.api.placed.modifiers.FindInDirection;
import org.betterx.wover.feature.api.placed.modifiers.Is;
import org.betterx.wover.feature.api.placed.modifiers.IsBasin;
import org.betterx.wover.feature.api.placed.modifiers.IsNextTo;
import org.betterx.wover.feature.api.placed.modifiers.Merge;
import org.betterx.wover.feature.api.placed.modifiers.NoiseFilter;
import org.betterx.wover.feature.api.placed.modifiers.Offset;
import org.betterx.wover.feature.api.placed.modifiers.OffsetProvider;
import org.betterx.wover.feature.api.placed.modifiers.Stencil;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;
import org.betterx.wover.feature.impl.configured.RandomPatchImpl;
import org.betterx.wover.math.api.valueproviders.Vec3iProvider;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.TrapezoidInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class FeaturePlacementBuilderImpl implements FeaturePlacementBuilder {
   protected final List<PlacementModifier> modifications = new LinkedList<>();
   @Nullable
   private final ResourceKey<PlacedFeature> key;
   @NotNull
   private final Holder<Feature> featureHolder;
   @Nullable
   private final BootstrapContext<PlacedFeature> bootstrapContext;
   @Nullable
   private final ResourceKey<Feature> transitiveFeatureKey;
   @Nullable
   private final BiFunction<ResourceKey<Feature>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder;

   public FeaturePlacementBuilderImpl(
      @Nullable BootstrapContext<PlacedFeature> bootstrapContext, @Nullable ResourceKey<PlacedFeature> key, @NotNull Holder<Feature> featureHolder
   ) {
      this(bootstrapContext, key, featureHolder, null, null);
   }

   public FeaturePlacementBuilderImpl(
      @Nullable BootstrapContext<PlacedFeature> bootstrapContext,
      @Nullable ResourceKey<PlacedFeature> key,
      @NotNull Holder<Feature> featureHolder,
      @Nullable ResourceKey<Feature> transitiveFeatureKey,
      @Nullable BiFunction<ResourceKey<Feature>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder
   ) {
      this.bootstrapContext = bootstrapContext;
      this.key = key;
      this.featureHolder = featureHolder;
      this.transitiveFeatureKey = transitiveFeatureKey;
      this.randomPatchBuilder = randomPatchBuilder;
   }

   @Internal
   public static FeaturePlacementBuilderImpl withTransitive(
      FeatureConfiguratorImpl featureBuilder, BiFunction<ResourceKey<Feature>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder
   ) {
      return new FeaturePlacementBuilderImpl(
         featureBuilder.getTransitiveBootstrapContext(),
         featureBuilder.getTransitiveFeatureKey(),
         featureBuilder.directHolder(),
         featureBuilder.key,
         randomPatchBuilder
      );
   }

   public FeaturePlacementBuilderImpl count(int count) {
      return this.modifier(CountPlacement.of(count));
   }

   public FeaturePlacementBuilderImpl countMax(int count) {
      return this.modifier(CountPlacement.of(UniformInt.of(0, count)));
   }

   public FeaturePlacementBuilderImpl countRange(int min, int max) {
      return this.modifier(CountPlacement.of(UniformInt.of(min, max)));
   }

   public FeaturePlacementBuilderImpl all() {
      return this.modifier(All.simple());
   }

   public FeaturePlacementBuilderImpl stencil() {
      return this.modifier(Stencil.all());
   }

   public FeaturePlacementBuilderImpl stencilOneIn4() {
      return this.modifier(Stencil.oneIn4());
   }

   public FeaturePlacementBuilderImpl onEveryLayer(int repetitions) {
      return this.modifier(CountOnEveryLayerPlacement.of(repetitions));
   }

   public FeaturePlacementBuilderImpl onEveryLayerMax(int count) {
      return this.modifier(CountOnEveryLayerPlacement.of(UniformInt.of(0, count)));
   }

   public FeaturePlacementBuilderImpl onEveryLayer() {
      return this.modifier(EveryLayer.on());
   }

   public FeaturePlacementBuilderImpl onEveryLayerMin4() {
      return this.modifier(EveryLayer.onTopMin4());
   }

   public FeaturePlacementBuilderImpl underEveryLayer() {
      return this.modifier(EveryLayer.underneath());
   }

   public FeaturePlacementBuilderImpl underEveryLayerMin4() {
      return this.modifier(EveryLayer.underneathMin4());
   }

   public FeaturePlacementBuilderImpl onceEvery(int n) {
      return this.modifier(RarityFilter.onAverageOnceEvery(n));
   }

   public FeaturePlacementBuilderImpl onlyInBiome() {
      return this.modifier(BiomeFilter.biome());
   }

   public FeaturePlacementBuilderImpl noiseIn(double min, double max, float scaleXZ, float scaleY) {
      return this.modifier(new NoiseFilter(Noises.GRAVEL, min, max, scaleXZ, scaleY));
   }

   public FeaturePlacementBuilderImpl noiseAbove(double value, float scaleXZ, float scaleY) {
      return this.modifier(new NoiseFilter(Noises.GRAVEL, value, Double.MAX_VALUE, scaleXZ, scaleY));
   }

   public FeaturePlacementBuilderImpl noiseBelow(double value, float scaleXZ, float scaleY) {
      return this.modifier(new NoiseFilter(Noises.GRAVEL, -Double.MAX_VALUE, value, scaleXZ, scaleY));
   }

   public FeaturePlacementBuilderImpl squarePlacement() {
      return this.modifier(InSquarePlacement.spread());
   }

   public FeaturePlacementBuilderImpl randomHeight10FromFloorCeil() {
      return this.modifier(PlacementUtils.RANGE_10_10);
   }

   public FeaturePlacementBuilderImpl randomHeight4FromFloorCeil() {
      return this.modifier(PlacementUtils.RANGE_4_4);
   }

   public FeaturePlacementBuilderImpl randomHeight8FromFloorCeil() {
      return this.modifier(PlacementUtils.RANGE_8_8);
   }

   public FeaturePlacementBuilderImpl randomHeightFromFloorToMaxTerrain() {
      return this.modifier(PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT);
   }

   public FeaturePlacementBuilderImpl randomHeight() {
      return this.modifier(PlacementUtils.FULL_RANGE);
   }

   public FeaturePlacementBuilderImpl spreadHorizontal(IntProvider p) {
      return this.modifier(OffsetPlacement.horizontal(p));
   }

   public FeaturePlacementBuilderImpl spreadVertical(IntProvider p) {
      return this.modifier(OffsetPlacement.horizontal(p));
   }

   public FeaturePlacementBuilderImpl spread(IntProvider horizontal, IntProvider vertical) {
      return this.modifier(OffsetPlacement.of(horizontal, vertical));
   }

   public FeaturePlacementBuilderImpl offset(Direction dir) {
      return this.modifier(Offset.inDirection(dir));
   }

   public FeaturePlacementBuilderImpl offset(Vec3i dir) {
      return this.modifier(new Offset(dir));
   }

   public FeaturePlacementBuilderImpl offset(int x, int y, int z) {
      return this.offset(new Vec3i(x, y, z));
   }

   public FeaturePlacementBuilderImpl offset(IntProvider x, IntProvider y, IntProvider z) {
      return this.modifier(new OffsetProvider(new Vec3iProvider(x, y, z)));
   }

   public FeaturePlacementBuilderImpl noiseBasedCount(float noiseLevel, int belowNoiseCount, int aboveNoiseCount) {
      return this.modifier(NoiseThresholdCountPlacement.of(noiseLevel, belowNoiseCount, aboveNoiseCount));
   }

   public FeaturePlacementBuilderImpl extendDown(int min, int max) {
      return this.modifier(new Extend(Direction.DOWN, UniformInt.of(min, max)));
   }

   public FeaturePlacementBuilderImpl inBasinOf(BlockPredicate... predicates) {
      return this.modifier(IsBasin.simple(BlockPredicate.anyOf(predicates)));
   }

   public FeaturePlacementBuilderImpl inOpenBasinOf(BlockPredicate... predicates) {
      return this.modifier(IsBasin.openTop(BlockPredicate.anyOf(predicates)));
   }

   public FeaturePlacementBuilderImpl is(BlockPredicate... predicates) {
      return this.modifier(new Is(BlockPredicate.anyOf(predicates), Optional.empty()));
   }

   public FeaturePlacementBuilderImpl isAbove(BlockPredicate... predicates) {
      return this.modifier(new Is(BlockPredicate.anyOf(predicates), Optional.of(Direction.DOWN.getUnitVec3i())));
   }

   public FeaturePlacementBuilderImpl isUnder(BlockPredicate... predicates) {
      return this.modifier(new Is(BlockPredicate.anyOf(predicates), Optional.of(Direction.UP.getUnitVec3i())));
   }

   public FeaturePlacementBuilderImpl findSolidFloor(int distance) {
      return this.modifier(FindInDirection.down(distance));
   }

   public FeaturePlacementBuilderImpl findSolidCeil(int distance) {
      return this.modifier(FindInDirection.up(distance));
   }

   public FeaturePlacementBuilderImpl findSolidSurface(Direction dir, int distance) {
      return this.modifier(new FindInDirection(dir, distance, 0, BlockPredicates.ONLY_GROUND));
   }

   public FeaturePlacementBuilderImpl findSolidSurface(List<Direction> dir, int distance, boolean randomSelect) {
      return this.modifier(new FindInDirection(dir, distance, randomSelect, 0, BlockPredicates.ONLY_GROUND));
   }

   public FeaturePlacementBuilderImpl onWalls(int distance, int depth) {
      return this.modifier(new FindInDirection(BlockHelper.HORIZONTAL, distance, false, depth, BlockPredicates.ONLY_GROUND));
   }

   public FeaturePlacementBuilderImpl onHeightmap(Types types) {
      return this.modifier(HeightmapPlacement.onHeightmap(types));
   }

   @Override
   public FeaturePlacementBuilder projectToSurface() {
      return this.heightmap().offset(0, -1, 0);
   }

   public FeaturePlacementBuilderImpl heightmap() {
      return this.modifier(PlacementUtils.HEIGHTMAP);
   }

   public FeaturePlacementBuilderImpl heightmapTopSolid() {
      return this.modifier(PlacementUtils.HEIGHTMAP_TOP_SOLID);
   }

   public FeaturePlacementBuilderImpl heightmapWorldSurface() {
      return this.modifier(PlacementUtils.HEIGHTMAP_WORLD_SURFACE);
   }

   @Override
   public FeaturePlacementBuilder heightmapOceanFloor() {
      return this.modifier(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR);
   }

   @Override
   public FeaturePlacementBuilderImpl extendXYZ(
      int xzSpread, float centerDensity, float borderDensity, int height, boolean square, ExtendXYZ.HeightPropagation propagation
   ) {
      return this.modifier(
         new ExtendXYZ(
            ConstantInt.of(xzSpread),
            ConstantFloat.of(centerDensity),
            ConstantFloat.of(borderDensity),
            square,
            ConstantFloat.of((float)Math.abs(height) / xzSpread),
            propagation
         )
      );
   }

   @Override
   public FeaturePlacementBuilder extendXYZ(
      IntProvider xzSpread,
      FloatProvider centerDensity,
      FloatProvider borderDensity,
      FloatProvider heightScale,
      boolean square,
      ExtendXYZ.HeightPropagation propagation
   ) {
      return this.modifier(new ExtendXYZ(xzSpread, centerDensity, borderDensity, square, heightScale, propagation));
   }

   public FeaturePlacementBuilderImpl extendXZ(int xzSpread, float centerDensity, float borderDensity, boolean square) {
      return this.modifier(new ExtendXYZ(ConstantInt.of(xzSpread), ConstantFloat.of(centerDensity), ConstantFloat.of(borderDensity), square));
   }

   @Override
   public FeaturePlacementBuilder extendXZ(IntProvider xzSpread, FloatProvider centerDensity, FloatProvider borderDensity, boolean square) {
      return this.modifier(new ExtendXYZ(xzSpread, centerDensity, borderDensity, square));
   }

   public FeaturePlacementBuilderImpl extendZigZagXZ(int xzSpread) {
      IntProvider xz = UniformInt.of(0, xzSpread);
      return this.modifier(
         new Merge(List.of(new Extend(Direction.NORTH, xz), new Extend(Direction.SOUTH, xz), new Extend(Direction.EAST, xz), new Extend(Direction.WEST, xz))),
         new Merge(List.of(new Extend(Direction.EAST, xz), new Extend(Direction.WEST, xz), new Extend(Direction.NORTH, xz), new Extend(Direction.SOUTH, xz)))
      );
   }

   public FeaturePlacementBuilderImpl extendZigZagXYZ(int xzSpread, int ySpread) {
      IntProvider xz = UniformInt.of(0, xzSpread);
      return this.extendZigZagXZ(xzSpread).extendDown(1, ySpread);
   }

   public FeaturePlacementBuilderImpl isEmpty() {
      return this.modifier(BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE));
   }

   public FeaturePlacementBuilderImpl is(BlockPredicate predicate) {
      return this.modifier(BlockPredicateFilter.forPredicate(predicate));
   }

   public FeaturePlacementBuilderImpl isNextTo(BlockPredicate predicate) {
      return this.modifier(IsNextTo.simple(predicate));
   }

   public FeaturePlacementBuilderImpl belowIsNextTo(BlockPredicate predicate) {
      return this.modifier(IsNextTo.offset(predicate, Direction.DOWN.getUnitVec3i()));
   }

   public FeaturePlacementBuilderImpl isNextTo(BlockPredicate predicate, Vec3i offset) {
      return this.modifier(IsNextTo.offset(predicate, offset));
   }

   public FeaturePlacementBuilderImpl isOn(BlockPredicate predicate) {
      return this.modifier(Is.below(predicate));
   }

   public FeaturePlacementBuilderImpl isEmptyAndOn(BlockPredicate predicate) {
      return this.isEmpty().isOn(predicate);
   }

   public FeaturePlacementBuilderImpl isEmptyAndOnNylium() {
      return this.isEmptyAndOn(BlockPredicates.ONLY_NYLIUM);
   }

   public FeaturePlacementBuilderImpl isEmptyAndOnNetherGround() {
      return this.isEmptyAndOn(BlockPredicates.ONLY_NETHER_GROUND);
   }

   public FeaturePlacementBuilderImpl isUnder(BlockPredicate predicate) {
      return this.modifier(Is.above(predicate));
   }

   public FeaturePlacementBuilderImpl isEmptyAndUnder(BlockPredicate predicate) {
      return this.isEmpty().isUnder(predicate);
   }

   public FeaturePlacementBuilderImpl isEmptyAndUnderNylium() {
      return this.isEmptyAndUnder(BlockPredicates.ONLY_NYLIUM);
   }

   public FeaturePlacementBuilderImpl isEmptyAndUnderNetherGround() {
      return this.isEmptyAndUnder(BlockPredicates.ONLY_NETHER_GROUND);
   }

   public FeaturePlacementBuilderImpl isFullShape() {
      return this.isEmptyAndUnder(BlockPredicates.IS_FULL_BLOCK);
   }

   public FeaturePlacementBuilderImpl vanillaNetherGround(int countPerLayer) {
      return this.randomHeight4FromFloorCeil().onlyInBiome().onEveryLayer(countPerLayer).onlyInBiome();
   }

   public FeaturePlacementBuilderImpl betterNetherGround(int countPerLayer) {
      return this.randomHeight4FromFloorCeil().count(countPerLayer).squarePlacement().onEveryLayerMin4().onlyInBiome();
   }

   public FeaturePlacementBuilderImpl betterNetherCeiling(int countPerLayer) {
      return this.randomHeight4FromFloorCeil().count(countPerLayer).squarePlacement().onlyInBiome().underEveryLayerMin4().onlyInBiome();
   }

   public FeaturePlacementBuilderImpl betterNetherOnWall(int countPerLayer) {
      return this.count(countPerLayer).squarePlacement().randomHeight4FromFloorCeil().onlyInBiome().onWalls(16, 0);
   }

   public FeaturePlacementBuilderImpl betterNetherInWall(int countPerLayer) {
      return this.count(countPerLayer).squarePlacement().randomHeight4FromFloorCeil().onlyInBiome().onWalls(16, 1);
   }

   public FeaturePlacementBuilderImpl modifier(PlacementModifier... modifiers) {
      for (PlacementModifier m : modifiers) {
         this.modifications.add(m);
      }

      return this;
   }

   public FeaturePlacementBuilderImpl modifier(List<PlacementModifier> modifiers) {
      this.modifications.addAll(modifiers);
      return this;
   }

   public FeaturePlacementBuilderImpl debug(String caption) {
      return this.modifier(new Debug(caption));
   }

   public FeaturePlacementBuilderImpl scatter(int tries, int xzSpread, int ySpread) {
      return this.count(tries).spread(TrapezoidInt.of(-xzSpread, xzSpread, 0), TrapezoidInt.of(-ySpread, ySpread, 0)).onlyInBiome();
   }

   public FeaturePlacementBuilderImpl scatter(int tries, int xzSpread, int ySpread, BlockPredicate filter) {
      return this.count(tries).spread(TrapezoidInt.of(-xzSpread, xzSpread, 0), TrapezoidInt.of(-ySpread, ySpread, 0)).is(filter).onlyInBiome();
   }

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   @Override
   public RandomPatch inRandomPatch() {
      RandomPatch randomPatch;
      if (this.randomPatchBuilder != null) {
         randomPatch = this.randomPatchBuilder.apply(this.transitiveFeatureKey, this.key);
      } else {
         randomPatch = new InlineBuilderImpl(this.bootstrapContext, this.key).randomPatch();
      }

      return randomPatch.featureToPlace(this.directHolder());
   }

   @Override
   public Holder<PlacedFeature> register() {
      if (this.key == null) {
         throw new IllegalStateException("A ResourceKey for a Feature can not be null if it should be registered!");
      } else if (this.bootstrapContext == null) {
         throw new IllegalStateException("A BootstrapContext for a Feature can not be null if it should be registered! (" + this.key.identifier() + ")");
      } else {
         PlacedFeature feature = this.build();
         return this.bootstrapContext.register(this.key, feature);
      }
   }

   @Override
   public Holder<PlacedFeature> directHolder() {
      return Holder.direct(this.build());
   }

   @NotNull
   public PlacedFeature build() {
      return new PlacedFeature(this.featureHolder, this.modifications);
   }
}
