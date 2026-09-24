package org.betterx.wover.generator.impl.biomesource.end;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.generator.api.biomesource.end.BiomeDecider;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate.Sampler;
import org.jetbrains.annotations.NotNull;

public class WoverEndBiomeSource extends WoverBiomeSource implements BiomeSourceWithConfig<WoverEndBiomeSource, WoverEndConfig> {
   public static MapCodec<WoverEndBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.LONG.fieldOf("seed").stable().forGetter(source -> source.currentSeed),
            WoverEndConfig.CODEC.fieldOf("config").orElse(WoverEndConfig.DEFAULT).forGetter(o -> o.config)
         )
         .apply(instance, instance.stable(WoverEndBiomeSource::new))
   );
   public static final List<TagKey<Biome>> TAGS = List.of(
      CommonBiomeTags.IS_END_CENTER,
      CommonBiomeTags.IS_END_BARRENS,
      CommonBiomeTags.IS_SMALL_END_ISLAND,
      CommonBiomeTags.IS_END_HIGHLAND,
      CommonBiomeTags.IS_END_MIDLAND,
      BiomeTags.IS_END
   );
   private final Point pos;
   private BiomeMap mapLand;
   private BiomeMap mapVoid;
   private BiomeMap mapCenter;
   private BiomeMap mapBarrens;
   private WoverBiomePicker endLandBiomePicker;
   private WoverBiomePicker endVoidBiomePicker;
   private WoverBiomePicker endCenterBiomePicker;
   private WoverBiomePicker endBarrensBiomePicker;
   private List<BiomeDecider> deciders;
   private WoverEndConfig config;

   private WoverEndBiomeSource(long seed, WoverEndConfig config) {
      this(seed, config, true);
   }

   public WoverEndBiomeSource(WoverEndConfig config) {
      this(0L, config, false);
   }

   private WoverEndBiomeSource(long seed, WoverEndConfig config, boolean initMaps) {
      super(seed);
      this.config = config;
      this.rebuildBiomes(false);
      this.pos = new Point();
      if (initMaps) {
         this.initMap(seed);
      }
   }

   @Override
   protected List<WoverBiomeSource.TagToPicker> createFreshPickerMap() {
      this.deciders = BiomeDeciderImpl.DECIDERS.stream().filter(d -> d.canProvideFor(this)).map(d -> d.createInstance(this)).toList();
      this.endLandBiomePicker = new WoverBiomePicker(this.fallbackBiome());
      this.endVoidBiomePicker = new WoverBiomePicker(Biomes.SMALL_END_ISLANDS);
      this.endCenterBiomePicker = new WoverBiomePicker(Biomes.THE_END);
      this.endBarrensBiomePicker = new WoverBiomePicker(Biomes.END_BARRENS);
      List<WoverBiomeSource.TagToPicker> pickerMap = new ArrayList<>();

      for (BiomeDecider decider : this.deciders) {
         TagKey<Biome> deciderTag = decider.pickerTag();
         WoverBiomePicker deciderPicker = decider.picker();
         if (deciderTag != null && deciderPicker != null) {
            pickerMap.add(new WoverBiomeSource.TagToPicker(deciderTag, deciderPicker));
         }
      }

      pickerMap.add(new WoverBiomeSource.TagToPicker(CommonBiomeTags.IS_END_CENTER, this.endCenterBiomePicker));
      pickerMap.add(new WoverBiomeSource.TagToPicker(CommonBiomeTags.IS_END_BARRENS, this.endBarrensBiomePicker));
      pickerMap.add(new WoverBiomeSource.TagToPicker(CommonBiomeTags.IS_SMALL_END_ISLAND, this.endVoidBiomePicker));
      pickerMap.add(new WoverBiomeSource.TagToPicker(CommonBiomeTags.IS_END_HIGHLAND, this.endLandBiomePicker));
      pickerMap.add(new WoverBiomeSource.TagToPicker(CommonBiomeTags.IS_END_MIDLAND, this.endLandBiomePicker));
      pickerMap.add(new WoverBiomeSource.TagToPicker(BiomeTags.IS_END, this.endLandBiomePicker));
      return pickerMap;
   }

   @Override
   protected boolean addToPicker(BiomeData biomeData, TagKey<Biome> type, WoverBiomePicker picker) {
      picker.addBiome(biomeData);
      return !type.equals(CommonBiomeTags.IS_END_BARRENS) || !biomeData.isIntendedFor(CommonBiomeTags.IS_SMALL_END_ISLAND);
   }

   @Override
   protected TagKey<Biome> defaultBiomeTag() {
      return CommonBiomeTags.IS_END_HIGHLAND;
   }

   @Override
   protected List<TagKey<Biome>> acceptedTags() {
      return TAGS;
   }

   @Override
   protected ResourceKey<Biome> fallbackBiome() {
      return Biomes.END_HIGHLANDS;
   }

   @Override
   public String toShortString() {
      return "WoVer - The End  BiomeSource (" + Integer.toHexString(this.hashCode()) + ")";
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
         + "\n    deciders   = "
         + this.deciders.size()
         + "\n    config     = "
         + this.config;
   }

   @Override
   protected void onInitMap(long newSeed) {
      for (BiomeDecider decider : this.deciders) {
         decider.createMap((picker, size) -> this.config.mapVersion.mapBuilder.create(newSeed, size <= 0 ? this.config.landBiomesSize : size, picker), newSeed);
      }

      this.mapLand = this.config.mapVersion.mapBuilder.create(newSeed, this.config.landBiomesSize, this.endLandBiomePicker);
      this.mapVoid = this.config.mapVersion.mapBuilder.create(newSeed, this.config.voidBiomesSize, this.endVoidBiomePicker);
      this.mapCenter = this.config.mapVersion.mapBuilder.create(newSeed, this.config.centerBiomesSize, this.endCenterBiomePicker);
      this.mapBarrens = this.config.mapVersion.mapBuilder.create(newSeed, this.config.barrensBiomesSize, this.endBarrensBiomePicker);
   }

   @Override
   protected void onHeightChange(int newHeight) {
   }

   @Override
   protected void onFinishBiomeRebuild(List<WoverBiomeSource.TagToPicker> pickerMap) {
      super.onFinishBiomeRebuild(pickerMap);

      for (BiomeDecider decider : this.deciders) {
         decider.rebuild();
      }

      if (WorldState.allStageRegistryAccess() != null) {
         this.endBarrensBiomePicker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(Biomes.END_BARRENS));
         this.endBarrensBiomePicker.rebuild();
      }

      boolean voidWasEmpty = this.endVoidBiomePicker.isEmpty();
      if (voidWasEmpty) {
         if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null) {
            LibWoverWorldGenerator.C.log.verbose("No Void Biomes found. Disabling by using barrens");
         }

         this.endVoidBiomePicker = this.endBarrensBiomePicker;
      }

      if (this.endBarrensBiomePicker.isEmpty()) {
         if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null) {
            LibWoverWorldGenerator.C.log.verbose("No Barrens Biomes found. Disabling by using land Biomes");
         }

         this.endBarrensBiomePicker = this.endLandBiomePicker;
         if (voidWasEmpty) {
            this.endVoidBiomePicker = this.endLandBiomePicker;
         }
      }

      if (this.endCenterBiomePicker.isEmpty()) {
         if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null) {
            LibWoverWorldGenerator.C.log.verbose("No Center Island Biomes found. Forcing use of vanilla center.");
         }

         this.endCenterBiomePicker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(Biomes.THE_END));
         this.endCenterBiomePicker.rebuild();
         if (this.endCenterBiomePicker.isEmpty()) {
            if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null) {
               LibWoverWorldGenerator.C.log.verbose("Unable to force vanilla central Island. Falling back to land Biomes...");
            }

            this.endCenterBiomePicker = this.endLandBiomePicker;
         }
      }
   }

   @NotNull
   protected MapCodec<? extends BiomeSource> codec() {
      return CODEC;
   }

   @NotNull
   public BiomeResolver createResolver(@NotNull Sampler sampler) {
      return (biomeX, biomeY, biomeZ) -> this.getNoiseBiome(biomeX, biomeY, biomeZ, sampler);
   }

   @NotNull
   private Holder<Biome> getNoiseBiome(int biomeX, int biomeY, int biomeZ, @NotNull Sampler sampler) {
      if (!this.wasBound()) {
         this.reloadBiomes(false);
      }

      if (this.mapLand != null && this.mapVoid != null && this.mapCenter != null && this.mapBarrens != null) {
         int posX = QuartPos.toBlock(biomeX);
         int posY = QuartPos.toBlock(biomeY);
         int posZ = QuartPos.toBlock(biomeZ);
         long dist = Math.abs(posX) + Math.abs(posZ) > this.config.innerVoidRadiusSquared
            ? this.config.innerVoidRadiusSquared + 1L
            : (long)posX * posX + (long)posZ * posZ;
         if ((biomeX & 63) == 0 || (biomeZ & 63) == 0) {
            this.mapLand.clearCache();
            this.mapVoid.clearCache();
            this.mapCenter.clearCache();
            this.mapBarrens.clearCache();

            for (BiomeDecider decider : this.deciders) {
               decider.clearMapCache();
            }
         }

         int x = (SectionPos.blockToSectionCoord(posX) * 2 + 1) * 8;
         int z = (SectionPos.blockToSectionCoord(posZ) * 2 + 1) * 8;
         double d = sampler.erosion().sampleValue(x, posY, z);
         TagKey<Biome> suggestedType;
         if (dist <= this.config.innerVoidRadiusSquared) {
            suggestedType = CommonBiomeTags.IS_END_CENTER;
         } else if (d > 0.25) {
            suggestedType = CommonBiomeTags.IS_END_HIGHLAND;
         } else if (d >= -0.0625) {
            suggestedType = CommonBiomeTags.IS_END_MIDLAND;
         } else {
            suggestedType = d < -0.21875
               ? CommonBiomeTags.IS_SMALL_END_ISLAND
               : (this.config.withVoidBiomes ? CommonBiomeTags.IS_END_BARRENS : CommonBiomeTags.IS_END_HIGHLAND);
         }

         TagKey<Biome> originalType = suggestedType;

         for (BiomeDecider decider : this.deciders) {
            suggestedType = decider.suggestType(originalType, suggestedType, d, this.maxHeight, posX, posY, posZ, biomeX, biomeY, biomeZ);
         }

         for (BiomeDecider decider : this.deciders) {
            if (decider.canProvideBiome(suggestedType)) {
               WoverBiomePicker.PickableBiome result = decider.provideBiome(suggestedType, posX, posY, posZ);
               if (result != null) {
                  return result.biome;
               }
            }
         }

         if (suggestedType == CommonBiomeTags.IS_END_CENTER) {
            return this.mapCenter.getBiome(posX, posY, posZ).biome;
         } else if (suggestedType == CommonBiomeTags.IS_SMALL_END_ISLAND) {
            return this.mapVoid.getBiome(posX, posY, posZ).biome;
         } else {
            return suggestedType == CommonBiomeTags.IS_END_BARRENS
               ? this.mapBarrens.getBiome(posX, posY, posZ).biome
               : this.mapLand.getBiome(posX, posY, posZ).biome;
         }
      } else {
         return (Holder<Biome>)this.possibleBiomes().stream().findFirst().orElseThrow();
      }
   }

   public WoverBiomePicker.PickableBiome landBiomeAt(int posX, int posZ) {
      return this.mapLand == null ? null : this.mapLand.getBiome(posX, 0.0, posZ);
   }

   public WoverEndConfig getBiomeSourceConfig() {
      return this.config;
   }

   public void setBiomeSourceConfig(WoverEndConfig newConfig) {
      this.config = newConfig;
      this.rebuildBiomes(true);
      this.initMap(this.currentSeed);
   }
}

