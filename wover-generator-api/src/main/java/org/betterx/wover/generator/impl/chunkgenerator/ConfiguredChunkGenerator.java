package org.betterx.wover.generator.impl.chunkgenerator;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public interface ConfiguredChunkGenerator {
   ResourceKey<WorldPreset> wover_getConfiguredWorldPreset();

   void wover_setConfiguredWorldPreset(ResourceKey<WorldPreset> var1);

   default String wover_getConfiguredWorldPresetString() {
      ResourceKey<WorldPreset> preset = this.wover_getConfiguredWorldPreset();
      return preset == null ? null : preset.identifier().toString();
   }

   default void wover_setConfiguredWorldPresetString(String preset) {
      this.wover_setConfiguredWorldPreset(
         preset == null ? null : ResourceKey.create(Registries.WORLD_PRESET, Identifier.parse(preset))
      );
   }
}
