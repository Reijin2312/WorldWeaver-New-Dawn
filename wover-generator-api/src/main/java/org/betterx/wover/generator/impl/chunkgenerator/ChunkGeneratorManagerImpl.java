package org.betterx.wover.generator.impl.chunkgenerator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.mixin.ChunkGeneratorAccessor;
import org.betterx.wover.config.api.Configs;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.state.api.WorldConfig;
import org.betterx.wover.state.api.WorldState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.FeatureSorter.StepFeatureData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ChunkGeneratorManagerImpl {
   private static final List<String> GENERATOR_IDS = new ArrayList<>(1);

   @Internal
   public static Map<String, Supplier<TypeTemplate>> addGeneratorDSL(Map<String, Supplier<TypeTemplate>> map) {
      if (map.containsKey("minecraft:flat") && !ModCore.isDatagen()) {
         Map<String, Supplier<TypeTemplate>> nMap = new HashMap<>(map);
         GENERATOR_IDS.forEach(id -> nMap.put(id, DSL::remainder));
         return ImmutableMap.copyOf(nMap);
      } else {
         return map;
      }
   }

   @Internal
   public static void initialize() {
      register(WoverChunkGenerator.ID, WoverChunkGenerator.CODEC);
      WorldConfig.registerMod(LibWoverWorldGenerator.C);
      WorldLifecycle.CREATED_NEW_WORLD_FOLDER.subscribe(ChunkGeneratorManagerImpl::onWorldCreation, 20000);
   }

   private static void onWorldCreation(
      LevelStorageAccess storage, Provider access, Holder<WorldPreset> currentPreset, WorldDimensions dimensions, boolean recreated
   ) {
      WorldGeneratorConfigImpl.createWorldConfig(access, currentPreset, dimensions);
   }

   public static void onWorldReCreate(LevelStorageAccess storage, WorldDimensions selectedDimensions) {
      CompoundTag configuredPreset = WorldGeneratorConfigImpl.getPresetsNbtFromFolder(storage);
      Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions = WorldGeneratorConfigImpl.loadWorldDimensions(
         WorldState.allStageRegistryAccess(), configuredPreset
      );

      for (Entry<ResourceKey<LevelStem>, LevelStem> dimEntry : selectedDimensions.dimensions().entrySet()) {
         ChunkGenerator refDim = dimensions.get(dimEntry.getKey());
         if (refDim instanceof ConfiguredChunkGenerator refGen
            && refGen.wover_getConfiguredWorldPreset() != null
            && dimEntry.getValue().generator() instanceof ConfiguredChunkGenerator loadGen
            && loadGen.wover_getConfiguredWorldPreset() == null) {
            loadGen.wover_setConfiguredWorldPreset(refGen.wover_getConfiguredWorldPreset());
         }
      }
   }

   public static void register(Identifier location, MapCodec<? extends ChunkGenerator> codec) {
      String idString = location.toString();
      if (GENERATOR_IDS.contains(idString)) {
         throw new IllegalStateException("Duplicate generator id: " + idString);
      } else {
         GENERATOR_IDS.add(idString);
         BuiltInRegistryManager.register(BuiltInRegistries.CHUNK_GENERATOR, location, codec);
      }
   }

   public static String enumerateFeatureNamespaces(@NotNull ChunkGenerator chunkGenerator) {
      if (chunkGenerator instanceof ChunkGeneratorAccessor acc) {
         Supplier<List<StepFeatureData>> supplier = acc.wover_getFeaturesPerStep();
         if (supplier != null) {
            HashMap<String, Integer> namespaces = new HashMap<>();

            try {
               List<StepFeatureData> list = supplier.get();
               if (list != null) {
                  for (StepFeatureData features : list) {
                     if (features != null) {
                        for (PlacedFeature feature : features.features()) {
                           if (feature != null) {
                              String namespace = null;
                              if (WorldState.registryAccess() != null) {
                                 Identifier location = WorldState.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).getKey(feature);
                                 if (location != null) {
                                    namespace = location.getNamespace();
                                 }
                              }

                              if (namespace == null && feature.feature() != null && feature.feature().unwrapKey().isPresent()) {
                                 namespace = ((ResourceKey)feature.feature().unwrapKey().get()).identifier().getNamespace();
                              }

                              if (namespace == null) {
                                 namespace = "none";
                              }

                              namespaces.put(namespace, namespaces.getOrDefault(namespace, 0) + 1);
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var11) {
               LibWoverWorldGenerator.C.log.warn("Failed to enumerate feature namespaces", new Object[]{var11});
            }

            return namespaces.entrySet().stream().map(entry -> entry.getKey() + "(" + entry.getValue() + ")").reduce((a, b) -> a + ", " + b).orElse("none");
         }
      }

      return "unknown";
   }

   public static String printGeneratorInfo(@Nullable String className, @NotNull ChunkGenerator generator) {
      StringBuilder sb = new StringBuilder();
      sb.append(className == null ? generator.getClass().getSimpleName() : className)
         .append(" (")
         .append(Integer.toHexString(generator.hashCode()))
         .append(")");
      if (generator instanceof ConfiguredChunkGenerator cfg) {
         ResourceKey<WorldPreset> preset = cfg.wover_getConfiguredWorldPreset();
         sb.append("\n    preset     = ").append(preset == null ? "none" : preset.identifier());
      }

      if (generator instanceof NoiseBasedChunkGenerator noise) {
         Optional<ResourceKey<NoiseGeneratorSettings>> key = noise.generatorSettings().unwrapKey();
         sb.append("\n    noise      = ").append(key.isEmpty() ? "custom" : key.get().identifier());
      }

      if (generator instanceof ChunkGeneratorAccessor) {
         sb.append("\n    features   = ").append(enumerateFeatureNamespaces(generator));
      }

      return sb.toString();
   }

   public static void printDimensionInfo(WorldDimensions dimensionRegistry) {
      if ((Boolean)Configs.MAIN.verboseLogging.get()) {
         printDimensionInfo("World Dimensions", dimensionRegistry.dimensions().entrySet());
      }
   }

   public static void printDimensionInfo(Registry<LevelStem> dimensionRegistry) {
      if ((Boolean)Configs.MAIN.verboseLogging.get()) {
         printDimensionInfo("World Dimensions", dimensionRegistry.entrySet());
      }
   }

   public static void printDimensionInfo(String title, WorldDimensions dimensionRegistry) {
      printDimensionInfo(title, dimensionRegistry.dimensions().entrySet());
   }

   public static void printDimensionInfo(String title, Set<Entry<ResourceKey<LevelStem>, LevelStem>> levels) {
      StringBuilder output = new StringBuilder(title + ": ");

      for (Entry<ResourceKey<LevelStem>, LevelStem> entry : levels) {
         output.append("\n - ")
            .append(entry.getKey().identifier())
            .append(": ")
            .append("\n     ")
            .append(entry.getValue().generator().toString().replace("\n", "\n     "))
            .append("\n     ")
            .append(entry.getValue().generator().getBiomeSource().toString().replace("\n", "\n     "));
      }

      LibWoverWorldGenerator.C.log.info(output.toString());
   }
}

