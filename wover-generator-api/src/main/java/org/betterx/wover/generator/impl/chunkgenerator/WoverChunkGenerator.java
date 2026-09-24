package org.betterx.wover.generator.impl.chunkgenerator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.biome.impl.modification.ChunkGeneratorHelper;
import org.betterx.wover.biome.mixin.ChunkGeneratorAccessor;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithNoiseRelatedSettings;
import org.betterx.wover.common.generator.api.biomesource.MergeableBiomeSource;
import org.betterx.wover.common.generator.api.biomesource.NoiseGeneratorSettingsProvider;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.common.generator.api.chunkgenerator.EnforceableChunkGenerator;
import org.betterx.wover.common.generator.api.chunkgenerator.RebuildableFeaturesPerStep;
import org.betterx.wover.common.generator.api.chunkgenerator.RestorableBiomeSource;
import org.betterx.wover.common.surface.api.InjectableSurfaceRules;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.compat.BlueprintBiomeSourceCompat;
import org.betterx.wover.common.generator.impl.compat.LithostitchedBiomeSourceCompat;
import org.betterx.wover.surface.impl.SurfaceRuleUtil;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.material.NetherMaterialRules;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions.HolderHolder;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;

public class WoverChunkGenerator
   extends NoiseBasedChunkGenerator
   implements RestorableBiomeSource<WoverChunkGenerator>,
   InjectableSurfaceRules<WoverChunkGenerator>,
   EnforceableChunkGenerator<WoverChunkGenerator>,
   RebuildableFeaturesPerStep<WoverChunkGenerator> {
   public static final Identifier ID = LibWoverWorldGenerator.C.id("betterx");
   protected static final NoiseSettings NETHER_NOISE_SETTINGS_AMPLIFIED = NoiseSettings.create(0, 256);
   public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED_NETHER = ResourceKey.create(
      Registries.NOISE_SETTINGS, LibWoverWorldGenerator.C.id("amplified_nether")
   );
   public static final MapCodec<WoverChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
      builderInstance -> {
         RecordCodecBuilder<WoverChunkGenerator, BiomeSource> biomeSourceCodec = BiomeSource.CODEC
            .fieldOf("biome_source")
            .forGetter(generator -> generator.biomeSource);
         RecordCodecBuilder<WoverChunkGenerator, Holder<NoiseGeneratorSettings>> settingsCodec = NoiseGeneratorSettings.CODEC
            .fieldOf("settings")
            .forGetter(generator -> generator.generatorSettings());
         return builderInstance.group(biomeSourceCodec, settingsCodec).apply(builderInstance, builderInstance.stable(WoverChunkGenerator::new));
      }
   );
   public final BiomeSource initialBiomeSource;
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = ResourceKey.create(
      Registries.DENSITY_FUNCTION, Identifier.withDefaultNamespace("nether/base_3d_noise")
   );

   public WoverChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> holder) {
      super(biomeSource, holder);
      this.initialBiomeSource = biomeSource;
      if (biomeSource instanceof BiomeSourceWithNoiseRelatedSettings bcl && holder.isBound()) {
         bcl.onLoadGeneratorSettings((NoiseGeneratorSettings)holder.value());
      }

      if (IntegrationCore.RUNS_TERRABLENDER) {
         LibWoverWorldGenerator.C.log.info("Make sure features are loaded from terrablender:" + biomeSource.getClass().getName());
         ChunkGeneratorHelper.rebuildFeaturesPerStep(this, biomeSource);
      }

      LibWoverWorldGenerator.C.log.info("Created WoverChunkGenerator with " + biomeSource.getClass().getName());
   }

   @NotNull
   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public void wover_rebuildFeaturesPerStep() {
      ChunkGeneratorHelper.rebuildFeaturesPerStep(this, this.getBiomeSource());
   }

   public void restoreInitialBiomeSource(ResourceKey<LevelStem> dimensionKey) {
      if (this.initialBiomeSource != this.getBiomeSource() && this instanceof ChunkGeneratorAccessor acc) {
         if (this.initialBiomeSource instanceof MergeableBiomeSource<?> bs) {
            acc.wover_setBiomeSource(bs.mergeWithBiomeSource(this.getBiomeSource()));
         } else if (this.initialBiomeSource instanceof ReloadableBiomeSource bs) {
            bs.reloadBiomes();
         }

         ChunkGeneratorHelper.rebuildFeaturesPerStep(this, this.getBiomeSource());
      }
   }

   public String toString() {
      return ChunkGeneratorManagerImpl.printGeneratorInfo("WoVer - Chunk Generator", this);
   }

   public void appendFeaturesPerStep() {
   }

   public Registry<LevelStem> enforceGeneratorInWorldGenSettings(
      RegistryAccess access,
      ResourceKey<LevelStem> dimensionKey,
      ResourceKey<DimensionType> dimensionTypeKey,
      ChunkGenerator loadedChunkGenerator,
      Registry<LevelStem> dimensionRegistry
   ) {
      LibWoverWorldGenerator.C.log.info("Enforcing Correct Generator for " + dimensionKey.identifier().toString() + ".");
      ChunkGenerator referenceGenerator = this;
      if (loadedChunkGenerator instanceof ChunkGeneratorAccessor generator
         && loadedChunkGenerator instanceof NoiseGeneratorSettingsProvider noiseProvider
         && this instanceof NoiseGeneratorSettingsProvider referenceProvider) {
         BiomeSource bs;
         if (this.getBiomeSource() instanceof MergeableBiomeSource<?> mbs) {
            bs = mbs.mergeWithBiomeSource(loadedChunkGenerator.getBiomeSource());
         } else {
            bs = this.getBiomeSource();
         }

         referenceProvider.wover_getNoiseGeneratorSettingHolders();
         referenceGenerator = new WoverChunkGenerator(bs, noiseProvider.wover_getNoiseGeneratorSettingHolders());
      }

      return WoverChunkGeneratorImpl.replaceGenerator(
         dimensionKey,
         dimensionTypeKey,
         access,
         dimensionRegistry.entrySet(),
         referenceGenerator,
         key -> dimensionRegistry.get(key).<LevelStem>map(Reference::value).orElse(null),
         (registry, key, stem) -> registry.register(key, stem, dimensionRegistry.registrationInfo(key).orElse(RegistrationInfo.BUILT_IN))
      );
   }

   public static NoiseGeneratorSettings amplifiedNether(BootstrapContext<NoiseGeneratorSettings> bootstapContext) {
      HolderGetter<DensityFunction> densityGetter = bootstapContext.lookup(Registries.DENSITY_FUNCTION);
      NoiseRouter router = netherNoNewCaves(densityGetter, bootstapContext.lookup(Registries.NOISE), slideNetherLike(densityGetter, 0, 256));
      return new NoiseGeneratorSettings(
         NETHER_NOISE_SETTINGS_AMPLIFIED,
         Blocks.NETHERRACK.defaultBlockState(),
         Blocks.LAVA.defaultBlockState(),
         router,
         bootstapContext.lookup(Registries.MATERIAL_RULE).getOrThrow(NetherMaterialRules.NETHER),
         List.of(),
         32,
         false,
         Optional.empty(),
         true,
         new NoiseGeneratorSettings.DebugFunctions(List.of(new NoiseGeneratorSettings.DebugFunctionEntry("N", router.finalDensity())))
      );
   }

   public void wover_injectSurfaceRules(Object dimensionRegistry, String dimensionKey) {
      if (dimensionKey != null) {
         ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, Identifier.parse(dimensionKey));
         if (LevelStem.END.equals(key)) {
            this.wover_removeBlueprintEndWrapper();
         }
         SurfaceRuleUtil.injectNoiseBasedSurfaceRules(
            key,
            this.generatorSettings(),
            this.getBiomeSource()
         );
      }
   }

   boolean wover_removeBlueprintEndWrapper() {
      BiomeSource currentSource = this.getBiomeSource();
      BiomeSource unwrappedSource = LithostitchedBiomeSourceCompat.unwrap(currentSource);
      if (!BlueprintBiomeSourceCompat.canReplaceEndWrapper()
         || currentSource == unwrappedSource
         || !(unwrappedSource instanceof WoverEndBiomeSource)
         || !(this instanceof ChunkGeneratorAccessor accessor)) {
         return false;
      }

      accessor.wover_setBiomeSource(unwrappedSource);
      if (unwrappedSource instanceof ReloadableBiomeSource reloadable) {
         reloadable.reloadBiomes();
      }
      ChunkGeneratorHelper.rebuildFeaturesPerStep(this, unwrappedSource);
      LibWoverWorldGenerator.C.log.info("Removed Blueprint End biome-source wrapper after importing its active overlays.");
      return true;
   }

   private static DensityFunction slideNetherLike(HolderGetter<DensityFunction> functions, int minY, int height) {
      DensityFunction caves = new HolderHolder(functions.getOrThrow(BASE_3D_NOISE_NETHER));
      DensityFunction topFactor = DensityFunctions.yClampedGradient(minY + height - 24, minY + height, 1.0F, 0.0F);
      DensityFunction noiseValue = DensityFunctions.lerp(topFactor, 0.9375F, caves);
      DensityFunction bottomFactor = DensityFunctions.yClampedGradient(minY - 8, minY + 24, 0.0F, 1.0F);
      return DensityFunctions.lerp(bottomFactor, 2.5F, noiseValue);
   }

   private static NoiseRouter netherNoNewCaves(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise> noises, DensityFunction slide) {
      DensityFunction temperature = DensityFunctions.shiftedNoise2d(
         DensityFunctions.zero(), DensityFunctions.zero(), 0.25, noises.getOrThrow(Noises.TEMPERATURE_NETHER)
      );
      DensityFunction vegetation = DensityFunctions.shiftedNoise2d(
         DensityFunctions.zero(), DensityFunctions.zero(), 0.25, noises.getOrThrow(Noises.VEGETATION_NETHER)
      );
      DensityFunction fullNoise = DensityFunctions.mul(DensityFunctions.interpolated(DensityFunctions.blendDensity(slide), 4, 8), DensityFunctions.constant(0.64F))
         .squeeze();
      return new NoiseRouter(
         temperature,
         vegetation,
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         fullNoise
      );
   }
}

