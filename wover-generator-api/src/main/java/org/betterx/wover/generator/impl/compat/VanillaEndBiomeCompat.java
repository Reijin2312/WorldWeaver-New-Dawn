package org.betterx.wover.generator.impl.compat;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.biome.impl.modification.BiomeTagModificationWorker;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Makes vanilla End biomes first-class candidates when their biome-data JSON is unavailable. */
public final class VanillaEndBiomeCompat {
    private static volatile Map<ResourceKey<Biome>, BiomeData> importedBiomeData = Map.of();

    private VanillaEndBiomeCompat() {
    }

    public static BiomeData getImportedBiomeData(ResourceKey<Biome> biome) {
        return importedBiomeData.get(biome);
    }

    public static int importBiomes(Registry<Biome> biomes) {
        Map<ResourceKey<Biome>, BiomeData> imported = new LinkedHashMap<>();
        BiomeTagModificationWorker tagWorker = new BiomeTagModificationWorker();

        add(imported, tagWorker, biomes, Biomes.THE_END, CommonBiomeTags.IS_END_CENTER, 1.0F, 0);
        add(imported, tagWorker, biomes, Biomes.END_HIGHLANDS, CommonBiomeTags.IS_END_HIGHLAND, 1.0F, 4);
        add(imported, tagWorker, biomes, Biomes.END_MIDLANDS, CommonBiomeTags.IS_END_MIDLAND, 0.25F, 0);
        add(imported, tagWorker, biomes, Biomes.END_BARRENS, CommonBiomeTags.IS_END_BARRENS, 0.15F, 0);
        add(imported, tagWorker, biomes, Biomes.SMALL_END_ISLANDS, CommonBiomeTags.IS_SMALL_END_ISLAND, 1.0F, 0);

        tagWorker.finished();
        importedBiomeData = Map.copyOf(imported);
        return imported.size();
    }

    private static void add(
            Map<ResourceKey<Biome>, BiomeData> imported,
            BiomeTagModificationWorker tagWorker,
            Registry<Biome> biomes,
            ResourceKey<Biome> biome,
            TagKey<Biome> placement,
            float generationChance,
            int edgeSize
    ) {
        if (!biomes.containsKey(biome)) return;

        tagWorker.addBiomeToTag(placement, biomes, biome, biomes.getOrThrow(biome));
        imported.put(biome, new WoverBiomeData(
                1.0F,
                biome,
                new BiomeGenerationDataContainer(List.of(), placement),
                0.1F,
                generationChance,
                edgeSize,
                false,
                null,
                null
        ));
    }
}
