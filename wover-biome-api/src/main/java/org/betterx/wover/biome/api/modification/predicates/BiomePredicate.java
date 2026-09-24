package org.betterx.wover.biome.api.modification.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.impl.modification.predicates.Always;
import org.betterx.wover.biome.impl.modification.predicates.And;
import org.betterx.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;
import org.betterx.wover.biome.impl.modification.predicates.ConfigIs;
import org.betterx.wover.biome.impl.modification.predicates.HasFeature;
import org.betterx.wover.biome.impl.modification.predicates.HasPlacedFeature;
import org.betterx.wover.biome.impl.modification.predicates.HasStructure;
import org.betterx.wover.biome.impl.modification.predicates.HasTag;
import org.betterx.wover.biome.impl.modification.predicates.InDimension;
import org.betterx.wover.biome.impl.modification.predicates.IsBiome;
import org.betterx.wover.biome.impl.modification.predicates.IsNamespace;
import org.betterx.wover.biome.impl.modification.predicates.LocationPathContains;
import org.betterx.wover.biome.impl.modification.predicates.Not;
import org.betterx.wover.biome.impl.modification.predicates.Or;
import org.betterx.wover.biome.impl.modification.predicates.Spawns;
import org.betterx.wover.core.api.ModCore;
import de.ambertation.wunderlib.configs.AbstractConfig;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface BiomePredicate {
   Codec<BiomePredicate> CODEC = BiomePredicateRegistryImpl.BIOME_PREDICATES.byNameCodec().dispatch(BiomePredicate::codec, Function.identity());

   static BiomePredicate or(BiomePredicate... predicates) {
      return new Or(List.of(predicates));
   }

   static BiomePredicate and(BiomePredicate... predicates) {
      return new And(List.of(predicates));
   }

   static BiomePredicate anyOf(BiomePredicate... predicates) {
      return or(predicates);
   }

   static BiomePredicate allOf(BiomePredicate... predicates) {
      return and(predicates);
   }

   static BiomePredicate not(BiomePredicate predicate) {
      return new Not(predicate);
   }

   static BiomePredicate always() {
      return Always.INSTANCE;
   }

   static BiomePredicate isBiome(ResourceKey<Biome> key) {
      return new IsBiome(key);
   }

   @SafeVarargs
   static BiomePredicate inBiomes(ResourceKey<Biome>... keys) {
      return new Or(Arrays.stream(keys).<BiomePredicate>map(IsBiome::new).toList());
   }

   @SafeVarargs
   static BiomePredicate notInBiomes(ResourceKey<Biome>... keys) {
      return new Not(inBiomes(keys));
   }

   static BiomePredicate inDimension(ResourceKey<LevelStem> key) {
      return new InDimension(key);
   }

   static BiomePredicate inOverworld() {
      return InDimension.OVERWORLD;
   }

   static BiomePredicate inEnd() {
      return InDimension.END;
   }

   static BiomePredicate inNether() {
      return InDimension.NETHER;
   }

   static BiomePredicate hasTag(TagKey<Biome> tag) {
      return new HasTag(tag);
   }

   static BiomePredicate spawns(EntityType<?> type) {
      return new Spawns(type);
   }

   static BiomePredicate hasStructure(ResourceKey<Structure> key) {
      return new HasStructure(key);
   }

   static BiomePredicate hasPlacedFeature(ResourceKey<PlacedFeature> key) {
      return new HasPlacedFeature(key);
   }

   static BiomePredicate hasFeature(ResourceKey<Feature> key) {
      return new HasFeature(key);
   }

   static BiomePredicate isVanilla() {
      return new IsNamespace("minecraft");
   }

   static BiomePredicate inNamespace(String namespace) {
      return new IsNamespace(namespace);
   }

   static BiomePredicate inNamespace(ModCore core) {
      return new IsNamespace(core.namespace);
   }

   static BiomePredicate notInNamespace(String namespace) {
      return not(new IsNamespace(namespace));
   }

   static BiomePredicate notInNamespace(ModCore core) {
      return not(new IsNamespace(core.namespace));
   }

   static BiomePredicate pathContains(String needle) {
      return new LocationPathContains(needle);
   }

   static <T, R extends AbstractConfig<?>.Value<T, R>> BiomePredicate hasConfig(AbstractConfig<?>.Value<T, R> value, T targetValue) {
      return ConfigIs.of(value, targetValue);
   }

   @Internal
   MapCodec<? extends BiomePredicate> codec();

   boolean test(BiomePredicate.Context var1);

   public static final class Context {
      @NotNull
      public final RegistryAccess registryAccess;
      @NotNull
      public final ResourceKey<Biome> biomeKey;
      @NotNull
      public final Biome biome;
      @NotNull
      public final Holder<Biome> biomeHolder;
      @NotNull
      public final Registry<Biome> biomes;
      @NotNull
      public final Registry<LevelStem> levelStems;
      @NotNull
      public final Registry<Structure> structures;
      @NotNull
      public final Registry<PlacedFeature> placedFeatures;
      @NotNull
      public final Registry<Feature> features;

      private Context(@NotNull RegistryAccess registryAccess, @NotNull Registry<Biome> biomes, @NotNull ResourceKey<Biome> biomeKey, @NotNull Biome biome) {
         this.registryAccess = registryAccess;
         this.biomeKey = biomeKey;
         this.biomes = biomes;
         this.levelStems = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
         this.structures = registryAccess.lookupOrThrow(Registries.STRUCTURE);
         this.placedFeatures = registryAccess.lookupOrThrow(Registries.PLACED_FEATURE);
         this.features = registryAccess.lookupOrThrow(Registries.FEATURE);
         this.biome = biome;
         this.biomeHolder = biomes.getOrThrow(biomeKey);
      }

      @Internal
      @Nullable
      public static BiomePredicate.Context of(@Nullable RegistryAccess registryAccess, @NotNull ResourceKey<Biome> biomeKey) {
         if (registryAccess == null) {
            return null;
         } else {
            Registry<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);
            return of(registryAccess, biomes, biomeKey);
         }
      }

      @Internal
      @Nullable
      public static BiomePredicate.Context of(@Nullable RegistryAccess registryAccess, @Nullable Registry<Biome> biomes, @NotNull ResourceKey<Biome> biomeKey) {
         if (biomes != null && registryAccess != null) {
            Reference<Biome> biome = (Reference<Biome>)biomes.get(biomeKey).orElse(null);
            return biome != null && biome.isBound() ? new BiomePredicate.Context(registryAccess, biomes, biomeKey, (Biome)biome.value()) : null;
         } else {
            return null;
         }
      }
   }
}
