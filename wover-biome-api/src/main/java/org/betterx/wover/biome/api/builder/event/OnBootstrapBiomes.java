package org.betterx.wover.biome.api.builder.event;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.events.api.Subscriber;

public interface OnBootstrapBiomes extends Subscriber {
   void bootstrap(BiomeBootstrapContext var1);
}
