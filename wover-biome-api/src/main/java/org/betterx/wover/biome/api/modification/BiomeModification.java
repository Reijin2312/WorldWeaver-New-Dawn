package org.betterx.wover.biome.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.biome.impl.modification.BiomeModificationImpl;
import org.betterx.wover.biome.impl.modification.FeatureMap;
import org.betterx.wover.biome.impl.modification.GenerationSettingsWorker;
import org.betterx.wover.biome.impl.modification.MobSettingsWorker;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.BasePlacedFeatureKey;
import org.betterx.wover.structure.api.StructureKey;
import de.ambertation.wunderlib.configs.AbstractConfig;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface BiomeModification {
   Codec<BiomeModification> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BiomePredicate.CODEC.fieldOf("predicate").forGetter(BiomeModification::predicate),
            FeatureMap.CODEC.optionalFieldOf("features", List.of()).forGetter(BiomeModification::features),
            TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("biome_tags", List.of()).forGetter(BiomeModification::biomeTags),
            WeightedList.codec(SpawnerData.CODEC)
               .optionalFieldOf("spawns", WeightedList.<SpawnerData>builder().build())
               .forGetter(BiomeModification::spawns)
         )
         .apply(instance, BiomeModificationImpl::new)
   );

   BiomePredicate predicate();

   List<List<Holder<PlacedFeature>>> features();

   WeightedList<SpawnerData> spawns();

   List<TagKey<Biome>> biomeTags();

   @Internal
   void apply(GenerationSettingsWorker var1, MobSettingsWorker var2);

   static BiomeModification.Builder build(@NotNull BootstrapContext<BiomeModification> context, @NotNull Identifier location) {
      return new BiomeModification.Builder(context, ResourceKey.create(BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY, location));
   }

   static BiomeModification.Builder build(@NotNull BootstrapContext<BiomeModification> context, @NotNull ResourceKey<BiomeModification> key) {
      return new BiomeModification.Builder(context, key);
   }

   public static final class Builder {
      @Nullable
      private final BootstrapContext<BiomeModification> bootstrapContext;
      private BiomePredicate predicate;
      private final FeatureMap features;
      private final net.minecraft.util.random.WeightedList.Builder<SpawnerData> spawns;
      private final Set<TagKey<Biome>> tags = new LinkedHashSet<>();
      private final ResourceKey<BiomeModification> key;

      private Builder(@Nullable BootstrapContext<BiomeModification> bootstrapContext, ResourceKey<BiomeModification> key) {
         this.bootstrapContext = bootstrapContext;
         this.key = key;
         this.predicate = BiomePredicate.always();
         this.features = FeatureMap.of(new ArrayList<>(Decoration.values().length));
         this.spawns = WeightedList.builder();
      }

      public BiomeModification.Builder predicate(BiomePredicate p) {
         this.predicate = p;
         return this;
      }

      public BiomeModification.Builder isBiome(ResourceKey<Biome> key) {
         this.predicate(BiomePredicate.isBiome(key));
         return this;
      }

      @SafeVarargs
      public final BiomeModification.Builder inBiomes(ResourceKey<Biome>... keys) {
         return this.predicate(BiomePredicate.inBiomes(keys));
      }

      @SafeVarargs
      public final BiomeModification.Builder notInBiomes(ResourceKey<Biome>... keys) {
         return this.predicate(BiomePredicate.notInBiomes(keys));
      }

      public BiomeModification.Builder inDimension(ResourceKey<LevelStem> key) {
         return this.predicate(BiomePredicate.inDimension(key));
      }

      public BiomeModification.Builder inOverworld() {
         return this.predicate(BiomePredicate.inOverworld());
      }

      public BiomeModification.Builder inEnd() {
         return this.predicate(BiomePredicate.inEnd());
      }

      public BiomeModification.Builder inNether() {
         return this.predicate(BiomePredicate.inNether());
      }

      public BiomeModification.Builder hasTag(TagKey<Biome> tag) {
         return this.predicate(BiomePredicate.hasTag(tag));
      }

      public BiomeModification.Builder spawns(EntityType<?> type) {
         return this.predicate(BiomePredicate.spawns(type));
      }

      public BiomeModification.Builder hasStructure(ResourceKey<Structure> key) {
         return this.predicate(BiomePredicate.hasStructure(key));
      }

      public BiomeModification.Builder hasPlacedFeature(ResourceKey<PlacedFeature> key) {
         return this.predicate(BiomePredicate.hasPlacedFeature(key));
      }

      public BiomeModification.Builder hasFeature(ResourceKey<Feature> key) {
         return this.predicate(BiomePredicate.hasFeature(key));
      }

      public BiomeModification.Builder anyOf(BiomePredicate... predicates) {
         return this.predicate(BiomePredicate.or(predicates));
      }

      public BiomeModification.Builder allOf(BiomePredicate... predicates) {
         return this.predicate(BiomePredicate.and(predicates));
      }

      public BiomeModification.Builder not(BiomePredicate predicate) {
         return this.predicate(BiomePredicate.not(predicate));
      }

      public BiomeModification.Builder isVanilla() {
         return this.predicate(BiomePredicate.isVanilla());
      }

      public BiomeModification.Builder inNamespace(String namespace) {
         return this.predicate(BiomePredicate.inNamespace(namespace));
      }

      public BiomeModification.Builder inNamespace(ModCore core) {
         return this.predicate(BiomePredicate.inNamespace(core));
      }

      public BiomeModification.Builder notInNamespace(String namespace) {
         return this.predicate(BiomePredicate.notInNamespace(namespace));
      }

      public BiomeModification.Builder notInNamespace(ModCore core) {
         return this.predicate(BiomePredicate.notInNamespace(core));
      }

      public <T, R extends AbstractConfig<?>.Value<T, R>> BiomeModification.Builder hasConfig(AbstractConfig<?>.Value<T, R> value, T targetValue) {
         return this.predicate(BiomePredicate.hasConfig(value, targetValue));
      }

      public BiomeModification.Builder addFeature(Decoration decoration, ResourceKey<PlacedFeature> featureKey) {
         if (this.bootstrapContext == null) {
            throw new IllegalStateException(
               "You can not add a ResourceKey for a PlacedFeature to a Biome Modification if no Bootstrap Context was supplied (" + this.key + ")."
            );
         } else {
            Reference<PlacedFeature> holder = this.bootstrapContext.lookup(Registries.PLACED_FEATURE).getOrThrow(featureKey);
            return this.addFeature(decoration, holder);
         }
      }

      public BiomeModification.Builder addFeature(Decoration decoration, Holder<PlacedFeature> holder) {
         this.features.getFeatures(decoration).add(holder);
         return this;
      }

      public BiomeModification.Builder addFeature(BasePlacedFeatureKey<?> feature) {
         if (this.bootstrapContext == null) {
            throw new IllegalStateException(
               "You can not add a PlacedFeatureKey to a Biome Modification if no Bootstrap Context was supplied (" + this.key + ")."
            );
         } else {
            return this.addFeature(feature.getDecoration(), feature.getHolder(this.bootstrapContext));
         }
      }

      public BiomeModification.Builder addStructureSet(StructureKey<?, ?, ?> structure) {
         if (this.bootstrapContext == null) {
            throw new IllegalStateException("You can not add a Structure to a Biome Modification if no Bootstrap Context was supplied (" + this.key + ").");
         } else {
            return this.addToTag(structure.biomeTag());
         }
      }

      public BiomeModification.Builder addStructureSet(TagKey<Biome> structureSet) {
         if (this.bootstrapContext == null) {
            throw new IllegalStateException("You can not add a Structure to a Biome Modification if no Bootstrap Context was supplied (" + this.key + ").");
         } else {
            return this.addToTag(structureSet);
         }
      }

      public <M extends Mob> BiomeModification.Builder addSpawn(EntityType<M> entityType, int weight, int minGroupCount, int maxGroupCount) {
         IntProvider count = (IntProvider)(minGroupCount == maxGroupCount ? ConstantInt.of(minGroupCount) : UniformInt.of(minGroupCount, maxGroupCount));
         return this.addSpawn(weight, new SpawnerData(entityType, count));
      }

      public <M extends Mob> BiomeModification.Builder addSpawn(int weight, SpawnerData spawnerData) {
         this.spawns.add(spawnerData, weight);
         return this;
      }

      public BiomeModification.Builder addToTag(TagKey<Biome> tag) {
         this.tags.add(tag);
         return this;
      }

      public Holder<BiomeModification> directHolder() {
         return Holder.direct(this.build());
      }

      public Holder<BiomeModification> register() {
         if (this.key == null) {
            throw new IllegalStateException("You need to specify a key when you register a Biome Modification.");
         } else if (this.bootstrapContext == null) {
            throw new IllegalStateException("You need to supply a key when you register a Biome Modification (" + this.key + ").");
         } else {
            return this.bootstrapContext.register(this.key, this.build());
         }
      }

      @NotNull
      private BiomeModificationImpl build() {
         return new BiomeModificationImpl(this.predicate, this.features.generic(), this.tags != null ? this.tags.stream().toList() : null, this.spawns.build());
      }
   }
}
