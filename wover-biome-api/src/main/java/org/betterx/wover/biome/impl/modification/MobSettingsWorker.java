package org.betterx.wover.biome.impl.modification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.Builder;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;

public class MobSettingsWorker {
   private final Biome biome;
   private final MobSpawnSettings mobSettings;
   private Map<MobCategory, WeightedList<SpawnerData>> customSpawners;

   public MobSettingsWorker(Biome biome) {
      this.biome = biome;
      this.mobSettings = mobSettingsOf(biome);
   }

   public static MobSpawnSettings mobSettingsOf(Biome biome) {
      return (MobSpawnSettings)biome.getAttributes()
         .applyModifier(EnvironmentAttributes.NATURAL_MOB_SPAWNS, (MobSpawnSettings)EnvironmentAttributes.NATURAL_MOB_SPAWNS.defaultValue());
   }

   private void unfreezeSpawners() {
      if (this.customSpawners == null) {
         this.customSpawners = new HashMap<>();

         for (MobCategory category : this.mobSettings.definedCategories()) {
            this.customSpawners.put(category, this.mobSettings.getMobsInCategory(category));
         }
      }
   }

   private void freezeSpawners() {
      if (this.customSpawners != null) {
         Builder builder = new Builder();
         this.customSpawners.forEach(builder::addAllSpawns);
         builder.addAllCosts(this.mobSettings.allSpawnCosts());
         this.biome.attributes = EnvironmentAttributeMap.builder()
            .putAll(this.biome.getAttributes())
            .set(EnvironmentAttributes.NATURAL_MOB_SPAWNS, builder.build())
            .build();
         this.customSpawners = null;
      }
   }

   public boolean finished() {
      boolean res = this.customSpawners != null;
      this.freezeSpawners();
      return res;
   }

   public <M extends Mob> void addSpawns(WeightedList<SpawnerData> spawns) {
      Map<MobCategory, List<Weighted<SpawnerData>>> input = spawns.unwrap()
         .stream()
         .collect(Collectors.groupingBy(s -> ((SpawnerData)s.value()).type().getCategory()));
      if (!input.isEmpty()) {
         this.unfreezeSpawners();

         for (MobCategory category : input.keySet()) {
            WeightedList<SpawnerData> currentSpawns = this.customSpawners.get(category);
            List<Weighted<SpawnerData>> mutableSpawns;
            if (currentSpawns == null) {
               mutableSpawns = input.get(category);
            } else {
               List<Weighted<SpawnerData>> tmpA = currentSpawns.unwrap();
               List<Weighted<SpawnerData>> tmpB = input.get(category);
               mutableSpawns = new ArrayList<>(tmpA.size() + tmpB.size());
               mutableSpawns.addAll(tmpA);
               mutableSpawns.addAll(tmpB);
            }

            this.customSpawners.put(category, WeightedList.of(mutableSpawns));
         }
      }
   }
}
