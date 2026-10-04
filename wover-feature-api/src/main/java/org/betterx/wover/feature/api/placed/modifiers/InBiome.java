package org.betterx.wover.feature.api.placed.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import org.jetbrains.annotations.NotNull;

public class InBiome implements PlacementFilter {
   public static final MapCodec<InBiome> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.BOOL.fieldOf("negate").orElse(false).forGetter(cfg -> cfg.negate),
            Codec.list(Identifier.CODEC).fieldOf("biomes").forGetter(cfg -> cfg.biomeIDs)
         )
         .apply(instance, InBiome::new)
   );
   public final List<Identifier> biomeIDs;
   public final boolean negate;

   protected InBiome(boolean negate, List<Identifier> biomeIDs) {
      this.biomeIDs = biomeIDs;
      this.negate = negate;
   }

   public static InBiome matchingID(Identifier... id) {
      return new InBiome(false, List.of(id));
   }

   public static InBiome matchingID(List<Identifier> ids) {
      return new InBiome(false, ids);
   }

   public static InBiome notMatchingID(Identifier... id) {
      return new InBiome(true, List.of(id));
   }

   public static InBiome notMatchingID(List<Identifier> ids) {
      return new InBiome(true, ids);
   }

   public boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
      Holder<Biome> holder = ctx.getLevel().getBiome(pos);
      Optional<Identifier> biomeLocation = holder.unwrapKey().map(key -> key.identifier());
      if (biomeLocation.isPresent()) {
         boolean contains = this.biomeIDs.contains(biomeLocation.get());
         return this.negate != contains;
      } else {
         return false;
      }
   }

   @NotNull
   public MapCodec<InBiome> codec() {
      return CODEC;
   }
}
