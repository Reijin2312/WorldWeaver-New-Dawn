package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IsNextTo implements PlacementFilter {
   public static final MapCodec<IsNextTo> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(cfg -> cfg.predicate),
            Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(cfg -> cfg.offset)
         )
         .apply(instance, IsNextTo::new)
   );
   private final BlockPredicate predicate;
   private final Vec3i offset;

   private IsNextTo(@NotNull BlockPredicate predicate, @NotNull Vec3i offset) {
      this.predicate = predicate;
      this.offset = offset;
   }

   public static PlacementFilter simple(@NotNull BlockPredicate predicate) {
      return new IsNextTo(predicate, Vec3i.ZERO);
   }

   public static PlacementFilter offset(@NotNull BlockPredicate predicate, @Nullable Vec3i offset) {
      return new IsNextTo(predicate, offset);
   }

   public boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
      WorldGenLevel level = ctx.getLevel();
      pos = pos.offset(this.offset);
      return this.predicate.test(level, pos.west())
         || this.predicate.test(level, pos.east())
         || this.predicate.test(level, pos.north())
         || this.predicate.test(level, pos.south());
   }

   @NotNull
   public MapCodec<IsNextTo> codec() {
      return CODEC;
   }
}
