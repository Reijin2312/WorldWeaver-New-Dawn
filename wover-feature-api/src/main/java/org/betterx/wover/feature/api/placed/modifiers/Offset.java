package org.betterx.wover.feature.api.placed.modifiers;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public class Offset implements PlacementModifier {
   private static final Map<Direction, Offset> DIRECTIONS = Maps.newHashMap();
   public static final MapCodec<Offset> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(Vec3i.CODEC.fieldOf("blocks").forGetter(cfg -> cfg.offset)).apply(instance, Offset::new)
   );
   private final Vec3i offset;

   public Offset(Vec3i offset) {
      this.offset = offset;
   }

   public static Offset inDirection(Direction dir) {
      return DIRECTIONS.get(dir);
   }

   public void modify(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, Consumer<BlockPos> consumer) {
      consumer.accept(blockPos.offset(this.offset));
   }

   @NotNull
   public MapCodec<Offset> codec() {
      return CODEC;
   }

   static {
      for (Direction d : Direction.values()) {
         DIRECTIONS.put(d, new Offset(d.getUnitVec3i()));
      }
   }
}
