package org.betterx.wover.feature.impl.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.feature.api.features.TemplateFeature;
import org.betterx.wover.structure.api.StructureNBT;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public class FeatureTemplateImpl extends StructureNBT implements TemplateFeature.FeatureTemplate {
   public static final Codec<TemplateFeature.FeatureTemplate> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Identifier.CODEC.fieldOf("location").forGetter(cfg -> cfg.getLocation()),
            Codec.INT.fieldOf("offset_y").orElse(0).forGetter(cfg -> cfg.getOffsetY())
         )
         .apply(instance, FeatureTemplateImpl::new)
   );
   public final int offsetY;
   private static final Map<String, FeatureTemplateImpl> READER_CACHE = new ConcurrentHashMap<>();

   protected FeatureTemplateImpl(Identifier location, int offsetY) {
      super(location);
      this.offsetY = offsetY;
   }

   public static TemplateFeature.FeatureTemplate createTemplate(Identifier location) {
      return createTemplate(location, 0);
   }

   public static TemplateFeature.FeatureTemplate createTemplate(Identifier location, int offsetY) {
      String key = location.toString() + "::" + offsetY;
      return READER_CACHE.computeIfAbsent(key, r -> new FeatureTemplateImpl(location, offsetY));
   }

   public boolean generateIfPlaceable(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
      return this.generateIfPlaceable(level, pos, getRandomRotation(random), getRandomMirror(random));
   }

   @Override
   public boolean generateIfPlaceable(ServerLevelAccessor level, BlockPos pos, Rotation r, Mirror m) {
      return this.canGenerate(level, pos, r) ? this.generate(level, pos, r, m) : false;
   }

   public boolean generate(ServerLevelAccessor level, BlockPos pos, Rotation r, Mirror m) {
      return this.generateCentered(level, pos.above(this.offsetY), r, m);
   }

   public boolean canGenerate(LevelAccessor level, BlockPos pos, Rotation rotation) {
      return !this.containsBedrock(level, pos);
   }

   private boolean containsBedrock(LevelAccessor level, BlockPos startPos) {
      for (int i = 0; i < this.structure.getSize().getY(); i += 2) {
         if (level.getBlockState(startPos.above(i)).is(Blocks.BEDROCK)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean loaded() {
      return this.structure != null;
   }

   @Override
   public int getOffsetY() {
      return this.offsetY;
   }

   @Override
   public Identifier getLocation() {
      return this.location;
   }
}
