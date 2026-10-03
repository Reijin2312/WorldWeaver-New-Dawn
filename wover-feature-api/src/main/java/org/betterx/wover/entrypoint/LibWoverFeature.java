package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.impl.FeatureManagerImpl;
import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

public class LibWoverFeature {
   public static final ModCore C = ModCore.create("wover-feature", "wover");

   public LibWoverFeature(IEventBus modEventBus) {
      PlacementModifiersImpl.ensureStaticInitialization();
      FeatureManagerImpl.ensureStaticInitialization();
      modEventBus.addListener(RegisterEvent.class, PlacementModifiersImpl::onRegister);
      modEventBus.addListener(RegisterEvent.class, FeatureManagerImpl::onRegister);
   }
}
