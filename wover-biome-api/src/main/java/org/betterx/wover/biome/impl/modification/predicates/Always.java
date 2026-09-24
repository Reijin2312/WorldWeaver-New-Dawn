package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

public class Always implements BiomePredicate {
   public static final Always INSTANCE = new Always();
   public static final MapCodec<Always> CODEC = MapCodec.unit(INSTANCE);

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      return true;
   }
}
