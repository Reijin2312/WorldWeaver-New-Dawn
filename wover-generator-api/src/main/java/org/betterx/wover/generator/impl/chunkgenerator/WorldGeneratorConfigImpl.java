package org.betterx.wover.generator.impl.chunkgenerator;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.entrypoint.LibWoverEvents;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.preset.WorldPresets;
import org.betterx.wover.generator.impl.preset.PresetRegistryImpl;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.preset.api.WorldPresetInfo;
import org.betterx.wover.preset.api.WorldPresetInfoRegistry;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.state.api.WorldConfig;
import org.betterx.wover.state.api.WorldState;
import de.ambertation.wunderlib.utils.Version;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldGeneratorConfigImpl {
   public static final String TAG_PRESET = "preset";
   private static final String LEGACY_TAG_GENERATOR = "generator";
   public static final String TAG_DIMENSIONS = "dimensions";
   public static final String TAG_DIMENSION_PRESETS = "world_presets";
   private static final String LEGACY_TAG_VERSION = "version";
   private static final String LEGACY_TAG_BN_GEN_VERSION = "generator_version";
   private static DimensionsWrapper DEFAULT_DIMENSIONS_WRAPPER = null;
   private static final String LEGACY_NAMESPACE = LegacyHelper.BCLIB_CORE.namespace + ":";
   private static final String WOVER_NAMESPACE = LibWoverWorldGenerator.C.namespace + ":";

   @NotNull
   static CompoundTag getPresetsNbt() {
      return WorldConfig.getCompoundTag(LibWoverWorldGenerator.C, "preset");
   }

   @NotNull
   static CompoundTag getPresetsNbtFromFolder(LevelStorageAccess levelStorageAccess) {
      File dataDir = levelStorageAccess.getLevelPath(LevelResource.ROOT).resolve("data").toFile();
      File nbtFile = new File(dataDir, LibWoverWorldGenerator.C.modId + ".nbt");
      CompoundTag root = null;
      if (nbtFile.exists()) {
         try {
            root = NbtIo.readCompressed(nbtFile.toPath(), NbtAccounter.create(2097152L));
         } catch (IOException var5) {
            LibWoverEvents.C.log.error("NBT loading failed", var5);
         }
      }

      return root != null ? root.getCompound("preset").orElse(new CompoundTag()) : new CompoundTag();
   }

   @NotNull
   private static CompoundTag getLegacyPresetsNbt() {
      return WorldConfig.getCompoundTag(LegacyHelper.WORLDS_TOGETHER_CORE, "preset");
   }

   private static CompoundTag getLegacyGeneratorNbt() {
      CompoundTag root = WorldConfig.getRootTag(LegacyHelper.WORLDS_TOGETHER_CORE);
      return root.contains("generator") ? WorldConfig.getCompoundTag(LegacyHelper.WORLDS_TOGETHER_CORE, "generator") : null;
   }

   public static void writeWorldPresetSettingsDirect(Map<ResourceKey<LevelStem>, ChunkGenerator> settings) {
      writeWorldPresetSettingsDirect(null, settings);
   }

   public static void writeWorldPresetSettingsDirect(@Nullable Provider access, Map<ResourceKey<LevelStem>, ChunkGenerator> settings) {
      DimensionsWrapper wrapper = new DimensionsWrapper(settings);
      writeWorldPresetSettings(access, wrapper);
   }

   private static void writeWorldPresetSettings(@Nullable Provider access, DimensionsWrapper wrapper) {
      RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, (Provider)(access != null ? access : WorldState.allStageRegistryAccess()));
      DataResult<Tag> encodeResult = DimensionsWrapper.CODEC.encodeStart(registryOps, wrapper);
      if (encodeResult.result().isPresent()) {
         CompoundTag settingsNbt = WorldConfig.getRootTag(LibWoverWorldGenerator.C);
         settingsNbt.put("preset", (Tag)encodeResult.result().get());
      } else {
         LibWoverWorldGenerator.C
            .log
            .error("Unable to encode world generator settings for level.dat: " + encodeResult.error().map(e -> e.message()).orElse("unknown error"));
      }

      WorldConfig.saveFile(LibWoverWorldGenerator.C);
   }

   private static void migrateLegacyDimensionIDs(CompoundTag dimensions) {
      for (String dimensionKey : dimensions.keySet()) {
         CompoundTag stem = (CompoundTag)dimensions.getCompound(dimensionKey).orElse(null);
         if (stem != null) {
            migrateLegacyID(stem, "type");
            migrateLegacyID(stem, "settings");
            stem.getCompound("biome_source").ifPresent(source -> migrateLegacyID(source, "type"));
         }
      }
   }

   private static void migrateLegacyID(CompoundTag tag, String key) {
      String id = (String)tag.getString(key).orElse(null);
      if (id != null && id.startsWith(LEGACY_NAMESPACE)) {
         String migrated = WOVER_NAMESPACE + id.substring(LEGACY_NAMESPACE.length());
         LibWoverWorldGenerator.C.log.info("Migrating legacy generator ID '" + id + "' to '" + migrated + "'.");
         tag.putString(key, migrated);
      }
   }

   public static void migrateGeneratorSettings() {
      CompoundTag settingsNbt = getPresetsNbt();
      if (settingsNbt.isEmpty()) {
         CompoundTag wtGen = getLegacyPresetsNbt();
         if (wtGen != null && wtGen.contains("dimensions")) {
            LibWoverWorldGenerator.C.log.info("Found World with WorldsTogether Settings.");
            CompoundTag newPresets = getPresetsNbt();
            CompoundTag dimensions = (CompoundTag)wtGen.getCompound("dimensions").orElse(null);
            if (dimensions != null) {
               migrateLegacyDimensionIDs(dimensions);
            }

            newPresets.put("dimensions", wtGen.get("dimensions"));
            WorldConfig.saveFile(LibWoverWorldGenerator.C);
            return;
         }

         CompoundTag oldGen = getLegacyGeneratorNbt();
         if (oldGen != null && oldGen.contains("type")) {
            LibWoverWorldGenerator.C.log.info("Found World with beta generator Settings.");
            if ("bclib:bcl_world_preset_settings".equals(oldGen.getString("type"))) {
               int netherVersion = oldGen.getInt("minecraft:the_nether").orElse(18);
               int endVersion = oldGen.getInt("minecraft:the_end").orElse(18);
               byte var10;
               if (netherVersion == 18) {
                  var10 = 0;
               } else if (netherVersion == 17) {
                  var10 = 1;
               } else {
                  var10 = 2;
               }

               byte var13;
               if (endVersion == 18) {
                  var13 = 0;
               } else if (endVersion == 17) {
                  var13 = 1;
               } else {
                  var13 = 2;
               }

               List<Map<ResourceKey<LevelStem>, ChunkGenerator>> presets = List.of(
                  DimensionsWrapper.getDimensionsMap(WorldPresets.WOVER_WORLD),
                  DimensionsWrapper.getDimensionsMap(PresetRegistryImpl.BCL_WORLD_17),
                  DimensionsWrapper.getDimensionsMap(net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL)
               );
               Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions = new HashMap<>();
               dimensions.put(LevelStem.OVERWORLD, presets.get(0).get(LevelStem.OVERWORLD));
               dimensions.put(LevelStem.NETHER, presets.get(var10).get(LevelStem.NETHER));
               dimensions.put(LevelStem.END, presets.get(var13).get(LevelStem.END));
               writeWorldPresetSettingsDirect(dimensions);
            }

            return;
         }

         LibWoverWorldGenerator.C.log.info("Found World without generator Settings. Setting up data...");
         ResourceKey<WorldPreset> biomeSourceVersion = WorldPresets.WOVER_WORLD;
         CompoundTag bclRoot = WorldConfig.getRootTag(LegacyHelper.BCLIB_CORE);
         Version bclVersion = new Version(bclRoot.getString("version").orElse("0.0.0"));
         boolean isPre18 = !bclVersion.isLargerOrEqualVersion("1.0.0");
         if (isPre18) {
            LibWoverWorldGenerator.C.log.info("World was create pre 1.18!");
            biomeSourceVersion = PresetRegistryImpl.BCL_WORLD_17;
         }

         if (WorldConfig.hasMod(IntegrationCore.BETTER_NETHER)) {
            LibWoverWorldGenerator.C.log.info("Found Data from BetterNether, using for migration.");
            CompoundTag bnRoot = WorldConfig.getRootTag(IntegrationCore.BETTER_NETHER);
            biomeSourceVersion = "1.17".equals(bnRoot.getString("generator_version")) ? PresetRegistryImpl.BCL_WORLD_17 : WorldPresets.WOVER_WORLD;
         }

         WorldDimensions dimensions = DimensionsWrapper.getDimensions(biomeSourceVersion);
         if (dimensions != null) {
            LibWoverWorldGenerator.C.log.info("Set world to BiomeSource Version " + biomeSourceVersion);
            writeWorldPresetSettings(null, new DimensionsWrapper(dimensions));
         } else {
            LibWoverWorldGenerator.C.log.error("Failed to set world to BiomeSource Version " + biomeSourceVersion);
         }
      }
   }

   public static void createWorldConfig(@Nullable Provider access, Holder<WorldPreset> currentPreset, WorldDimensions dimensions) {
      if (currentPreset != null && currentPreset.unwrapKey().isPresent()) {
         WorldPresetInfo info = WorldPresetInfoRegistry.getFor(currentPreset);
         ResourceKey<WorldPreset> presetKey = (ResourceKey<WorldPreset>)currentPreset.unwrapKey().orElseThrow();

         for (Entry<ResourceKey<LevelStem>, LevelStem> dimEntry : dimensions.dimensions().entrySet()) {
            if (dimEntry.getValue().generator() instanceof ConfiguredChunkGenerator cfg) {
               ResourceKey<WorldPreset> secondaryPreset = info.getPresetOverrideRecursive(dimEntry.getKey());
               if (cfg.wover_getConfiguredWorldPreset() == null) {
                  cfg.wover_setConfiguredWorldPreset(secondaryPreset != null ? secondaryPreset : presetKey);
               }
            }
         }
      }

      LibWoverWorldGenerator.C.log.verbose("Creating presets file for new world");
      writeWorldPresetSettingsDirect(access, DimensionsWrapper.build(dimensions));
   }

   @NotNull
   public static Map<ResourceKey<LevelStem>, ChunkGenerator> loadWorldDimensions(RegistryAccess registryAccess, CompoundTag presetNBT) {
      try {
         RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
         if (DEFAULT_DIMENSIONS_WRAPPER == null) {
            DEFAULT_DIMENSIONS_WRAPPER = new DimensionsWrapper(DimensionsWrapper.getDimensionsMap(registryAccess, WorldPresetManager.getDefault()));
         }

         if (presetNBT != null && presetNBT.contains("dimensions")) {
            Optional<DimensionsWrapper> oLevelStem = DimensionsWrapper.CODEC
               .parse(new Dynamic(registryOps, presetNBT))
               .resultOrPartial(error -> LibWoverWorldGenerator.C.log.error(String.valueOf(error)));
            return oLevelStem.orElse(DEFAULT_DIMENSIONS_WRAPPER).dimensions;
         } else {
            return DEFAULT_DIMENSIONS_WRAPPER.dimensions;
         }
      } catch (Exception var4) {
         LibWoverWorldGenerator.C.log.error("Failed to load Dimensions", var4);
         return DEFAULT_DIMENSIONS_WRAPPER.dimensions;
      }
   }
}

