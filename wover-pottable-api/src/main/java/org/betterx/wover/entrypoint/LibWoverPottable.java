package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;

import net.neoforged.bus.api.IEventBus;

public class LibWoverPottable {
    public static final ModCore C = ModCore.create("wover-pottable", "wover");

    public LibWoverPottable(IEventBus modEventBus) {
        C.registerDatapackListener(modEventBus);
    }
}
