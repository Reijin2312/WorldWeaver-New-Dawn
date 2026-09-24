package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.biome.impl.modification.MobSettingsWorker;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;

public record Spawns(EntityType<?> entityType) implements BiomePredicate {
   public static final MapCodec<Spawns> CODEC = EntityType.CODEC.xmap(Spawns::new, Spawns::entityType).fieldOf("entity_type");

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      MobSpawnSettings spawns = MobSettingsWorker.mobSettingsOf(ctx.biome);

      for (MobCategory spawnGroup : MobCategory.values()) {
         for (Weighted<SpawnerData> spawnEntry : spawns.getMobsToSpawn(spawnGroup).unwrap()) {
            if (((SpawnerData)spawnEntry.value()).type().equals(this.entityType)) {
               return true;
            }
         }
      }

      return false;
   }
}
