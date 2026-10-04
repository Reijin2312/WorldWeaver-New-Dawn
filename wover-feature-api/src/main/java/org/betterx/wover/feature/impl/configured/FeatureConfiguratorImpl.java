package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.feature.impl.placed.FeaturePlacementBuilderImpl;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class FeatureConfiguratorImpl {
   public static final EventImpl<OnBootstrapRegistry<Feature>> BOOTSTRAP_FEATURES = new EventImpl("BOOTSTRAP_FEATURES");
   private static boolean didInit = false;
   @Nullable
   public final ResourceKey<Feature> key;
   @Nullable
   protected final BootstrapContext<Feature> bootstrapContext;
   private ResourceKey<PlacedFeature> transitiveFeatureKey;
   private BootstrapContext<PlacedFeature> transitiveBootstrapContext;

   @Internal
   public static void initialize() {
      if (!didInit) {
         didInit = true;
         DatapackRegistryBuilder.addBootstrap(Registries.FEATURE, FeatureConfiguratorImpl::onBootstrap);
      }
   }

   private static void onBootstrap(BootstrapContext<Feature> context) {
      BOOTSTRAP_FEATURES.emit(c -> c.bootstrap(context));
   }

   @NotNull
   public static ResourceKey<Feature> createKey(@NotNull Identifier id) {
      return ResourceKey.create(Registries.FEATURE, id);
   }

   @Nullable
   public static Holder<Feature> getHolder(@Nullable HolderGetter<Feature> getter, @NotNull ResourceKey<Feature> key) {
      if (getter == null) {
         return null;
      } else {
         Optional<Reference<Feature>> h = getter.get(key);
         return (Holder<Feature>)h.orElse(null);
      }
   }

   FeatureConfiguratorImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      this.key = key;
      this.bootstrapContext = ctx;
   }

   void setTransitive(BootstrapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> key) {
      this.transitiveBootstrapContext = ctx;
      this.transitiveFeatureKey = key;
   }

   @Internal
   public ResourceKey<PlacedFeature> getTransitiveFeatureKey() {
      return this.transitiveFeatureKey;
   }

   @Internal
   public BootstrapContext<PlacedFeature> getTransitiveBootstrapContext() {
      return this.transitiveBootstrapContext;
   }

   @NotNull
   protected abstract Feature createFeature();

   public Holder<Feature> register() {
      if (this.key == null) {
         throw new IllegalStateException("A ResourceKey can not be null if a feature should be registered!");
      } else {
         if (this.bootstrapContext == null) {
            this.throwStateError("Can not register a feature without a bootstrap context!");
         }

         return this.bootstrapContext.register(this.key, this.createFeature());
      }
   }

   public Holder<Feature> directHolder() {
      return Holder.direct(this.createFeature());
   }

   public FeaturePlacementBuilderImpl inlinePlace() {
      return FeaturePlacementBuilderImpl.withTransitive(this, (cfg, plc) -> {
         RandomPatchImpl res = new RandomPatchImpl(this.bootstrapContext, cfg);
         res.setTransitive(this.transitiveBootstrapContext, plc);
         return res;
      });
   }

   void throwStateError(String message) {
      throw new IllegalStateException(message + (this.key == null ? "" : "(" + this.key.identifier() + ")"));
   }
}
