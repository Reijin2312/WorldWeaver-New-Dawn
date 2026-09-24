package org.betterx.wover.generator.impl.chunkgenerator;

import com.mojang.serialization.Lifecycle;
import org.betterx.wover.common.generator.api.chunkgenerator.RestorableBiomeSource;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.state.api.WorldState;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.RegistryAccess.ImmutableRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.jetbrains.annotations.ApiStatus.Internal;

public class WoverChunkGeneratorImpl {
   @Internal
   public static void initialize() {
      WorldLifecycle.MINECRAFT_SERVER_READY.subscribe(WoverChunkGeneratorImpl::restoreInitialBiomeSourceInAllDimensions);
      WorldLifecycle.ON_DIMENSION_LOAD.subscribe(WoverChunkGeneratorImpl::repairBiomeSourceInAllDimensions);
      WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(WoverChunkGeneratorImpl::printInfo, -1000);
   }

   private static void printInfo(
      LevelStorageAccess levelStorageAccess,
      PackRepository packRepository,
      LayeredRegistryAccess<RegistryLayer> registryLayerLayeredRegistryAccess,
      WorldData worldData,
      WorldGenSettings worldGenSettings
   ) {
      if (WorldState.registryAccess() != null) {
         Registry<LevelStem> dimensionsRegistry = WorldState.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
         ChunkGeneratorManagerImpl.printDimensionInfo(dimensionsRegistry);
      }
   }

   private static void restoreInitialBiomeSourceInAllDimensions(LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem) {
      for (Entry<ResourceKey<LevelStem>, LevelStem> entry : WorldState.registryAccess().lookupOrThrow(Registries.LEVEL_STEM).entrySet()) {
         ResourceKey<LevelStem> key = entry.getKey();
         LevelStem stem = entry.getValue();
         if (stem.generator() instanceof RestorableBiomeSource<?> generator) {
            generator.restoreInitialBiomeSource(key);
         }
      }
   }

   private static LayeredRegistryAccess<RegistryLayer> repairBiomeSourceInAllDimensions(LayeredRegistryAccess<RegistryLayer> registries) {
      WorldGeneratorConfigImpl.migrateGeneratorSettings();
      Frozen access = registries.compositeAccess();
      Registry<LevelStem> dimensions = access.lookupOrThrow(Registries.LEVEL_STEM);
      BiomeRepairHelper biomeHelper = new BiomeRepairHelper();
      Registry<LevelStem> changedDimensions = biomeHelper.repairBiomeSourceInAllDimensions(access, dimensions);
      if (dimensions != changedDimensions) {
         LibWoverWorldGenerator.C.log.verbose("Loading World with initially configured Dimensions.");
         registries = registries.replaceFrom(RegistryLayer.DIMENSIONS, new Frozen[]{new ImmutableRegistryAccess(List.of(changedDimensions)).freeze()});
      }

      return registries;
   }

   public static Registry<LevelStem> replaceGenerator(
      ResourceKey<LevelStem> dimensionKey,
      ResourceKey<DimensionType> dimensionTypeKey,
      RegistryAccess registryAccess,
      Set<Entry<ResourceKey<LevelStem>, LevelStem>> dimensionRegistry,
      ChunkGenerator generator,
      WoverChunkGeneratorImpl.StemGetter getter,
      WoverChunkGeneratorImpl.RegisterHelper registerHelper
   ) {
      Registry<DimensionType> dimensionTypeRegistry = registryAccess.lookupOrThrow(Registries.DIMENSION_TYPE);
      LevelStem levelStem = getter.get(dimensionKey);
      Holder<DimensionType> dimensionType = (Holder<DimensionType>)(levelStem == null ? dimensionTypeRegistry.getOrThrow(dimensionTypeKey) : levelStem.type());
      MappedRegistry<LevelStem> writableRegistry = new MappedRegistry(Registries.LEVEL_STEM, Lifecycle.experimental());
      writableRegistry.register(dimensionKey, new LevelStem(dimensionType, generator), RegistrationInfo.BUILT_IN);

      for (Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensionRegistry) {
         ResourceKey<LevelStem> resourceKey = entry.getKey();
         if (!dimensionKey.identifier().equals(resourceKey.identifier())) {
            registerHelper.register(writableRegistry, resourceKey, entry.getValue());
         }
      }

      return writableRegistry;
   }

   public interface RegisterHelper {
      Reference<LevelStem> register(MappedRegistry<LevelStem> var1, ResourceKey<LevelStem> var2, LevelStem var3);
   }

   public interface StemGetter {
      LevelStem get(ResourceKey<LevelStem> var1);
   }
}

