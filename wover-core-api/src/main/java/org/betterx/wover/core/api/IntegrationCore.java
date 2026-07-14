package org.betterx.wover.core.api;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.loader.api.FabricLoader;

public class IntegrationCore {
    public static boolean hasMod(String namespace) {
        return FabricLoader.getInstance()
                           .getModContainer(namespace)
                           .isPresent();
    }

    public static final boolean RUNS_TERRABLENDER = hasMod("terrablender");
    public static final boolean RUNS_NULLSCAPE = hasMod("nullscape");

    public static final ModCore MINECRAFT = ModCore.create(ResourceLocation.DEFAULT_NAMESPACE);
    public static final ModCore BETTER_END = ModCore.create("betterend");
    public static final ModCore BETTER_NETHER = ModCore.create("betternether");
}
