package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.impl.registry.ModCoreImpl;
import org.betterx.wover.ui.api.VersionChecker;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("wover")
public class Wover {
    public static final ModCore C = ModCoreImpl.GLOBAL_MOD;

    public Wover(IEventBus modEventBus) {
        C.registerDatapackListener(modEventBus);
        org.betterx.wover.block.api.BlockRegistry.hook(modEventBus);
        org.betterx.wover.item.api.ItemRegistry.hook(modEventBus);
        VersionChecker.registerMod(C);

        new LibWoverCommon(modEventBus);
        new LibWoverCore(modEventBus);
        new LibWoverMath(modEventBus);
        new LibWoverDatagen(modEventBus);
        new LibWoverEvents(modEventBus);
        new LibWoverUi(modEventBus);
        new LibWoverTag(modEventBus);
        new LibWoverItem(modEventBus);
        new LibWoverBlock(modEventBus);
        new LibWoverRecipe(modEventBus);
        new LibWoverWorldPreset(modEventBus);
        new LibWoverSurface(modEventBus);
        new LibWoverStructure(modEventBus);
        new LibWoverFeature(modEventBus);
        new LibWoverBiome(modEventBus);
        new LibWoverWorldGenerator(modEventBus);
        new LibWoverPottable(modEventBus);
    }
}
