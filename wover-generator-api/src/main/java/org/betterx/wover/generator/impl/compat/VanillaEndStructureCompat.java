package org.betterx.wover.generator.impl.compat;

import org.betterx.wover.common.generator.impl.compat.LithostitchedBiomeSourceCompat;
import org.betterx.wover.generator.impl.biomesource.end.TheEndBiomesHelper;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;

import java.util.function.Predicate;

public final class VanillaEndStructureCompat {
    private VanillaEndStructureCompat() {
    }

    public static Predicate<Holder<Biome>> expandPredicate(
            BiomeSource source,
            RegistryAccess registryAccess,
            Predicate<Holder<Biome>> original
    ) {
        if (!(LithostitchedBiomeSourceCompat.unwrap(source) instanceof WoverEndBiomeSource)
                || registryAccess == null) {
            return original;
        }

        Registry<Biome> biomes = registryAccess.registryOrThrow(Registries.BIOME);
        Holder<Biome> center = biomes.getHolderOrThrow(Biomes.THE_END);
        Holder<Biome> highlands = biomes.getHolderOrThrow(Biomes.END_HIGHLANDS);
        Holder<Biome> midlands = biomes.getHolderOrThrow(Biomes.END_MIDLANDS);
        Holder<Biome> barrens = biomes.getHolderOrThrow(Biomes.END_BARRENS);
        Holder<Biome> smallIslands = biomes.getHolderOrThrow(Biomes.SMALL_END_ISLANDS);

        boolean allowCenter = original.test(center);
        boolean allowHighlands = original.test(highlands);
        boolean allowMidlands = original.test(midlands);
        boolean allowBarrens = original.test(barrens);
        boolean allowSmallIslands = original.test(smallIslands);

        return biome -> {
            if (original.test(biome)) return true;

            ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
            return (allowCenter && isCenter(biome, key))
                    || (allowHighlands && isHighlands(biome, key))
                    || (allowMidlands && isMidlands(biome, key))
                    || (allowBarrens && isBarrens(biome, key))
                    || (allowSmallIslands && isSmallIsland(biome, key));
        };
    }

    private static boolean isCenter(Holder<Biome> biome, ResourceKey<Biome> key) {
        return biome.is(CommonBiomeTags.IS_END_CENTER)
                || key != null && TheEndBiomesHelper.canGenerateAsMainIslandBiome(key);
    }

    private static boolean isHighlands(Holder<Biome> biome, ResourceKey<Biome> key) {
        return biome.is(CommonBiomeTags.IS_END_HIGHLAND)
                || key != null && TheEndBiomesHelper.canGenerateAsHighlandsBiome(key);
    }

    private static boolean isMidlands(Holder<Biome> biome, ResourceKey<Biome> key) {
        return biome.is(CommonBiomeTags.IS_END_MIDLAND)
                || key != null && TheEndBiomesHelper.canGenerateAsEndMidlands(key);
    }

    private static boolean isBarrens(Holder<Biome> biome, ResourceKey<Biome> key) {
        return biome.is(CommonBiomeTags.IS_END_BARRENS)
                || key != null && TheEndBiomesHelper.canGenerateAsEndBarrens(key);
    }

    private static boolean isSmallIsland(Holder<Biome> biome, ResourceKey<Biome> key) {
        return biome.is(CommonBiomeTags.IS_SMALL_END_ISLAND)
                || key != null && TheEndBiomesHelper.canGenerateAsSmallIslandsBiome(key);
    }
}
