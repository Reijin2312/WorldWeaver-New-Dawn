package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

public record IsNamespace(String namespace) implements BiomePredicate {
   public static final MapCodec<IsNamespace> CODEC = Codec.STRING.xmap(IsNamespace::new, IsNamespace::namespace).fieldOf("namespace");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      return ctx.biomeKey.identifier().getNamespace().equals(this.namespace);
   }
}
