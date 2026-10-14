package org.betterx.wover.generator.datagen;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;
import net.minecraft.world.level.biome.Biomes;

public class VanillaBiomeDataProvider extends WoverBiomeProvider {
   private static final float DEFAULT_CHANCE = 1.0F;

   public VanillaBiomeDataProvider(ModCore modCore) {
      super(modCore);
   }

   protected void bootstrap(BiomeBootstrapContext context) {
      BiomeKey<WoverBiomeBuilder.Wrapped> END_HIGHLANDS = WoverBiomeBuilder.wrappedKey(Biomes.END_HIGHLANDS);
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)END_HIGHLANDS.bootstrap(context)).genChance(DEFAULT_CHANCE)).edgeSize(4))
         .isEndHighlandBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.END_MIDLANDS).bootstrap(context)).genChance(DEFAULT_CHANCE * 0.25F))
         .isEndMidlandBiome(END_HIGHLANDS)
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.END_BARRENS).bootstrap(context)).genChance(DEFAULT_CHANCE * 0.15F))
         .isEndBarrensBiome(END_HIGHLANDS)
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.THE_END).bootstrap(context)).genChance(DEFAULT_CHANCE))
         .isEndCenterIslandBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.SMALL_END_ISLANDS).bootstrap(context)).genChance(DEFAULT_CHANCE))
         .isEndSmallIslandBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.NETHER_WASTES).bootstrap(context)).genChance(DEFAULT_CHANCE))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.SOUL_SAND_VALLEY).bootstrap(context)).genChance(DEFAULT_CHANCE))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.BASALT_DELTAS).bootstrap(context)).genChance(DEFAULT_CHANCE))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.CRIMSON_FOREST).bootstrap(context)).genChance(DEFAULT_CHANCE))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.WARPED_FOREST).bootstrap(context)).genChance(DEFAULT_CHANCE))
         .isNetherBiome()
         .register();
   }
}
