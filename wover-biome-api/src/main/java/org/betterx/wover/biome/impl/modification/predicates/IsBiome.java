package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public record IsBiome(ResourceKey<Biome> biomeKey) implements BiomePredicate {
   public static final MapCodec<IsBiome> CODEC = ResourceKey.codec(Registries.BIOME).xmap(IsBiome::new, IsBiome::biomeKey).fieldOf("biome_key");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      return ctx.biomeKey.equals(this.biomeKey);
   }
}
