package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import java.util.List;

public record And(List<BiomePredicate> predicates) implements BiomePredicate {
   public static final MapCodec<And> CODEC = BiomePredicate.CODEC.listOf().xmap(And::new, And::predicates).fieldOf("predicates");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      return this.predicates().stream().allMatch(p -> p.test(ctx));
   }
}
