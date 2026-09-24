package org.betterx.wover.generator.mixin.generator;

import org.betterx.wover.generator.impl.chunkgenerator.ConfiguredChunkGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ChunkGenerator.class})
public class ChunkGeneratorMixin implements ConfiguredChunkGenerator {
   @Unique
   private ResourceKey<WorldPreset> wover_configuredWorldPreset;

   @Override
   public ResourceKey<WorldPreset> wover_getConfiguredWorldPreset() {
      return this.wover_configuredWorldPreset;
   }

   @Override
   public void wover_setConfiguredWorldPreset(ResourceKey<WorldPreset> preset) {
      this.wover_configuredWorldPreset = preset;
   }
}

