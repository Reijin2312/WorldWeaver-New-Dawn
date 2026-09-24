package org.betterx.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.feature.impl.features.FeatureTemplateImpl;
import org.betterx.wover.structure.api.StructureNBT;
import org.betterx.wover.util.RandomizedWeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public class TemplateFeature implements Feature {
   public static final MapCodec<TemplateFeature> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(RandomizedWeightedList.buildCodec(FeatureTemplateImpl.CODEC).fieldOf("structures").forGetter(cfg -> cfg.structures))
         .apply(instance, TemplateFeature::of)
   );
   private final RandomizedWeightedList<TemplateFeature.FeatureTemplate> structures;

   public static TemplateFeature of(RandomizedWeightedList<TemplateFeature.FeatureTemplate> structures) {
      return new TemplateFeature(structures);
   }

   private TemplateFeature(RandomizedWeightedList<TemplateFeature.FeatureTemplate> structures) {
      this.structures = structures;
   }

   public MapCodec<TemplateFeature> codec() {
      return CODEC;
   }

   public TemplateFeature.FeatureTemplate randomStructure(RandomSource random) {
      return (TemplateFeature.FeatureTemplate)this.structures.getRandomValue(random);
   }

   public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
      TemplateFeature.FeatureTemplate structure = this.randomStructure(random);
      return structure.generateIfPlaceable(level, pos, StructureNBT.getRandomRotation(random), StructureNBT.getRandomMirror(random));
   }

   public interface FeatureTemplate {
      int getOffsetY();

      Identifier getLocation();

      boolean generateIfPlaceable(ServerLevelAccessor var1, BlockPos var2, Rotation var3, Mirror var4);

      boolean loaded();
   }
}
