package org.betterx.wover.biome.api.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class BiomePredicateRegistry {
   public static final ResourceKey<Registry<MapCodec<? extends BiomePredicate>>> BIOME_PREDICATE_REGISTRY = BiomePredicateRegistryImpl.BIOME_PREDICATE_REGISTRY;
   public static final Registry<MapCodec<? extends BiomePredicate>> BIOME_PREDICATES = BiomePredicateRegistryImpl.BIOME_PREDICATES;

   public static MapCodec<? extends BiomePredicate> register(Identifier location, MapCodec<? extends BiomePredicate> codec) {
      return BiomePredicateRegistryImpl.register(BIOME_PREDICATES, location, codec);
   }

   private BiomePredicateRegistry() {
   }
}
