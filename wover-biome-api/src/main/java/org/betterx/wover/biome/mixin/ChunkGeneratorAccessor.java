package org.betterx.wover.biome.mixin;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter.StepFeatureData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ChunkGenerator.class})
public interface ChunkGeneratorAccessor {
   @Accessor("biomeSource")
   @Mutable
   void wover_setBiomeSource(BiomeSource var1);

   @Accessor("featuresPerStep")
   @Mutable
   void wover_setFeaturesPerStep(Supplier<List<StepFeatureData>> var1);

   @Accessor("featuresPerStep")
   @Mutable
   Supplier<List<StepFeatureData>> wover_getFeaturesPerStep();
}
