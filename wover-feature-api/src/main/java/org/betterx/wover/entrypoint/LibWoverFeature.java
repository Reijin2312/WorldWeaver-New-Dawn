package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.impl.FeatureManagerImpl;
import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;
import net.neoforged.bus.api.IEventBus;

public class LibWoverFeature {
   public static final ModCore C = ModCore.create("wover-feature", "wover");

   public LibWoverFeature(IEventBus modEventBus) {
      PlacementModifiersImpl.ensureStaticInitialization();
      FeatureManagerImpl.ensureStaticInitialization();
   }
}
