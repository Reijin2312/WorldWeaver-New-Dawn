package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public record HasStructure(ResourceKey<Structure> key) implements BiomePredicate {
   public static final MapCodec<HasStructure> CODEC = ResourceKey.codec(Registries.STRUCTURE)
      .xmap(HasStructure::new, HasStructure::key)
      .fieldOf("structure_key");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      Structure instance = (Structure)((Reference)ctx.structures.get(this.key).orElse(null)).value();
      return instance == null ? false : instance.biomes().contains(ctx.biomeHolder);
   }
}
