package org.betterx.wover.biome.impl.modification;

import com.google.common.base.Stopwatch;
import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.common.generator.api.chunkgenerator.RebuildableFeaturesPerStep;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.state.api.WorldState;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BiomeModificationRegistryImpl {
   public static final EventImpl<OnBootstrapRegistry<BiomeModification>> BOOTSTRAP_BIOME_MODIFICATION_REGISTRY = new EventImpl(
      "BOOTSTRAP_BIOME_MODIFICATION_REGISTRY"
   );
   private static boolean didInit = false;

   @Internal
   public static void initialize() {
      if (!didInit) {
         didInit = true;
         DatapackRegistryBuilder.register(
            BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY, BiomeModification.CODEC, BiomeModificationRegistryImpl::onBootstrap
         );
         WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(BiomeModificationRegistryImpl::whenReady, 100000000);
      }
   }

   private static void onBootstrap(BootstrapContext<BiomeModification> ctx) {
      BOOTSTRAP_BIOME_MODIFICATION_REGISTRY.emit(c -> c.bootstrap(ctx));
   }

   private static void whenReady(
      LevelStorageAccess storageSource,
      PackRepository packRepository,
      LayeredRegistryAccess<RegistryLayer> registries,
      WorldData worldData,
      WorldGenSettings worldGenSettings
   ) {
      Stopwatch sw = Stopwatch.createStarted();
      RegistryAccess registryAccess = WorldState.registryAccess();
      Registry<BiomeModification> modifications = (Registry<BiomeModification>)registryAccess.lookup(BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY)
         .orElse(null);
      if (modifications == null) {
         LibWoverBiome.C.log.error("Biome Modification Registry is missing. Cannot apply Biome Modifications.");
      } else {
         Registry<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);
         List<ResourceKey<Biome>> keys = biomes.entrySet()
            .stream()
            .map(Entry::getKey)
            .sorted(Comparator.comparingInt(key -> biomes.getId((Biome)biomes.getOrThrow(key).value())))
            .toList();
         BiomeTagModificationWorker biomeTagWorker = new BiomeTagModificationWorker();
         List<BiomeModification> biomeModifications = modifications.stream().toList();
         int biomesChanged = 0;
         int biomesProcessed = 0;
         int modifiersApplied = 0;
         int tagsAdded = 0;

         for (ResourceKey<Biome> biomeKey : keys) {
            BiomePredicate.Context context = BiomePredicate.Context.of(registryAccess, biomeKey);
            if (context == null) {
               LibWoverBiome.C.log.warn("Failed to get biome context for {}", new Object[]{biomeKey.identifier()});
            } else {
               biomesProcessed++;
               GenerationSettingsWorker worker = null;
               MobSettingsWorker mobWorker = null;
               boolean didChangeBiome = false;

               for (BiomeModification modification : biomeModifications) {
                  if (modification.predicate().test(context)) {
                     if (worker == null) {
                        worker = new GenerationSettingsWorker(registryAccess, context.biome);
                     }

                     if (mobWorker == null) {
                        mobWorker = new MobSettingsWorker(context.biome);
                     }

                     if (modification.biomeTags() != null) {
                        for (TagKey<Biome> tag : modification.biomeTags()) {
                           if (biomeTagWorker.addBiomeToTag(tag, context)) {
                              tagsAdded++;
                              didChangeBiome = true;
                           }
                        }
                     }

                     modification.apply(worker, mobWorker);
                     modifiersApplied++;
                  }
               }

               if (worker != null && worker.finished()) {
                  didChangeBiome = true;
               }

               if (mobWorker != null && mobWorker.finished()) {
                  didChangeBiome = true;
               }

               if (didChangeBiome) {
                  biomesChanged++;
               }
            }
         }

         biomeTagWorker.finished();
         if (tagsAdded > 0) {
            Registry<LevelStem> dimensions = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
            dimensions.forEach(stem -> {
               if (stem.generator().getBiomeSource() instanceof ReloadableBiomeSource reloadable) {
                  reloadable.reloadBiomes();
               }
            });
         }

         if (biomesProcessed > 0) {
            Registry<LevelStem> dimensions = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
            dimensions.forEach(stem -> {
               if (stem.generator() instanceof RebuildableFeaturesPerStep<?> generator) {
                  generator.wover_rebuildFeaturesPerStep();
               }
            });
            LibWoverBiome.C
               .log
               .info(
                  "Applied {} biome modifications and added {} tags to {} of {} biomes in {}",
                  new Object[]{modifiersApplied, tagsAdded, biomesChanged, biomesProcessed, sw.stop()}
               );
         }
      }
   }

   public static ResourceKey<BiomeModification> createKey(Identifier modificationID) {
      return ResourceKey.create(BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY, modificationID);
   }
}
