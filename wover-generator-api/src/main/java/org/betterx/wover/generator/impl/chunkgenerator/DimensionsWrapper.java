package org.betterx.wover.generator.impl.chunkgenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.state.api.WorldState;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DimensionsWrapper {
   private static final Codec<Map<ResourceKey<LevelStem>, ChunkGenerator>> DIMENSIONS_CODEC =
      Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), ChunkGenerator.CODEC);
   private static final Codec<Map<ResourceKey<LevelStem>, ResourceKey<WorldPreset>>> WORLD_PRESETS_CODEC =
      Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), ResourceKey.codec(Registries.WORLD_PRESET));
   public static final Codec<DimensionsWrapper> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            DIMENSIONS_CODEC
               .optionalFieldOf("dimensions", new HashMap<>())
               .forGetter(o -> o.dimensions),
            WORLD_PRESETS_CODEC
               .optionalFieldOf("world_presets", new HashMap<>())
               .forGetter(DimensionsWrapper::getDimensionPresets)
         )
         .apply(instance, DimensionsWrapper::fromCodec)
   );
   final Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions;

   static Map<ResourceKey<LevelStem>, ChunkGenerator> build(WorldDimensions dimensions) {
      Map<ResourceKey<LevelStem>, ChunkGenerator> map = new HashMap<>();

      for (Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.dimensions().entrySet()) {
         ResourceKey<LevelStem> key = entry.getKey();
         LevelStem stem = entry.getValue();
         map.put(key, stem.generator());
      }

      return map;
   }

   @Nullable
   public static WorldDimensions getDimensions(ResourceKey<WorldPreset> key) {
      RegistryAccess access = WorldState.allStageRegistryAccess();
      if (access == null) {
         LibWoverWorldGenerator.C.log.error("No valid registry found!");
         return null;
      } else {
         Optional<Reference<WorldPreset>> preset = access.lookupOrThrow(Registries.WORLD_PRESET).get(key);
         return preset.<WorldDimensions>map(worldPresetReference -> ((WorldPreset)worldPresetReference.value()).createWorldDimensions()).orElse(null);
      }
   }

   @Nullable
   public static WorldDimensions getDimensions(RegistryAccess access, ResourceKey<WorldPreset> key) {
      if (access == null) {
         LibWoverWorldGenerator.C.log.error("No valid registry found!");
         return null;
      } else {
         Optional<Reference<WorldPreset>> preset = access.lookupOrThrow(Registries.WORLD_PRESET).get(key);
         return preset.<WorldDimensions>map(worldPresetReference -> ((WorldPreset)worldPresetReference.value()).createWorldDimensions()).orElse(null);
      }
   }

   @NotNull
   public static Map<ResourceKey<LevelStem>, ChunkGenerator> getDimensionsMap(RegistryAccess access, ResourceKey<WorldPreset> key) {
      WorldDimensions reg = getDimensions(access, key);
      return (Map<ResourceKey<LevelStem>, ChunkGenerator>)(reg == null ? new HashMap<>() : build(reg));
   }

   @NotNull
   public static Map<ResourceKey<LevelStem>, ChunkGenerator> getDimensionsMap(ResourceKey<WorldPreset> key) {
      WorldDimensions reg = getDimensions(key);
      return (Map<ResourceKey<LevelStem>, ChunkGenerator>)(reg == null ? new HashMap<>() : build(reg));
   }

   private static DimensionsWrapper fromCodec(
      Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions, Map<ResourceKey<LevelStem>, ResourceKey<WorldPreset>> presets
   ) {
      for (Entry<ResourceKey<LevelStem>, ChunkGenerator> dimEntry : dimensions.entrySet()) {
         ResourceKey<WorldPreset> preset = presets.get(dimEntry.getKey());
         if (preset != null && dimEntry.getValue() instanceof ConfiguredChunkGenerator cfg) {
            cfg.wover_setConfiguredWorldPreset(preset);
         }
      }

      return new DimensionsWrapper(dimensions);
   }

   DimensionsWrapper(WorldDimensions dimensions) {
      this(build(dimensions));
   }

   DimensionsWrapper(Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions) {
      this.dimensions = dimensions;
   }

   public Map<ResourceKey<LevelStem>, ResourceKey<WorldPreset>> getDimensionPresets() {
      Map<ResourceKey<LevelStem>, ResourceKey<WorldPreset>> map = new HashMap<>();

      for (Entry<ResourceKey<LevelStem>, ChunkGenerator> dimEntry : this.dimensions.entrySet()) {
         if (dimEntry.getValue() instanceof ConfiguredChunkGenerator cfg && cfg.wover_getConfiguredWorldPreset() != null) {
            map.put(dimEntry.getKey(), cfg.wover_getConfiguredWorldPreset());
         }
      }

      return map;
   }
}

