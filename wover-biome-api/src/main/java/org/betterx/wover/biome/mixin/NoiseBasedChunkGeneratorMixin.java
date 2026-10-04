package org.betterx.wover.biome.mixin;

import org.betterx.wover.biome.impl.modification.ChunkGeneratorHelper;
import org.betterx.wover.common.generator.api.chunkgenerator.RebuildableFeaturesPerStep;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({NoiseBasedChunkGenerator.class})
public abstract class NoiseBasedChunkGeneratorMixin implements RebuildableFeaturesPerStep<NoiseBasedChunkGenerator> {
   public void wover_rebuildFeaturesPerStep() {
      ChunkGenerator g = (ChunkGenerator)(Object)this;
      ChunkGeneratorHelper.rebuildFeaturesPerStep(g, g.getBiomeSource());
   }
}
