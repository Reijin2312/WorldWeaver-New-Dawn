package org.betterx.wover.generator.impl.compat;

import org.betterx.wover.entrypoint.LibWoverWorldGenerator;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.DimensionType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Compatibility for Biolith biome sources created outside a LevelStem by WoVer. */
public final class BiolithBiomeSourceCompat {
    private static final String INTERFACE_BIOME_SOURCE =
            "com.terraformersmc.biolith.impl.biome.InterfaceBiomeSource";
    private static final String SET_DIMENSION_TYPE = "biolith$setDimensionType";
    private static final String GET_BIOME_ENTRIES = "biolith$getBiomeEntries";
    private static final String BIOME_ENTRIES_FIELD = "biolith$biomeEntries";

    private BiolithBiomeSourceCompat() {
    }

    public static boolean setDimensionType(BiomeSource source, ResourceKey<DimensionType> dimensionType) {
        try {
            Class<?> biolithBiomeSource = Class.forName(INTERFACE_BIOME_SOURCE);
            if (!biolithBiomeSource.isInstance(source)) return false;
            Method setter = biolithBiomeSource.getMethod(SET_DIMENSION_TYPE, ResourceKey.class);
            setter.invoke(source, dimensionType);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        } catch (InvocationTargetException e) {
            LibWoverWorldGenerator.C.log.warn(
                    "Biolith rejected dimension type {} for fallback biome source {}",
                    dimensionType.identifier(), source.getClass().getName(), e.getCause()
            );
        } catch (ReflectiveOperationException | RuntimeException e) {
            LibWoverWorldGenerator.C.log.warn(
                    "Unable to configure Biolith fallback biome source {} for {}",
                    source.getClass().getName(), dimensionType.identifier(), e
            );
        }
        return false;
    }

    /** Adds zero-offset copies so all Cinderscapes biomes remain competitive in dense climate maps. */
    public static boolean protectCinderscapesClimatePoints(BiomeSource source) {
        try {
            Class<?> biolithBiomeSource = Class.forName(INTERFACE_BIOME_SOURCE);
            if (!biolithBiomeSource.isInstance(source)) return false;

            source.possibleBiomes();
            Method getter = biolithBiomeSource.getMethod(GET_BIOME_ENTRIES);
            Object value = getter.invoke(source);
            if (!(value instanceof Climate.ParameterList<?> rawParameters)) return false;

            @SuppressWarnings("unchecked")
            List<Pair<Climate.ParameterPoint, Holder<Biome>>> entries = new ArrayList<>(
                    (List<Pair<Climate.ParameterPoint, Holder<Biome>>>) (List<?>) rawParameters.values()
            );
            Set<ResourceKey<Biome>> zeroOffsetBiomes = new HashSet<>();
            for (Pair<Climate.ParameterPoint, Holder<Biome>> entry : entries) {
                entry.getSecond().unwrapKey().ifPresent(key -> {
                    if ("cinderscapes".equals(key.identifier().getNamespace()) && entry.getFirst().offset() == 0L) {
                        zeroOffsetBiomes.add(key);
                    }
                });
            }

            List<Pair<Climate.ParameterPoint, Holder<Biome>>> protectionPoints = new ArrayList<>();
            for (Pair<Climate.ParameterPoint, Holder<Biome>> entry : entries) {
                ResourceKey<Biome> key = entry.getSecond().unwrapKey().orElse(null);
                if (key == null || !"cinderscapes".equals(key.identifier().getNamespace())
                        || entry.getFirst().offset() == 0L || zeroOffsetBiomes.contains(key)) continue;

                Climate.ParameterPoint point = entry.getFirst();
                protectionPoints.add(Pair.of(new Climate.ParameterPoint(
                        point.temperature(), point.humidity(), point.continentalness(), point.erosion(),
                        point.depth(), point.weirdness(), 0L
                ), entry.getSecond()));
                zeroOffsetBiomes.add(key);
                LibWoverWorldGenerator.C.log.info(
                        "Protected Biolith climate point for {} with a zero-offset copy (original offset={})",
                        key.identifier(), point.offset()
                );
            }
            if (protectionPoints.isEmpty()) return false;
            entries.addAll(protectionPoints);

            Field entriesField = findBiomeEntriesField(source.getClass());
            entriesField.setAccessible(true);
            entriesField.set(source, new Climate.ParameterList<>(entries));
            LibWoverWorldGenerator.C.log.info(
                    "Added {} zero-offset Cinderscapes climate protection point(s)", protectionPoints.size()
            );
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        } catch (InvocationTargetException e) {
            LibWoverWorldGenerator.C.log.warn(
                    "Biolith rejected Cinderscapes climate-point adaptation for {}",
                    source.getClass().getName(), e.getCause()
            );
        } catch (ReflectiveOperationException | RuntimeException e) {
            LibWoverWorldGenerator.C.log.warn(
                    "Unable to adapt Cinderscapes climate points in Biolith fallback {}",
                    source.getClass().getName(), e
            );
        }
        return false;
    }

    private static Field findBiomeEntriesField(Class<?> sourceClass) throws NoSuchFieldException {
        Class<?> current = sourceClass;
        while (current != null) {
            try {
                return current.getDeclaredField(BIOME_ENTRIES_FIELD);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(BIOME_ENTRIES_FIELD);
    }
}
