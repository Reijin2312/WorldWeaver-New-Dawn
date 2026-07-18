package org.betterx.wover.generator.mixin.biomesource;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiNoiseBiomeSourceParameterList.class)
public interface MultiNoiseBiomeSourceParameterListAccessor {
    @Accessor("parameters")
    Climate.ParameterList<Holder<Biome>> wover_getParameters();

    @Accessor("parameters")
    @Mutable
    void wover_setParameters(Climate.ParameterList<Holder<Biome>> parameters);
}
