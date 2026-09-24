package org.betterx.wover.feature.api.configured;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
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
import org.betterx.wover.feature.impl.configured.AsBlockColumnImpl;
import org.betterx.wover.feature.impl.configured.AsMultiPlaceRandomSelectImpl;
import org.betterx.wover.feature.impl.configured.AsOreImpl;
import org.betterx.wover.feature.impl.configured.AsPillarImpl;
import org.betterx.wover.feature.impl.configured.AsRandomSelectImpl;
import org.betterx.wover.feature.impl.configured.AsSequenceImpl;
import org.betterx.wover.feature.impl.configured.FacingBlockImpl;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import org.betterx.wover.feature.impl.configured.ForSimpleBlockImpl;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;
import org.betterx.wover.feature.impl.configured.NetherForrestVegetationImpl;
import org.betterx.wover.feature.impl.configured.RandomPatchImpl;
import org.betterx.wover.feature.impl.configured.WeightedBlockImpl;
import org.betterx.wover.feature.impl.configured.WeightedBlockPatchImpl;
import org.betterx.wover.feature.impl.configured.WithFeatureImpl;
import org.betterx.wover.feature.impl.configured.WithTemplatesImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FeatureContentManager {
   public static final Event<OnBootstrapRegistry<Feature>> BOOTSTRAP_FEATURES = FeatureConfiguratorImpl.BOOTSTRAP_FEATURES;
   public static final FeatureContentManager.InlineBuilder INLINE_BUILDER = new InlineBuilderImpl();

   public static FeatureKey<ForSimpleBlock> simple(Identifier id) {
      return new ForSimpleBlockImpl.Key(id);
   }

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   public static FeatureKey<RandomPatch> randomPatch(Identifier id) {
      return new RandomPatchImpl.Key(id);
   }

   public static FeatureKey<AsOre> ore(Identifier id) {
      return new AsOreImpl.Key(id);
   }

   public static FeatureKey<AsPillar> pillar(Identifier id) {
      return new AsPillarImpl.Key(id);
   }

   public static FeatureKey<AsSequence> sequence(Identifier id) {
      return new AsSequenceImpl.Key(id);
   }

   public static FeatureKey<AsBlockColumn> blockColumn(Identifier id) {
      return new AsBlockColumnImpl.Key(id);
   }

   public static FeatureKey<WithTemplates> templates(Identifier id) {
      return new WithTemplatesImpl.Key(id);
   }

   public static FeatureKey<NetherForrestVegetation> netherForrestVegetation(Identifier id) {
      return new NetherForrestVegetationImpl.Key(id);
   }

   public static <F extends Feature> FeatureKey<WithFeature<F>> withFeature(Identifier id, F feature) {
      return new WithFeatureImpl.Key<>(id, feature);
   }

   public static FeatureKey<FacingBlock> facingBlock(Identifier id) {
      return new FacingBlockImpl.Key(id);
   }

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   public static FeatureKey<WeightedBlockPatch> randomBlockPatch(Identifier id) {
      return new WeightedBlockPatchImpl.Key(id);
   }

   @Deprecated(
      since = "26.1.0",
      forRemoval = true
   )
   public static FeatureKey<WeightedBlockPatch> bonemeal(Identifier id) {
      return new WeightedBlockPatchImpl.KeyBonemeal(id);
   }

   public static FeatureKey<NetherForrestVegetation> bonemealNetherForrest(Identifier id) {
      return new NetherForrestVegetationImpl.KeyBonemeal(id);
   }

   public static FeatureKey<WeightedBlock> randomBlock(Identifier id) {
      return new WeightedBlockImpl.Key(id);
   }

   public static FeatureKey<AsRandomSelect> randomFeature(Identifier id) {
      return new AsRandomSelectImpl.Key(id);
   }

   public static FeatureKey<AsMultiPlaceRandomSelect> multiPlaceRandomFeature(Identifier id) {
      return new AsMultiPlaceRandomSelectImpl.Key(id);
   }

   @Nullable
   public static Holder<Feature> getHolder(@Nullable HolderGetter<Feature> getter, @NotNull ResourceKey<Feature> key) {
      return FeatureConfiguratorImpl.getHolder(getter, key);
   }

   @Nullable
   public static Holder<Feature> getHolder(@Nullable BootstrapContext<?> context, @NotNull ResourceKey<Feature> key) {
      return getHolder(context.lookup(Registries.FEATURE), key);
   }

   private FeatureContentManager() {
   }

   public interface InlineBuilder {
      AsOre ore();

      AsPillar pillar();

      AsSequence sequence();

      AsBlockColumn blockColumn();

      WithTemplates templates();

      NetherForrestVegetation netherForrestVegetation();

      <F extends Feature> WithFeature<F> withFeature(F var1);

      FacingBlock facingBlock();

      @Deprecated(
         since = "26.1.0",
         forRemoval = true
      )
      WeightedBlockPatch randomBlockPatch();

      @Deprecated(
         since = "26.1.0",
         forRemoval = true
      )
      WeightedBlockPatch bonemealPatch();

      WeightedBlock randomBlock();

      AsRandomSelect randomFeature();

      AsMultiPlaceRandomSelect multiPlaceRandomFeature();

      ForSimpleBlock simple();

      @Deprecated(
         since = "26.1.0",
         forRemoval = true
      )
      RandomPatch randomPatch();
   }
}
