package org.betterx.wover.feature.mixin;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BiomeGenerationSettings.class})
public interface BiomeGenerationSettingsAccessor {
   @Accessor("features")
   List<HolderSet<PlacedFeature>> wover_getFeatures();

   @Accessor("features")
   @Mutable
   void wover_setFeatures(List<HolderSet<PlacedFeature>> var1);

   @Accessor("featureSet")
   void wover_setFeatureSet(Supplier<Set<PlacedFeature>> var1);

   @Accessor("boneMealFeatures")
   void wover_setFlowerFeatures(Supplier<List<Feature>> var1);

   @Accessor("carvers")
   HolderSet<WorldCarver> wover_getCarvers();

   @Accessor("carvers")
   @Mutable
   void wover_setCarvers(HolderSet<WorldCarver> var1);
}
