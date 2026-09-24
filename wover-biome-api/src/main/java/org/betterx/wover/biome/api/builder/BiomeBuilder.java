package org.betterx.wover.biome.api.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.impl.builder.BiomeSurfaceRuleBuilderImpl;
import org.betterx.wover.feature.api.placed.BasePlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;
import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;
import de.ambertation.wunderlib.ui.ColorHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.AmbientMoodSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.biome.BiomeGenerationSettings.Builder;
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeBuilder<B extends BiomeBuilder<B>> {
   public final BiomeKey<B> key;
   public final BiomeBootstrapContext bootstrapContext;
   public static int DEFAULT_WATER_FOG_COLOR = 329011;
   public static int DEFAULT_WATER_COLOR = 4159204;
   public static int DEFAULT_NETHER_WATER_COLOR = DEFAULT_WATER_COLOR;
   public static int DEFAULT_END_WATER_COLOR = DEFAULT_WATER_COLOR;
   public static int DEFAULT_NETHER_WATER_FOG_COLOR = 329011;
   public static int DEFAULT_END_WATER_FOG_COLOR = DEFAULT_NETHER_WATER_FOG_COLOR;
   public static int DEFAULT_FOG_COLOR = 12638463;
   public static int DEFAULT_END_FOG_COLOR = 10518688;
   public static int DEFAULT_END_SKY_COLOR = 0;
   public static float DEFAULT_NETHER_TEMPERATURE = 2.0F;
   public static float DEFAULT_END_TEMPERATURE = 0.5F;
   public static float DEFAULT_NETHER_WETNESS = 0.0F;
   public static float DEFAULT_END_WETNESS = 0.5F;
   protected final List<ParameterPoint> parameters = new ArrayList<>(1);
   @Nullable
   protected TagKey<Biome> intendedPlacement = null;
   protected float fogDensity;
   protected final List<TagKey<Biome>> biomeTags = new ArrayList<>(2);
   @Nullable
   private BiomeSurfaceRuleBuilderImpl<B> surfaceBuilder;

   public static int calculateSkyColor(float temperature) {
      return OverworldBiomes.calculateSkyColor(temperature);
   }

   protected BiomeBuilder(BiomeBootstrapContext context, BiomeKey<B> key) {
      this.key = key;
      this.bootstrapContext = context;
      this.fogDensity = 1.0F;
   }

   public B addClimate(ParameterPoint point) {
      this.parameters.add(point);
      return (B)this;
   }

   public B addNetherClimate(float temperature, float humidity, float offset) {
      return this.addClimate(Climate.parameters(temperature, humidity, 0.0F, 0.0F, 0.0F, 0.0F, offset));
   }

   public B addNetherClimate(float temperature, float humidity) {
      return this.addNetherClimate(temperature, humidity, 0.0F);
   }

   public B fogDensity(float density) {
      this.fogDensity = density;
      return (B)this;
   }

   public B structure(StructureKey<?, ?, ?> structure) {
      return this.tag(structure.biomeTag());
   }

   public B structure(TagKey<Biome> structureTag) {
      return this.tag(structureTag);
   }

   protected B biomeTypeTag(TagKey<Biome> tag) {
      if (this.intendedPlacement == null && tag != null) {
         this.intendedPlacement = tag;
      }

      return this.tag(tag);
   }

   @SafeVarargs
   public final B tag(TagKey<Biome>... tags) {
      for (TagKey<Biome> biomeTag : tags) {
         if (biomeTag != null && !this.biomeTags.contains(biomeTag)) {
            this.biomeTags.add(biomeTag);
         }
      }

      return (B)this;
   }

   public BiomeSurfaceRuleBuilder<B> startSurface() {
      this.surfaceBuilder = new BiomeSurfaceRuleBuilderImpl<>(this.key, (B)this);
      return this.surfaceBuilder;
   }

   public B surface(BlockState state) {
      return (B)((BiomeSurfaceRuleBuilder)this.startSurface().surface(state)).finishSurface();
   }

   public B surface(Block block) {
      return (B)((BiomeSurfaceRuleBuilder)this.startSurface().surface(block)).finishSurface();
   }

   public B surface(BlockState top, BlockState under) {
      return (B)((BiomeSurfaceRuleBuilder)((BiomeSurfaceRuleBuilder)this.startSurface().surface(top)).subsurface(under, 3)).finishSurface();
   }

   public B surface(Block top, Block under) {
      return (B)((BiomeSurfaceRuleBuilder)((BiomeSurfaceRuleBuilder)this.startSurface().surface(top)).subsurface(under, 3)).finishSurface();
   }

   public B intendedPlacement(TagKey<Biome> biome) {
      this.intendedPlacement = biome;
      return (B)this;
   }

   public void register() {
      this.bootstrapContext.register(this);
   }

   public abstract void registerBiome(BootstrapContext<Biome> var1);

   public abstract void registerBiomeData(BootstrapContext<BiomeData> var1);

   public void registerBiomeTags(TagBootstrapContext<Biome> context) {
      for (TagKey<Biome> biomeTag : this.biomeTags) {
         context.add(biomeTag, new ResourceKey[]{this.key.key});
      }
   }

   public void registerSurfaceRule(@NotNull BootstrapContext<AssignedSurfaceRule> context) {
      if (this.surfaceBuilder != null) {
         this.surfaceBuilder.register(context);
      }
   }

   public abstract static class Vanilla extends BiomeBuilder.VanillaBuilder<BiomeBuilder.Vanilla> {
      protected Vanilla(BiomeBootstrapContext context, BiomeKey<BiomeBuilder.Vanilla> key) {
         super(context, key);
      }
   }

   public abstract static class VanillaBuilder<B extends BiomeBuilder.VanillaBuilder<B>> extends BiomeBuilder<B> {
      private TemperatureModifier temperatureModifier;
      private float downfall;
      private float temperature;
      private boolean hasPrecipitation;
      private final net.minecraft.world.level.biome.BiomeSpecialEffects.Builder fx = new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder();
      private final Builder generationSettings;
      private final net.minecraft.world.level.biome.MobSpawnSettings.Builder mobSpawnSettings = new net.minecraft.world.level.biome.MobSpawnSettings.Builder();
      private int fogColor;
      private int waterFogColor;
      private int skyColor;
      @Nullable
      private AmbientParticle ambientParticle;
      @Nullable
      private Holder<SoundEvent> ambientLoop;
      @Nullable
      private AmbientMoodSettings ambientMood;
      @Nullable
      private AmbientAdditionsSettings ambientAdditions;
      @Nullable
      private Music backgroundMusic;
      @Nullable
      private Float creatureGenerationProbability;

      protected VanillaBuilder(BiomeBootstrapContext context, BiomeKey<B> key) {
         super(context, key);
         this.temperatureModifier = TemperatureModifier.NONE;
         this.downfall = 0.0F;
         this.temperature = 0.5F;
         this.hasPrecipitation = false;
         this.generationSettings = new Builder(this.bootstrapContext.lookup(Registries.PLACED_FEATURE), this.bootstrapContext.lookup(Registries.CARVER));
         this.fogColor = DEFAULT_FOG_COLOR;
         this.waterFogColor = DEFAULT_WATER_FOG_COLOR;
         this.fx.waterColor(DEFAULT_WATER_COLOR);
         this.skyColor = calculateSkyColor(this.temperature);
      }

      public B hasPrecipitation(boolean bl) {
         this.hasPrecipitation = bl;
         return (B)this;
      }

      public B temperature(float f) {
         this.temperature = f;
         return (B)this;
      }

      public B downfall(float f) {
         this.downfall = f;
         return (B)this;
      }

      public B temperatureAdjustment(TemperatureModifier temperatureModifier) {
         this.temperatureModifier = temperatureModifier;
         return (B)this;
      }

      public B temperatureFrozen() {
         return (B)this.temperatureAdjustment(TemperatureModifier.FROZEN);
      }

      public B temperatureRegular() {
         return (B)this.temperatureAdjustment(TemperatureModifier.NONE);
      }

      public B feature(BasePlacedFeatureKey<?> feature) {
         this.generationSettings.addFeature(feature.getDecoration(), feature.getHolder(this.bootstrapContext.lookup(Registries.PLACED_FEATURE)));
         return (B)this;
      }

      public B feature(Decoration decoration, ResourceKey<PlacedFeature> feature) {
         this.generationSettings.addFeature(decoration, PlacedFeatureManager.getHolder(this.bootstrapContext.lookup(Registries.PLACED_FEATURE), feature));
         return (B)this;
      }

      public B feature(Decoration decoration, Holder<PlacedFeature> feature) {
         this.generationSettings.addFeature(decoration, feature);
         return (B)this;
      }

      public B feature(Consumer<Builder> featureAdd) {
         featureAdd.accept(this.generationSettings);
         return (B)this;
      }

      public B defaultMushrooms() {
         return (B)this.feature(BiomeDefaultFeatures::addDefaultMushrooms);
      }

      public B netherDefaultOres() {
         return (B)this.feature(BiomeDefaultFeatures::addNetherDefaultOres);
      }

      public B carver(ResourceKey<WorldCarver> carver) {
         this.generationSettings.addCarver(this.bootstrapContext.lookup(Registries.CARVER).getOrThrow(carver));
         return (B)this;
      }

      public B carver(Holder<WorldCarver> carver) {
         this.generationSettings.addCarver(carver);
         return (B)this;
      }

      public B fogColor(int color) {
         this.fogColor = color;
         return (B)this;
      }

      public B fogColor(int r, int g, int b) {
         this.fogColor = ColorHelper.color(r, g, b);
         return (B)this;
      }

      public B waterColor(int r, int g, int b) {
         return (B)this.waterColor(ColorHelper.color(r, g, b));
      }

      public B waterColor(int color) {
         this.fx.waterColor(color);
         return (B)this;
      }

      public B waterFogColor(int r, int g, int b) {
         return (B)this.waterFogColor(ColorHelper.color(r, g, b));
      }

      public B waterFogColor(int color) {
         this.waterFogColor = color;
         return (B)this;
      }

      public B skyColor(int r, int g, int b) {
         return (B)this.skyColor(ColorHelper.color(r, g, b));
      }

      public B skyColor(int color) {
         this.skyColor = color;
         return (B)this;
      }

      public B foliageColorOverride(int r, int g, int b) {
         return (B)this.foliageColorOverride(ColorHelper.color(r, g, b));
      }

      public B foliageColorOverride(int color) {
         this.fx.foliageColorOverride(color);
         return (B)this;
      }

      public B grassColorOverride(int r, int g, int b) {
         return (B)this.grassColorOverride(ColorHelper.color(r, g, b));
      }

      public B grassColorOverride(int color) {
         this.fx.grassColorOverride(color);
         return (B)this;
      }

      public B grassColorModifier(GrassColorModifier grassColorModifier) {
         this.fx.grassColorModifier(grassColorModifier);
         return (B)this;
      }

      public B waterAndFogColor(int color) {
         return (B)this.waterColor(color).waterFogColor(color);
      }

      public B waterAndFogColor(int r, int g, int b) {
         return (B)this.waterAndFogColor(ColorHelper.color(r, g, b));
      }

      public B plantsColor(int r, int g, int b) {
         return (B)this.plantsColor(ColorHelper.color(r, g, b));
      }

      public B plantsColor(int color) {
         return (B)this.grassColorOverride(color).foliageColorOverride(color);
      }

      public B particles(ParticleOptions particle, float probability) {
         this.particles(new AmbientParticle(particle, probability));
         return (B)this;
      }

      public B particles(AmbientParticle ambientParticle) {
         this.ambientParticle = ambientParticle;
         return (B)this;
      }

      public B loop(Holder<SoundEvent> holder) {
         this.ambientLoop = holder;
         return (B)this;
      }

      public B mood(AmbientMoodSettings ambientMoodSettings) {
         this.ambientMood = ambientMoodSettings;
         return (B)this;
      }

      public B mood(Holder<SoundEvent> mood) {
         return (B)this.mood(mood, 6000, 8, 2.0F);
      }

      public B mood(Holder<SoundEvent> mood, int tickDelay, int blockSearchExtent, float soundPositionOffset) {
         return (B)this.mood(new AmbientMoodSettings(mood, tickDelay, blockSearchExtent, soundPositionOffset));
      }

      public B additions(AmbientAdditionsSettings ambientAdditionsSettings) {
         this.ambientAdditions = ambientAdditionsSettings;
         return (B)this;
      }

      public B additions(Holder<SoundEvent> additions, float intensity) {
         return (B)this.additions(new AmbientAdditionsSettings(additions, intensity));
      }

      public B additions(Holder<SoundEvent> additions) {
         return (B)this.additions(additions, 0.0111F);
      }

      public B music(@Nullable Music music) {
         this.backgroundMusic = music;
         return (B)this;
      }

      public B music(Holder<SoundEvent> music) {
         return (B)this.music(Musics.createGameMusic(music));
      }

      public B music(Holder<SoundEvent> music, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
         return (B)this.music(new Music(music, minDelay, maxDelay, replaceCurrentMusic));
      }

      public final B isNetherBiome() {
         return (B)((BiomeBuilder.VanillaBuilder)this.biomeTypeTag(BiomeTags.IS_NETHER));
      }

      public final B isEndHighlandBiome() {
         return (B)((BiomeBuilder.VanillaBuilder)this.biomeTypeTag(CommonBiomeTags.IS_END_HIGHLAND));
      }

      public final B isEndMidlandBiome() {
         return (B)((BiomeBuilder.VanillaBuilder)this.biomeTypeTag(CommonBiomeTags.IS_END_MIDLAND));
      }

      public final B isEndCenterIslandBiome() {
         return (B)((BiomeBuilder.VanillaBuilder)this.biomeTypeTag(CommonBiomeTags.IS_END_CENTER));
      }

      public final B isEndBarrensBiome() {
         return (B)((BiomeBuilder.VanillaBuilder)this.biomeTypeTag(CommonBiomeTags.IS_END_BARRENS));
      }

      public final B isEndSmallIslandBiome() {
         return (B)((BiomeBuilder.VanillaBuilder)this.biomeTypeTag(CommonBiomeTags.IS_SMALL_END_ISLAND));
      }

      public B spawn(EntityType<?> entityType, int weight, int minGroupCount, int maxGroupCount) {
         this.mobSpawnSettings.addSpawn(entityType, weight, minGroupCount, maxGroupCount);
         return (B)this;
      }

      public B addMobCharge(EntityType<?> entityType, double energyBudget, double charge) {
         this.mobSpawnSettings.addMobSpawnCost(entityType, energyBudget, charge);
         return (B)this;
      }

      public B creatureGenerationProbability(float p) {
         this.creatureGenerationProbability = p;
         return (B)this;
      }

      @Override
      public void register() {
         this.bootstrapContext.register(this);
      }

      @Override
      public void registerBiome(BootstrapContext<Biome> biomeContext) {
         biomeContext.register(this.key.key, this.buildBiome());
      }

      @Override
      public abstract void registerBiomeData(BootstrapContext<BiomeData> var1);

      protected Biome buildBiome() {
         net.minecraft.world.level.biome.Biome.BiomeBuilder vanillaBuilder = new net.minecraft.world.level.biome.Biome.BiomeBuilder();
         vanillaBuilder.hasPrecipitation(this.hasPrecipitation);
         vanillaBuilder.downfall(this.downfall);
         vanillaBuilder.temperature(this.temperature);
         vanillaBuilder.temperatureAdjustment(this.temperatureModifier);
         vanillaBuilder.generationSettings(this.generationSettings.build());
         vanillaBuilder.specialEffects(this.fx.build());
         vanillaBuilder.mobSpawnSettings(this.mobSpawnSettings.build());
         if (this.creatureGenerationProbability != null) {
            vanillaBuilder.setAttribute(EnvironmentAttributes.CREATURE_WORLD_GEN_SPAWN_PROBABILITY, this.creatureGenerationProbability);
         }

         vanillaBuilder.setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(this.fogColor));
         vanillaBuilder.setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(this.waterFogColor));
         vanillaBuilder.setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(this.skyColor));
         if (this.ambientParticle != null) {
            vanillaBuilder.setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, List.of(this.ambientParticle));
         }

         if (this.ambientLoop != null || this.ambientMood != null || this.ambientAdditions != null) {
            vanillaBuilder.setAttribute(
               EnvironmentAttributes.AMBIENT_SOUNDS,
               new AmbientSounds(
                  Optional.ofNullable(this.ambientLoop),
                  Optional.ofNullable(this.ambientMood),
                  this.ambientAdditions == null ? List.of() : List.of(this.ambientAdditions)
               )
            );
         }

         if (this.backgroundMusic != null) {
            vanillaBuilder.setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(this.backgroundMusic));
         }

         return vanillaBuilder.build();
      }
   }

   public abstract static class Wrapped extends BiomeBuilder<BiomeBuilder.Wrapped> {
      protected Wrapped(BiomeBootstrapContext context, BiomeKey<BiomeBuilder.Wrapped> key) {
         super(context, key);
      }
   }
}
