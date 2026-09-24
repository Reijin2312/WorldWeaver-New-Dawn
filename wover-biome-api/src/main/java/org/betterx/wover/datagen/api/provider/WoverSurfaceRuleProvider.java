package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class WoverSurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
   public WoverSurfaceRuleProvider(@NotNull ModCore modCore) {
      this(modCore, modCore.id("default"));
   }

   public WoverSurfaceRuleProvider(@NotNull ModCore modCore, @NotNull Identifier providerId) {
      super(modCore, providerId.toString() + " (Surface Rules)", SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);
   }

   protected abstract void bootstrap(BootstrapContext<AssignedSurfaceRule> var1);
}
