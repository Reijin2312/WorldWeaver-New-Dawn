package org.betterx.wover.generator.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.preset.api.WorldPresetInfo;
import org.betterx.wover.preset.api.WorldPresetInfoBuilder;
import org.betterx.wover.preset.api.WorldPresetInfoRegistry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class WorldPresetInfoProvider extends WoverRegistryContentProvider<WorldPresetInfo> {
   public WorldPresetInfoProvider(ModCore modCore) {
      super(modCore, "World Preset Info", WorldPresetInfoRegistry.WORLD_PRESET_INFO_REGISTRY);
   }

   protected void bootstrap(BootstrapContext<WorldPresetInfo> context) {
      WorldPresetInfoBuilder.start(context)
         .order(1500)
         .overworldOverride(WorldPresets.NORMAL)
         .register(org.betterx.wover.generator.api.preset.WorldPresets.WOVER_WORLD);
      WorldPresetInfoBuilder.start(context)
         .order(2500)
         .overworldOverride(WorldPresets.AMPLIFIED)
         .endOverride(org.betterx.wover.generator.api.preset.WorldPresets.WOVER_WORLD)
         .register(org.betterx.wover.generator.api.preset.WorldPresets.WOVER_WORLD_AMPLIFIED);
      WorldPresetInfoBuilder.start(context)
         .order(3500)
         .overworldOverride(WorldPresets.LARGE_BIOMES)
         .register(org.betterx.wover.generator.api.preset.WorldPresets.WOVER_WORLD_LARGE);
   }
}
