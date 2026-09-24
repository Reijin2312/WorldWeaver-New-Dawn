package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BiomeModificationImpl implements BiomeModification {
   @NotNull
   private final BiomePredicate predicate;
   @NotNull
   private final FeatureMap features;
   @Nullable
   private final List<TagKey<Biome>> biomeTags;
   @NotNull
   private final WeightedList<SpawnerData> spawns;

   public BiomeModificationImpl(
      @NotNull BiomePredicate predicate,
      @NotNull List<List<Holder<PlacedFeature>>> features,
      @Nullable List<TagKey<Biome>> biomeTags,
      @Nullable WeightedList<SpawnerData> spawns
   ) {
      this.predicate = predicate;
      this.features = FeatureMap.of(features);
      this.biomeTags = biomeTags;
      this.spawns = spawns;
   }

   @Override
   public BiomePredicate predicate() {
      return this.predicate;
   }

   @Override
   public List<TagKey<Biome>> biomeTags() {
      return this.biomeTags;
   }

   @Override
   public final List<List<Holder<PlacedFeature>>> features() {
      return this.features.generic();
   }

   @Override
   public WeightedList<SpawnerData> spawns() {
      return this.spawns;
   }

   @Override
   public void apply(GenerationSettingsWorker worker, MobSettingsWorker mobWorker) {
      worker.addFeatures(this.features);
      mobWorker.addSpawns(this.spawns);
   }
}
