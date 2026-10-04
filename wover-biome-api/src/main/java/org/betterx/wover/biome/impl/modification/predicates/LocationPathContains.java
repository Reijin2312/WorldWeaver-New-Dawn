package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

public record LocationPathContains(String needle) implements BiomePredicate {
   public static final MapCodec<LocationPathContains> CODEC = Codec.STRING.xmap(LocationPathContains::new, LocationPathContains::needle).fieldOf("needle");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      return ctx.biomeKey.identifier().getPath().contains(this.needle);
   }
}
