package org.betterx.wover.generator.impl.compat;

import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * WoVer biome data that delegates Nether Descent's enable/disable setting to Nether Descent itself.
 *
 * <p>Adapted from RedCraft86's MIT-licensed Nether Descent WoVer Compat.</p>
 */
final class NetherDescentBiomeData extends WoverBiomeData {
    static final MapCodec<NetherDescentBiomeData> CODEC = codec(NetherDescentBiomeData::new);
    static final KeyDispatchDataCodec<NetherDescentBiomeData> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    private static final String CONFIG_CLASS =
            "net.potionstudios.netherdescent.config.configs.WorldGenerationConfig";
    private static volatile Method getConfig;
    private static volatile Method isEnabled;
    private static volatile boolean configLookupFailed;

    NetherDescentBiomeData(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull BiomeGenerationDataContainer generationData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent
    ) {
        super(fogDensity, biome, generationData, terrainHeight, genChance, edgeSize, vertical, edge, parent);
    }

    @Override
    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
    }

    @Override
    public boolean isEnabled() {
        if (configLookupFailed) return true;

        try {
            Method get = getConfig;
            Method enabled = isEnabled;
            if (get == null || enabled == null) {
                synchronized (NetherDescentBiomeData.class) {
                    get = getConfig;
                    enabled = isEnabled;
                    if (get == null || enabled == null) {
                        Class<?> configClass = Class.forName(CONFIG_CLASS);
                        get = configClass.getMethod("get");
                        enabled = configClass.getMethod("isEnabled", ResourceKey.class);
                        getConfig = get;
                        isEnabled = enabled;
                    }
                }
            }

            return (boolean) enabled.invoke(get.invoke(null), biomeKey);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            configLookupFailed = true;
            return true;
        }
    }

    @Override
    public boolean isPickable() {
        return isEnabled() && super.isPickable();
    }
}
