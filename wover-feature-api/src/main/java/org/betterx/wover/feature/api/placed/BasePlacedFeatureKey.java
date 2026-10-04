package org.betterx.wover.feature.api.placed;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BasePlacedFeatureKey<K extends BasePlacedFeatureKey<K>> {
   ResourceKey<PlacedFeature> key();

   @Nullable
   Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> var1);

   @Nullable
   Holder<PlacedFeature> getHolder(@Nullable RegistryAccess var1);

   Holder<PlacedFeature> getHolder(@NotNull BootstrapContext<?> var1);

   Decoration getDecoration();

   K setDecoration(Decoration var1);
}
