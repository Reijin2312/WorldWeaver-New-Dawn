package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.configurators.AsBlockColumn;
import org.betterx.wover.feature.api.configured.configurators.AsMultiPlaceRandomSelect;
import org.betterx.wover.feature.api.configured.configurators.AsOre;
import org.betterx.wover.feature.api.configured.configurators.AsPillar;
import org.betterx.wover.feature.api.configured.configurators.AsRandomSelect;
import org.betterx.wover.feature.api.configured.configurators.AsSequence;
import org.betterx.wover.feature.api.configured.configurators.FacingBlock;
import org.betterx.wover.feature.api.configured.configurators.ForSimpleBlock;
import org.betterx.wover.feature.api.configured.configurators.NetherForrestVegetation;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.configured.configurators.WeightedBlock;
import org.betterx.wover.feature.api.configured.configurators.WeightedBlockPatch;
import org.betterx.wover.feature.api.configured.configurators.WithFeature;
import org.betterx.wover.feature.api.configured.configurators.WithTemplates;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.ApiStatus.Internal;

public class InlineBuilderImpl implements FeatureContentManager.InlineBuilder {
   private final ResourceKey<PlacedFeature> key;
   private final BootstrapContext<PlacedFeature> bootstapContext;

   public InlineBuilderImpl() {
      this(null, null);
   }

   @Internal
   public InlineBuilderImpl(BootstrapContext<PlacedFeature> bootstapContext, ResourceKey<PlacedFeature> key) {
      this.key = key;
      this.bootstapContext = bootstapContext;
   }

   @Override
   public AsOre ore() {
      AsOreImpl res = new AsOreImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public AsPillar pillar() {
      AsPillarImpl res = new AsPillarImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public AsSequence sequence() {
      AsSequenceImpl res = new AsSequenceImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public AsBlockColumn blockColumn() {
      AsBlockColumnImpl res = new AsBlockColumnImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public WithTemplates templates() {
      WithTemplatesImpl res = new WithTemplatesImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public NetherForrestVegetation netherForrestVegetation() {
      NetherForrestVegetationImpl res = new NetherForrestVegetationImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public <F extends Feature> WithFeature<F> withFeature(F feature) {
      WithFeatureImpl<F> res = new WithFeatureImpl<>(null, null);
      res.feature(feature);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public FacingBlock facingBlock() {
      FacingBlockImpl res = new FacingBlockImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   @Override
   public WeightedBlockPatch randomBlockPatch() {
      WeightedBlockPatchImpl res = new WeightedBlockPatchImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   @Override
   public WeightedBlockPatch bonemealPatch() {
      WeightedBlockPatchImpl res = new WeightedBlockPatchImpl(null, null);
      res.likeDefaultBonemeal();
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public WeightedBlock randomBlock() {
      WeightedBlockImpl res = new WeightedBlockImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public AsRandomSelect randomFeature() {
      AsRandomSelectImpl res = new AsRandomSelectImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public AsMultiPlaceRandomSelect multiPlaceRandomFeature() {
      AsMultiPlaceRandomSelectImpl res = new AsMultiPlaceRandomSelectImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Override
   public ForSimpleBlock simple() {
      ForSimpleBlockImpl res = new ForSimpleBlockImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   @Override
   public RandomPatch randomPatch() {
      RandomPatchImpl res = new RandomPatchImpl(null, null);
      res.setTransitive(this.bootstapContext, this.key);
      return res;
   }
}
