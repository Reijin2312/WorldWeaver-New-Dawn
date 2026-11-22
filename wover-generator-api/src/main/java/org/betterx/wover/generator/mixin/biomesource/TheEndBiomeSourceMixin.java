package org.betterx.wover.generator.mixin.biomesource;

import org.betterx.wover.generator.impl.biomesource.end.TheEndBiomesHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(TheEndBiomeSource.class)
public class TheEndBiomeSourceMixin {
    @Shadow @Final private Holder<Biome> end;
    @Shadow @Final private Holder<Biome> highlands;
    @Shadow @Final private Holder<Biome> midlands;
    @Shadow @Final private Holder<Biome> islands;
    @Shadow @Final private Holder<Biome> barrens;

    @ModifyReturnValue(method = "collectPossibleBiomes", at = @At("RETURN"))
    private Stream<Holder<Biome>> wover_collectPossibleBiomes(Stream<Holder<Biome>> original) {
        Set<Holder<Biome>> holders = new LinkedHashSet<>(original.toList());
        TheEndBiomesHelper.addAllPossibleBiomes(holders);
        return holders.stream();
    }

    @Inject(method = "getNoiseBiome", at = @At("HEAD"), cancellable = true)
    private void wover_getNoiseBiome(
            int x,
            int y,
            int z,
            Climate.Sampler sampler,
            CallbackInfoReturnable<Holder<Biome>> cir
    ) {
        int blockX = QuartPos.toBlock(x);
        int blockY = QuartPos.toBlock(y);
        int blockZ = QuartPos.toBlock(z);
        int sectionX = SectionPos.blockToSectionCoord(blockX);
        int sectionZ = SectionPos.blockToSectionCoord(blockZ);
        if ((long) sectionX * sectionX + (long) sectionZ * sectionZ <= 4096L) {
            ResourceKey<Biome> key = TheEndBiomesHelper.pickMainIslandKey(sampler, blockX, blockY, blockZ);
            cir.setReturnValue(TheEndBiomesHelper.resolveHolder(key, end));
            return;
        }

        int noiseX = (sectionX * 2 + 1) * 8;
        int noiseZ = (sectionZ * 2 + 1) * 8;
        double erosion = sampler.erosion().sampleValue(noiseX, blockY, noiseZ);

        ResourceKey<Biome> highlandsKey = TheEndBiomesHelper.pickHighlandsKey(sampler, noiseX, blockY, noiseZ);
        if (erosion > 0.25) {
            cir.setReturnValue(TheEndBiomesHelper.resolveHolder(highlandsKey, highlands));
        } else if (erosion >= -0.0625) {
            ResourceKey<Biome> midlandsKey = TheEndBiomesHelper.pickMidlandsKey(highlandsKey, sampler, noiseX, blockY, noiseZ);
            cir.setReturnValue(TheEndBiomesHelper.resolveHolder(midlandsKey, midlands));
        } else if (erosion < -0.21875) {
            ResourceKey<Biome> islandsKey = TheEndBiomesHelper.pickSmallIslandsKey(sampler, noiseX, blockY, noiseZ);
            cir.setReturnValue(TheEndBiomesHelper.resolveHolder(islandsKey, islands));
        } else {
            ResourceKey<Biome> barrensKey = TheEndBiomesHelper.pickBarrensKey(highlandsKey, sampler, noiseX, blockY, noiseZ);
            cir.setReturnValue(TheEndBiomesHelper.resolveHolder(barrensKey, barrens));
        }
    }
}
