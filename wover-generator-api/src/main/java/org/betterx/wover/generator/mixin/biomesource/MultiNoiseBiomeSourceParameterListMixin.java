package org.betterx.wover.generator.mixin.biomesource;

import org.betterx.wover.generator.impl.biomesource.nether.NetherBiomesHelper;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(MultiNoiseBiomeSourceParameterList.class)
public class MultiNoiseBiomeSourceParameterListMixin {
    @Shadow
    @Final
    @Mutable
    private Climate.ParameterList<Holder<Biome>> parameters;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wover_addNetherBiomes(
            MultiNoiseBiomeSourceParameterList.Preset preset,
            HolderGetter<Biome> biomes,
            CallbackInfo ci
    ) {
        if (preset != MultiNoiseBiomeSourceParameterList.Preset.NETHER) return;

        List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> additions = NetherBiomesHelper.getAdditions();
        if (additions.isEmpty()) return;

        List<Pair<Climate.ParameterPoint, Holder<Biome>>> updated = new ArrayList<>(this.parameters.values());
        Set<ResourceKey<Biome>> existing = new HashSet<>();
        for (Pair<Climate.ParameterPoint, Holder<Biome>> entry : updated) {
            entry.getSecond().unwrapKey().ifPresent(existing::add);
        }

        for (Pair<Climate.ParameterPoint, ResourceKey<Biome>> entry : additions) {
            if (!existing.contains(entry.getSecond())) {
                try {
                    Holder<Biome> holder = biomes.getOrThrow(entry.getSecond());
                    updated.add(Pair.of(entry.getFirst(), holder));
                } catch (RuntimeException ignored) {
                    // Ignore missing biome entries during bootstrap.
                }
            }
        }

        this.parameters = new Climate.ParameterList<>(updated);
    }
}
