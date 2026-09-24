package org.betterx.wover.generator.impl.biomesource.nether;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.generator.impl.map.MapStack;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate.Sampler;
import org.jetbrains.annotations.NotNull;

public class WoverNetherBiomeSource extends WoverBiomeSource implements BiomeSourceWithConfig<WoverNetherBiomeSource, WoverNetherConfig> {
   public static final MapCodec<WoverNetherBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.LONG.fieldOf("seed").stable().forGetter(source -> source.currentSeed),
            WoverNetherConfig.CODEC.fieldOf("config").orElse(WoverNetherConfig.DEFAULT).forGetter(o -> o.config)
         )
         .apply(instance, instance.stable(WoverNetherBiomeSource::new))
   );
   public static final List<TagKey<Biome>> TAGS = List.of(BiomeTags.IS_NETHER);
   private BiomeMap biomeMap;
   private WoverBiomePicker biomePicker;
   private WoverNetherConfig config;

   public WoverNetherBiomeSource(WoverNetherConfig config) {
      this(0L, config, false);
   }

   private WoverNetherBiomeSource(long seed, WoverNetherConfig config) {
      this(seed, config, true);
   }

   private WoverNetherBiomeSource(long seed, WoverNetherConfig config, boolean initMaps) {
      super(seed);
      this.config = config;
      this.rebuildBiomes(false);
      if (initMaps) {
         this.initMap(seed);
      }
   }

   @Override
   protected List<WoverBiomeSource.TagToPicker> createFreshPickerMap() {
      this.biomePicker = new WoverBiomePicker(this.fallbackBiome());
      return List.of(new WoverBiomeSource.TagToPicker(BiomeTags.IS_NETHER, this.biomePicker));
   }

   @Override
   protected TagKey<Biome> defaultBiomeTag() {
      return BiomeTags.IS_NETHER;
   }

   @Override
   protected List<TagKey<Biome>> acceptedTags() {
      return TAGS;
   }

   @Override
   protected TagKey<Biome> tagForUnknownBiome(Holder<Biome> biomeHolder, ResourceKey<Biome> biomeKey) {
      return this.defaultBiomeTag();
   }

   @Override
   protected ResourceKey<Biome> fallbackBiome() {
      return Biomes.NETHER_WASTES;
   }

   @Override
   public String toShortString() {
      return "WoVer - Nether BiomeSource (" + Integer.toHexString(this.hashCode()) + ")";
   }

   public String toString() {
      return this.toShortString()
         + "\n    biomes     = "
         + this.possibleBiomes().size()
         + "\n    namespaces = "
         + this.getNamespaces()
         + "\n    seed       = "
         + this.currentSeed
         + "\n    height     = "
         + this.maxHeight
         + "\n    config     = "
         + this.config;
   }

   @Override
   protected void onInitMap(long newSeed) {
      MapBuilderFunction mapConstructor = this.config.mapVersion.mapBuilder;
      if (this.maxHeight > this.config.biomeSizeVertical * 1.5 && this.config.useVerticalBiomes) {
         this.biomeMap = new MapStack(newSeed, this.config.biomeSize, this.biomePicker, this.config.biomeSizeVertical, this.maxHeight, mapConstructor);
      } else {
         this.biomeMap = mapConstructor.create(newSeed, this.config.biomeSize, this.biomePicker);
      }
   }

   @Override
   protected void onHeightChange(int newHeight) {
      this.initMap(this.currentSeed);
   }

   @NotNull
   protected MapCodec<? extends BiomeSource> codec() {
      return CODEC;
   }

   @NotNull
   public BiomeResolver createResolver(Sampler sampler) {
      return (biomeX, biomeY, biomeZ) -> this.getNoiseBiome(biomeX, biomeY, biomeZ);
   }

   @NotNull
   private Holder<Biome> getNoiseBiome(int biomeX, int biomeY, int biomeZ) {
      if (!this.wasBound()) {
         this.reloadBiomes(false);
      }

      if (this.biomeMap == null) {
         return (Holder<Biome>)this.possibleBiomes().stream().findFirst().get();
      } else {
         if ((biomeX & 63) == 0 && (biomeZ & 63) == 0) {
            this.biomeMap.clearCache();
         }

         WoverBiomePicker.PickableBiome bb = this.biomeMap.getBiome(biomeX << 2, biomeY << 2, biomeZ << 2);
         return bb.biome;
      }
   }

   public WoverNetherConfig getBiomeSourceConfig() {
      return this.config;
   }

   public void setBiomeSourceConfig(WoverNetherConfig newConfig) {
      this.config = newConfig;
      this.initMap(this.currentSeed);
   }
}

