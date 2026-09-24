package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.registry.DatapackRegistryEntrypoint;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import org.betterx.wover.feature.impl.placed.PlacedFeatureManagerImpl;

public class FeatureDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
   public void registerDatapackRegistries() {
      FeatureConfiguratorImpl.initialize();
      PlacedFeatureManagerImpl.initialize();
   }
}
