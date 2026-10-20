package org.betterx.wover.generator.impl.compat;

import org.betterx.wover.biome.api.data.BiomeCodecRegistry;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.neoforged.fml.ModList;

import java.util.List;

/**
 * Native compatibility for Nether Descent's WoVer biome placement.
 *
 * <p>Based on RedCraft86's MIT-licensed Nether Descent WoVer Compat. The integration is deliberately
 * optional and does not load any Nether Descent class unless that mod is installed.</p>
 */
public final class NetherDescentCompat {
    private static final String MOD_ID = "netherdescent";
    private static final ResourceLocation CODEC_ID = id("biome");
    private static final ResourceKey<Biome> ARISIAN_UNDERGROWTH = biome("arisian_undergrowth");
    private static final ResourceKey<Biome> CRIMSON_GARDENS = biome("crimson_gardens");
    private static final ResourceKey<Biome> EMBUR_BOG = biome("embur_bog");
    private static final ResourceKey<Biome> SYTHIAN_TORRIDS = biome("sythian_torrids");
    private static final ResourceKey<Biome> WAILING_GARTH = biome("wailing_garth");
    private static boolean active;

    private NetherDescentCompat() {
    }

    public static void initialize() {
        if (!ModList.get().isLoaded(MOD_ID)) {
            return;
        }
        if (ModList.get().isLoaded("netherdescent_wover")) {
            LibWoverWorldGenerator.C.log.info(
                    "Nether Descent WoVer Compat is installed; native compatibility will stay disabled"
            );
            return;
        }

        active = true;
        if (!BiomeCodecRegistry.BIOME_CODECS.containsKey(CODEC_ID)) {
            BiomeCodecRegistry.register(CODEC_ID, NetherDescentBiomeData.KEY_CODEC);
        }
        BiomeDataRegistry.BOOTSTRAP_BIOME_DATA_REGISTRY.subscribe(NetherDescentCompat::bootstrapBiomes);
        LibWoverWorldGenerator.C.log.info("Enabled native Nether Descent biome compatibility");
    }

    public static boolean isActive() {
        return active;
    }

    private static void bootstrapBiomes(BootstrapContext<BiomeData> context) {
        register(context, ARISIAN_UNDERGROWTH, point(0.0F, 0.4F, 0.375F), false);
        register(context, CRIMSON_GARDENS, point(0.3F, 0.0F, 0.0F), false);
        register(context, EMBUR_BOG, point(0.5F, 0.0F, 0.0F), false);
        register(context, SYTHIAN_TORRIDS, point(0.0F, -0.4F, 0.0F), true);
        register(context, WAILING_GARTH, point(-0.4F, 0.0F, 0.175F), true);
    }

    private static void register(
            BootstrapContext<BiomeData> context,
            ResourceKey<Biome> biome,
            Climate.ParameterPoint parameterPoint,
            boolean vertical
    ) {
        context.register(
                BiomeDataRegistry.createKey(biome.location()),
                new NetherDescentBiomeData(
                        1.0F,
                        biome,
                        new BiomeGenerationDataContainer(List.of(parameterPoint), BiomeTags.IS_NETHER),
                        0.1F,
                        1.0F,
                        0,
                        vertical,
                        null,
                        null
                )
        );
    }

    private static Climate.ParameterPoint point(float temperature, float humidity, float offset) {
        return Climate.parameters(temperature, humidity, 0.0F, 0.0F, 0.0F, 0.0F, offset);
    }

    private static ResourceKey<Biome> biome(String path) {
        return ResourceKey.create(Registries.BIOME, id(path));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
