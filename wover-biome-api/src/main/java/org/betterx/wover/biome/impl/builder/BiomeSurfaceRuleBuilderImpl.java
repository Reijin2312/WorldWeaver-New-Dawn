package org.betterx.wover.biome.impl.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import org.betterx.wover.surface.impl.SurfaceRuleBuilderImpl;
import org.betterx.wover.surface.impl.SurfaceRuleRegistryImpl;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public class BiomeSurfaceRuleBuilderImpl<B extends BiomeBuilder<B>>
   extends SurfaceRuleBuilderImpl<BiomeSurfaceRuleBuilder<B>>
   implements BiomeSurfaceRuleBuilder<B> {
   private final B sourceBuilder;

   public BiomeSurfaceRuleBuilderImpl(BiomeKey<?> biomeKey, B sourceBuilder) {
      this.biome(biomeKey.key);
      this.sourceBuilder = sourceBuilder;
   }

   public void register(@NotNull BootstrapContext<AssignedSurfaceRule> ctx) {
      ResourceKey<AssignedSurfaceRule> ruleKey = SurfaceRuleRegistry.createKey(this.biomeKey.identifier());
      SurfaceRuleRegistryImpl.register(ctx, ruleKey, this.biomeKey, this.getRuleSource(), this.sortPriority);
   }

   @Override
   public B finishSurface() {
      return this.sourceBuilder;
   }
}
