package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record HasPlacedFeature(ResourceKey<PlacedFeature> key) implements BiomePredicate {
   public static final MapCodec<HasPlacedFeature> CODEC = ResourceKey.codec(Registries.PLACED_FEATURE)
      .xmap(HasPlacedFeature::new, HasPlacedFeature::key)
      .fieldOf("feature_key");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      for (HolderSet<PlacedFeature> featuresForStep : ctx.biome.getGenerationSettings().features()) {
         for (Holder<PlacedFeature> holders : featuresForStep) {
            Optional<ResourceKey<PlacedFeature>> optionalKey = ctx.placedFeatures.getResourceKey((PlacedFeature)holders.value());
            if (optionalKey.map(fkey -> fkey.equals(this.key)).orElse(false)) {
               return true;
            }
         }
      }

      return false;
   }
}
