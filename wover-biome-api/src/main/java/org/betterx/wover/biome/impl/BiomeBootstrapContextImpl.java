package org.betterx.wover.biome.impl;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.core.api.registry.CustomBootstrapContext;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BiomeBootstrapContextImpl extends CustomBootstrapContext<Biome, BiomeBootstrapContextImpl> implements BiomeBootstrapContext {
   private final List<BiomeBuilder<?>> registeredBuilders = new LinkedList<>();

   @Override
   public void register(@NotNull BiomeBuilder<?> builder) {
      this.registeredBuilders.add(builder);
   }

   @Internal
   public final void bootstrapBiome(BootstrapContext<Biome> context) {
      for (BiomeBuilder<?> builder : this.registeredBuilders) {
         builder.registerBiome(context);
      }
   }

   @Internal
   public final void bootstrapBiomeData(BootstrapContext<BiomeData> context) {
      for (BiomeBuilder<?> builder : this.registeredBuilders) {
         builder.registerBiomeData(context);
      }
   }

   @Internal
   public final void bootstrapSurfaceRules(BootstrapContext<AssignedSurfaceRule> context) {
      for (BiomeBuilder<?> builder : this.registeredBuilders) {
         builder.registerSurfaceRule(context);
      }
   }

   public final void prepareTags(TagBootstrapContext<Biome> context) {
      for (BiomeBuilder<?> builder : this.registeredBuilders) {
         builder.registerBiomeTags(context);
      }
   }

   public void onBootstrapContextChange(BiomeBootstrapContextImpl bootstrapContext) {
      LibWoverBiome.C.log.debug("Biome getter changed, resetting bootstrap context");
      BiomeManagerImpl.BOOTSTRAP_BIOMES_WITH_DATA.emit(c -> c.bootstrap(bootstrapContext));
   }
}
