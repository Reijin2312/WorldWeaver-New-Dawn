package org.betterx.wover.generator.api.biomesource;

import com.google.common.base.Stopwatch;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.impl.modification.BiomeTagModificationWorker;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithNoiseRelatedSettings;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithSeed;
import org.betterx.wover.common.generator.api.biomesource.MergeableBiomeSource;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.impl.biomesource.WoverBiomeSourceImpl;
import org.betterx.wover.state.api.WorldState;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.NotNull;

public abstract class WoverBiomeSource
   extends BiomeSource
   implements ReloadableBiomeSource,
   BiomeSourceWithNoiseRelatedSettings,
   BiomeSourceWithSeed,
   MergeableBiomeSource<WoverBiomeSource> {
   private volatile boolean didCreatePickers = false;
   Set<Holder<Biome>> dynamicPossibleBiomes = Set.of();
   protected long currentSeed;
   protected int maxHeight;

   public WoverBiomeSource(long seed) {
      this.currentSeed = seed;
   }

   @NotNull
   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      this.reloadBiomes();
      return this.dynamicPossibleBiomes.stream();
   }

   public final void setSeed(long seed) {
      if (seed != this.currentSeed) {
         LibWoverWorldGenerator.C.log.debug(this.toShortString() + "\n    --> new seed = " + seed);
         this.currentSeed = seed;
         this.initMap(seed);
      }
   }

   public final void setMaxHeight(int maxHeight) {
      if (this.maxHeight != maxHeight) {
         LibWoverWorldGenerator.C.log.debug(this.toShortString() + "\n    --> new height = " + maxHeight);
         this.maxHeight = maxHeight;
         this.onHeightChange(maxHeight);
      }
   }

   protected boolean wasBound() {
      return this.didCreatePickers;
   }

   protected abstract List<TagKey<Biome>> acceptedTags();

   protected abstract ResourceKey<Biome> fallbackBiome();

   public abstract String toShortString();

   protected abstract void onInitMap(long var1);

   protected abstract void onHeightChange(int var1);

   protected TagKey<Biome> defaultBiomeTag() {
      return this.acceptedTags().get(0);
   }

   protected List<WoverBiomeSource.TagToPicker> createFreshPickerMap() {
      return this.acceptedTags().stream().map(tag -> new WoverBiomeSource.TagToPicker((TagKey<Biome>)tag, new WoverBiomePicker(this.fallbackBiome()))).toList();
   }

   public void onLoadGeneratorSettings(NoiseGeneratorSettings generator) {
      this.setMaxHeight(generator.noiseSettings().height());
   }

   protected void onFinishBiomeRebuild(List<WoverBiomeSource.TagToPicker> pickerMap) {
      for (WoverBiomeSource.TagToPicker tagToPicker : pickerMap) {
         tagToPicker.picker.rebuild();
      }
   }

   @NotNull
   protected String getNamespaces() {
      return WoverBiomeSourceImpl.getNamespaces(this.possibleBiomes());
   }

   protected TagKey<Biome> tagForUnknownBiome(Holder<Biome> biomeHolder, ResourceKey<Biome> biomeKey) {
      for (TagKey<Biome> type : this.acceptedTags()) {
         if (biomeHolder.is(type)) {
            return type;
         }
      }

      return this.defaultBiomeTag();
   }

   protected boolean addToPicker(BiomeData biomeData, TagKey<Biome> type, WoverBiomePicker picker) {
      picker.addBiome(biomeData);
      return true;
   }

   protected final synchronized void rebuildBiomes(boolean force) {
      if (force || !this.didCreatePickers) {
         LibWoverWorldGenerator.C.log.verbose("Updating Pickers for " + this.toShortString());
         List<WoverBiomeSource.TagToPicker> pickers = this.createFreshPickerMap();
         this.dynamicPossibleBiomes = WoverBiomeSourceImpl.populateBiomePickers(pickers, this::addToPicker);
         if (this.dynamicPossibleBiomes == null) {
            this.dynamicPossibleBiomes = Set.of();
         }

         this.didCreatePickers = true;
         this.onFinishBiomeRebuild(pickers);
      }
   }

   protected synchronized void reloadBiomes(boolean force) {
      this.rebuildBiomes(force);
      this.initMap(this.currentSeed);
   }

   public void reloadBiomes() {
      this.reloadBiomes(true);
   }

   protected final void initMap(long seed) {
      LibWoverWorldGenerator.C.log.debug(this.toShortString() + "\n    --> Map Update");
      this.onInitMap(seed);
   }

   public WoverBiomeSource mergeWithBiomeSource(BiomeSource inputBiomeSource) {
      Stopwatch sw = Stopwatch.createStarted();
      RegistryAccess access = WorldState.registryAccess();
      if (access == null) {
         access = WorldState.allStageRegistryAccess();
         if (access == null) {
            LibWoverWorldGenerator.C.log.error("Unable to merge Biome Sources");
            return this;
         }

         LibWoverWorldGenerator.C.log.verbose("Registries were not finalized before merging biome sources!");
      }

      Registry<Biome> biomes = access.lookupOrThrow(Registries.BIOME);
      BiomeTagModificationWorker biomeTagWorker = new BiomeTagModificationWorker();
      int biomesAdded = 0;

      try {
         for (Holder<Biome> biomeHolder : inputBiomeSource.possibleBiomes()) {
            if (biomeHolder.unwrapKey().isPresent()) {
               ResourceKey<Biome> key = (ResourceKey<Biome>)biomeHolder.unwrapKey().orElseThrow();
               TagKey<Biome> tag = this.tagForUnknownBiome(biomeHolder, key);
               if (tag != null && !biomeHolder.is(tag)) {
                  biomeTagWorker.addBiomeToTag(tag, biomes, key, biomeHolder);
                  biomesAdded++;
               }
            }
         }

         biomeTagWorker.finished();
      } catch (RuntimeException var11) {
         LibWoverWorldGenerator.C.log.error("Error while rebuilding BiomeSources!", var11);
      } catch (Exception var12) {
         LibWoverWorldGenerator.C.log.error("Error while rebuilding BiomeSources!", var12);
      }

      this.reloadBiomes();
      if (biomesAdded > 0) {
         LibWoverWorldGenerator.C.log.info("Merged {} biomes to {} in {}", new Object[]{biomesAdded, this.toShortString(), sw});
      }

      return this;
   }

   @FunctionalInterface
   public interface PickerAdder {
      boolean add(BiomeData var1, TagKey<Biome> var2, WoverBiomePicker var3);
   }

   @FunctionalInterface
   public interface PickerMapFactory {
      List<WoverBiomeSource.TagToPicker> create(Registry<BiomeData> var1);
   }

   public record TagToPicker(TagKey<Biome> tag, WoverBiomePicker picker) {
   }
}

