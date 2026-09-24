package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class WoverBiomeDataProvider extends WoverRegistryContentProvider<BiomeData> {
   public WoverBiomeDataProvider(@NotNull ModCore modCore) {
      this(modCore, modCore.id("default"));
   }

   public WoverBiomeDataProvider(@NotNull ModCore modCore, @NotNull Identifier providerId) {
      super(modCore, providerId.toString() + " (Biome Data)", BiomeDataRegistry.BIOME_DATA_REGISTRY);
   }

   protected abstract void bootstrap(BootstrapContext<BiomeData> var1);
}
