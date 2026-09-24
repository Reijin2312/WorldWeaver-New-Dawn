package org.betterx.wover.biome.impl.modification;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import org.betterx.wover.entrypoint.LibWoverFeature;
import org.betterx.wover.feature.mixin.BiomeGenerationSettingsAccessor;
import org.betterx.wover.util.MutableHolderSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FeatureTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

public class GenerationSettingsWorker {
   private final Registry<WorldCarver> carverLookup;
   private final Registry<PlacedFeature> featureLookup;
   private final BiomeGenerationSettings generationSettings;
   private final Biome biome;
   MutableHolderSet<WorldCarver> customizedCarvers;
   List<HolderSet<PlacedFeature>> customizedFeatures;

   public GenerationSettingsWorker(RegistryAccess registries, Biome biome) {
      this.biome = biome;
      this.generationSettings = biome.getGenerationSettings();
      this.carverLookup = registries.lookupOrThrow(Registries.CARVER);
      this.featureLookup = registries.lookupOrThrow(Registries.PLACED_FEATURE);
   }

   private void unfreezeCarvers() {
      if (this.customizedCarvers == null && this.generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
         this.customizedCarvers = MutableHolderSet.of(accessor.wover_getCarvers());
         accessor.wover_setCarvers(this.customizedCarvers);
      } else if (!(this.generationSettings instanceof BiomeGenerationSettingsAccessor)) {
         LibWoverFeature.C.LOG.error("Cannot unfreeze generation carvers");
      }
   }

   private void freezeCarvers() {
      if (this.customizedCarvers != null && this.generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
         accessor.wover_setCarvers(this.customizedCarvers.asDirectHolderSet());
         this.customizedCarvers = null;
      }
   }

   private void unfreezeFeatures() {
      if (this.customizedFeatures == null && this.generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
         this.customizedFeatures = new LinkedList<>(accessor.wover_getFeatures());
         accessor.wover_setFeatures(this.customizedFeatures);
      } else if (!(this.generationSettings instanceof BiomeGenerationSettingsAccessor)) {
         LibWoverFeature.C.LOG.error("Cannot unfreeze generation features");
      }
   }

   private void freezeFeatures() {
      if (this.customizedFeatures != null && this.generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
         accessor.wover_setFeatures(ImmutableList.copyOf(this.customizedFeatures));
         accessor.wover_setFeatureSet(Suppliers.memoize(this::createPlacedFeatrueSet));
         accessor.wover_setFlowerFeatures(Suppliers.memoize(this::createFlowerFeatures));
         this.customizedFeatures = null;
      }
   }

   public boolean finished() {
      boolean res = this.customizedCarvers != null || this.customizedFeatures != null;
      this.freezeCarvers();
      this.freezeFeatures();
      return res;
   }

   private Set<PlacedFeature> createPlacedFeatrueSet() {
      return this.getFlatFeatureStream().collect(Collectors.toSet());
   }

   private List<Feature> createFlowerFeatures() {
      return this.getFlatFeatureStream()
         .flatMap(PlacedFeature::getFeatures)
         .filter(feature -> feature.is(FeatureTags.CAN_SPAWN_FROM_BONE_MEAL))
         .map(holder -> holder.value())
         .collect(ImmutableList.toImmutableList());
   }

   @NotNull
   private Stream<PlacedFeature> getFlatFeatureStream() {
      if (this.generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
         return accessor.wover_getFeatures().stream().flatMap(HolderSet::stream).map(Holder::value);
      } else {
         LibWoverFeature.C.LOG.error("Cannot get flat feature stream from generation settings");
         return Stream.empty();
      }
   }

   public void addFeatures(FeatureMap features) {
      boolean hasNewFeatures = false;

      for (int index = 0; index < features.size(); index++) {
         if (index < Decoration.values().length && !features.get(index).isEmpty()) {
            hasNewFeatures = true;
            break;
         }
      }

      if (hasNewFeatures) {
         this.unfreezeFeatures();

         for (int indexx = 0; indexx < features.size(); indexx++) {
            if (indexx < Decoration.values().length) {
               LinkedList<Holder<PlacedFeature>> newFeatures = features.get(indexx);
               if (!newFeatures.isEmpty()) {
                  Decoration step = Decoration.values()[indexx];
                  List<Holder<PlacedFeature>> featuresInStep = new ArrayList<>(FeatureMap.getFeatures(this.customizedFeatures, step).stream().toList());
                  featuresInStep.addAll(newFeatures);
                  this.customizedFeatures.set(indexx, HolderSet.direct(featuresInStep));
               }
            }
         }
      }
   }
}
