package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.LibWoverBiome;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BiomePredicateRegistryImpl {
   public static final ResourceKey<Registry<MapCodec<? extends BiomePredicate>>> BIOME_PREDICATE_REGISTRY = DatapackRegistryBuilder.createRegistryKey(
      LibWoverBiome.C.id("wover/biome_predicates")
   );
   public static final Registry<MapCodec<? extends BiomePredicate>> BIOME_PREDICATES = BuiltInRegistryManager.createRegistry(
      BIOME_PREDICATE_REGISTRY, BiomePredicateRegistryImpl::onBootstrap
   );

   public static MapCodec<? extends BiomePredicate> register(
      Registry<MapCodec<? extends BiomePredicate>> registry, Identifier location, MapCodec<? extends BiomePredicate> codec
   ) {
      return (MapCodec<? extends BiomePredicate>)Registry.register(registry, location, codec);
   }

   @Internal
   public static void initialize() {
      onBootstrap(BIOME_PREDICATES);
   }

   private static MapCodec<? extends BiomePredicate> onBootstrap(Registry<MapCodec<? extends BiomePredicate>> registry) {
      Identifier all = LibWoverBiome.C.id("all");
      if (registry.containsKey(all)) {
         return (MapCodec<? extends BiomePredicate>)((Reference)registry.get(all).orElseThrow()).value();
      } else {
         register(registry, LibWoverBiome.C.id("not"), Not.CODEC);
         register(registry, LibWoverBiome.C.id("and"), And.CODEC);
         register(registry, LibWoverBiome.C.id("or"), Or.CODEC);
         register(registry, LibWoverBiome.C.id("is_biome"), IsBiome.CODEC);
         register(registry, LibWoverBiome.C.id("has_tag"), HasTag.CODEC);
         register(registry, LibWoverBiome.C.id("in_dimension"), InDimension.CODEC);
         register(registry, LibWoverBiome.C.id("is_namespace"), IsNamespace.CODEC);
         register(registry, LibWoverBiome.C.id("location_path_contains"), LocationPathContains.CODEC);
         register(registry, LibWoverBiome.C.id("spawns"), Spawns.CODEC);
         register(registry, LibWoverBiome.C.id("has_structure"), HasStructure.CODEC);
         register(registry, LibWoverBiome.C.id("has_placed_feature"), HasPlacedFeature.CODEC);
         register(registry, LibWoverBiome.C.id("has_feature"), HasFeature.CODEC);
         register(registry, LibWoverBiome.C.id("config_is"), ConfigIs.CODEC);
         return register(registry, all, Always.CODEC);
      }
   }
}
