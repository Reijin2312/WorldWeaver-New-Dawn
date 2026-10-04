package org.betterx.wover.biome.impl.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import net.minecraft.data.worldgen.BootstrapContext;

public class VanillaBiomeBuilderImpl extends BiomeBuilder.Vanilla {
   public VanillaBiomeBuilderImpl(BiomeBootstrapContext context, BiomeKey<BiomeBuilder.Vanilla> key) {
      super(context, key);
   }

   @Override
   public void registerBiomeData(BootstrapContext<BiomeData> dataContext) {
      if (this.fogDensity != 1.0F || !this.parameters.isEmpty()) {
         dataContext.register(
            this.key.dataKey, new BiomeData(this.fogDensity, this.key.key, new BiomeGenerationDataContainer(this.parameters, this.intendedPlacement))
         );
      }
   }
}
