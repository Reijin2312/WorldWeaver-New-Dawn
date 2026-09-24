package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

public record Not(BiomePredicate predicate) implements BiomePredicate {
   public static final MapCodec<Not> CODEC = BiomePredicate.CODEC.xmap(Not::new, Not::predicate).fieldOf("predicate");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      return !this.predicate().test(ctx);
   }
}
