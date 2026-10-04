package org.betterx.wover.generator.datagen;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;
import net.minecraft.world.level.biome.Biomes;

public class VanillaBiomeDataProvider extends WoverBiomeProvider {
   public VanillaBiomeDataProvider(ModCore modCore) {
      super(modCore);
   }

   protected void bootstrap(BiomeBootstrapContext context) {
      BiomeKey<WoverBiomeBuilder.Wrapped> END_HIGHLANDS = WoverBiomeBuilder.wrappedKey(Biomes.END_HIGHLANDS);
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)END_HIGHLANDS.bootstrap(context)).genChance(0.2F)).edgeSize(4))
         .isEndHighlandBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.END_MIDLANDS).bootstrap(context)).genChance(0.05F))
         .isEndMidlandBiome(END_HIGHLANDS)
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.END_BARRENS).bootstrap(context)).genChance(0.03F))
         .isEndBarrensBiome(END_HIGHLANDS)
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.THE_END).bootstrap(context)).genChance(0.01F))
         .isEndCenterIslandBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.SMALL_END_ISLANDS).bootstrap(context)).genChance(0.01F))
         .isEndSmallIslandBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.NETHER_WASTES).bootstrap(context)).genChance(0.01F))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.SOUL_SAND_VALLEY).bootstrap(context)).genChance(0.01F))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.BASALT_DELTAS).bootstrap(context)).genChance(0.01F))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.CRIMSON_FOREST).bootstrap(context)).genChance(0.01F))
         .isNetherBiome()
         .register();
      ((WoverBiomeBuilder.Wrapped)((WoverBiomeBuilder.Wrapped)WoverBiomeBuilder.wrappedKey(Biomes.WARPED_FOREST).bootstrap(context)).genChance(0.01F))
         .isNetherBiome()
         .register();
   }
}
