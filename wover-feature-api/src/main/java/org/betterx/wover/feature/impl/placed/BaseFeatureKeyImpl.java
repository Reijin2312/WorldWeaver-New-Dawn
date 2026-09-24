package org.betterx.wover.feature.impl.placed;

import org.betterx.wover.feature.api.placed.BasePlacedFeatureKey;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseFeatureKeyImpl<K extends BasePlacedFeatureKey<K>> implements BasePlacedFeatureKey<K> {
   public final ResourceKey<PlacedFeature> key;
   protected Decoration decoration = Decoration.VEGETAL_DECORATION;

   BaseFeatureKeyImpl(Identifier featureId) {
      this.key = ResourceKey.create(Registries.PLACED_FEATURE, featureId);
   }

   @Override
   public Decoration getDecoration() {
      return this.decoration;
   }

   @Override
   public K setDecoration(Decoration decoration) {
      this.decoration = decoration;
      return (K)this;
   }

   @Override
   public ResourceKey<PlacedFeature> key() {
      return null;
   }

   @Nullable
   @Override
   public Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> getter) {
      return PlacedFeatureManagerImpl.getHolder(getter, this.key);
   }

   @Nullable
   @Override
   public Holder<PlacedFeature> getHolder(@Nullable RegistryAccess access) {
      return access == null ? null : this.getHolder(access.lookupOrThrow(Registries.PLACED_FEATURE));
   }

   @Nullable
   @Override
   public Holder<PlacedFeature> getHolder(@NotNull BootstrapContext<?> context) {
      return this.getHolder(context.lookup(Registries.PLACED_FEATURE));
   }

   protected FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> bootstrapContext, Holder<Feature> holder) {
      return new FeaturePlacementBuilderImpl(bootstrapContext, this.key, holder);
   }
}
