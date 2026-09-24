package org.betterx.wover.biome.impl;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.core.api.registry.CustomBootstrapContext;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import java.util.Objects;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BiomeManagerImpl {
   public static final EventImpl<OnBootstrapRegistry<Biome>> BOOTSTRAP_BIOME_REGISTRY = new EventImpl("BOOTSTRAP_BIOME_REGISTRY");
   public static final EventImpl<OnBootstrapBiomes> BOOTSTRAP_BIOMES_WITH_DATA = new EventImpl("BOOTSTRAP_BIOMES_WITH_DATA");
   private static boolean didInit = false;

   private static void onBootstrap(BootstrapContext<Biome> ctx) {
      BOOTSTRAP_BIOME_REGISTRY.emit(c -> c.bootstrap(ctx));
   }

   @Internal
   public static void initialize() {
      if (!didInit) {
         didInit = true;
         DatapackRegistryBuilder.addBootstrap(Registries.BIOME, BiomeManagerImpl::onBootstrap);
         BOOTSTRAP_BIOME_REGISTRY.subscribe(BiomeManagerImpl::onBootstrapBiomeRegistry, 500);
         BiomeDataRegistryImpl.BOOTSTRAP_BIOME_DATA_REGISTRY.subscribe(BiomeManagerImpl::onBootstrapBiomeDataRegistry, 500);
         SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY.subscribe(BiomeManagerImpl::onBootstrapSurfaceRuleRegistry, 500);
         TagManager.BIOMES.bootstrapEvent().subscribe(BiomeManagerImpl::onBootstrapTags, 500);
      }
   }

   private static <B> BiomeBootstrapContextImpl initContext(BootstrapContext<B> lookupContext) {
      return (BiomeBootstrapContextImpl)CustomBootstrapContext.initContext(lookupContext, Registries.BIOME, BiomeBootstrapContextImpl::new);
   }

   private static void onBootstrapBiomeDataRegistry(BootstrapContext<BiomeData> biomeDataBootstrapContext) {
      BiomeBootstrapContextImpl context = initContext(biomeDataBootstrapContext);
      context.bootstrapBiomeData(biomeDataBootstrapContext);
   }

   private static void onBootstrapBiomeRegistry(BootstrapContext<Biome> biomeBootstrapContext) {
      BiomeBootstrapContextImpl context = initContext(biomeBootstrapContext);
      context.bootstrapBiome(biomeBootstrapContext);
   }

   private static void onBootstrapSurfaceRuleRegistry(BootstrapContext<AssignedSurfaceRule> assignedSurfaceRuleBootstrapContext) {
      BiomeBootstrapContextImpl context = initContext(assignedSurfaceRuleBootstrapContext);
      context.bootstrapSurfaceRules(assignedSurfaceRuleBootstrapContext);
   }

   private static void onBootstrapTags(TagBootstrapContext<Biome> biomeTagBootstrapContext) {
      BiomeBootstrapContextImpl context = Objects.requireNonNull(
         initContext(null), "No biome bootstrap context: biome tags are loaded before the biome registry was bootstrapped"
      );
      context.prepareTags(biomeTagBootstrapContext);
   }

   public static ResourceKey<Biome> createKey(Identifier biomeID) {
      return ResourceKey.create(Registries.BIOME, biomeID);
   }

   public static BiomeKey<BiomeBuilder.Vanilla> vanilla(Identifier location) {
      return new VanillaKeyImpl(location);
   }

   public static BiomeKey<BiomeBuilder.Wrapped> wrapped(@NotNull ResourceKey<Biome> key) {
      return new WrappedKeyImpl(key.identifier());
   }
}
