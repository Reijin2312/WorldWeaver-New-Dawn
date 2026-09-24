package org.betterx.wover.feature.impl.placed;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class PlacedFeatureManagerImpl {
   public static final EventImpl<OnBootstrapRegistry<PlacedFeature>> BOOTSTRAP_PLACED_FEATURES = new EventImpl("BOOTSTRAP_PLACED_FEATURES");
   private static boolean didInit = false;

   @Nullable
   public static Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> getter, @NotNull ResourceKey<PlacedFeature> key) {
      if (getter == null) {
         return null;
      } else {
         Optional<Reference<PlacedFeature>> h = getter.get(key);
         return (Holder<PlacedFeature>)h.orElse(null);
      }
   }

   @Internal
   public static void initialize() {
      if (!didInit) {
         didInit = true;
         DatapackRegistryBuilder.addBootstrap(Registries.PLACED_FEATURE, PlacedFeatureManagerImpl::onBootstrap);
      }
   }

   private static void onBootstrap(BootstrapContext<PlacedFeature> context) {
      BOOTSTRAP_PLACED_FEATURES.emit(c -> c.bootstrap(context));
   }
}
