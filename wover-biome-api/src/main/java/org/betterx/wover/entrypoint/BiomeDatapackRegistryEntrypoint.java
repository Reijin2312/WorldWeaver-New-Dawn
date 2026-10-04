package org.betterx.wover.entrypoint;

import org.betterx.wover.biome.impl.BiomeManagerImpl;
import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.biome.impl.modification.BiomeModificationRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryEntrypoint;

public class BiomeDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
   public void registerDatapackRegistries() {
      BiomeManagerImpl.initialize();
      BiomeDataRegistryImpl.initialize();
      BiomeModificationRegistryImpl.initialize();
   }
}
