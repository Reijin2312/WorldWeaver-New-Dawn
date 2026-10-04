package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record HasFeature(ResourceKey<Feature> key) implements BiomePredicate {
   public static final MapCodec<HasFeature> CODEC = ResourceKey.codec(Registries.FEATURE).xmap(HasFeature::new, HasFeature::key).fieldOf("feature_key");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      for (HolderSet<PlacedFeature> featuresForStep : ctx.biome.getGenerationSettings().features()) {
         for (Holder<PlacedFeature> holders : featuresForStep) {
            if (((PlacedFeature)holders.value())
               .getFeatures()
               .map(Holder::value)
               .<Optional>map(ctx.features::getResourceKey)
               .filter(Optional::isPresent)
               .map(Optional::get)
               .anyMatch(fkey -> fkey.equals(this.key))) {
               return true;
            }
         }
      }

      return false;
   }
}
