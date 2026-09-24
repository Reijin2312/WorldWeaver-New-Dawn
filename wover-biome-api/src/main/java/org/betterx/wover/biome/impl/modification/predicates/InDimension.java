package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

public record InDimension(ResourceKey<LevelStem> dimensionKey) implements BiomePredicate {
   public static final InDimension OVERWORLD = new InDimension(LevelStem.OVERWORLD);
   public static final InDimension END = new InDimension(LevelStem.END);
   public static final InDimension NETHER = new InDimension(LevelStem.NETHER);
   public static final MapCodec<InDimension> CODEC = ResourceKey.codec(Registries.LEVEL_STEM)
      .xmap(InDimension::new, InDimension::dimensionKey)
      .fieldOf("dimension_key");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      Holder<LevelStem> dimension = (Holder<LevelStem>)ctx.levelStems.get(this.dimensionKey).orElse(null);
      return dimension != null && dimension.isBound()
         ? ((LevelStem)dimension.value())
            .generator()
            .getBiomeSource()
            .possibleBiomes()
            .stream()
            .<Optional>map(Holder::unwrapKey)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(entry -> entry.equals(ctx.biomeKey))
         : false;
   }
}
