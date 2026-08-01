package org.betterx.wover.generator.mixin.generator;

import org.betterx.wover.generator.impl.chunkgenerator.ConfiguredChunkGenerator;
import org.betterx.wover.generator.impl.compat.VanillaEndStructureCompat;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin implements ConfiguredChunkGenerator {
    @Shadow
    @Final
    protected BiomeSource biomeSource;

    @Unique
    private ResourceKey<WorldPreset> wover_configuredWorldPreset;

    public ResourceKey<WorldPreset> wover_getConfiguredWorldPreset() {
        return wover_configuredWorldPreset;
    }

    public void wover_setConfiguredWorldPreset(ResourceKey<WorldPreset> preset) {
        wover_configuredWorldPreset = preset;
    }

    @Redirect(
            method = "tryGenerateStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/Structure;generate(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"
            )
    )
    private StructureStart wover_expandEndStructureBiomes(
            Structure structure,
            RegistryAccess registryAccess,
            ChunkGenerator generator,
            BiomeSource source,
            RandomState randomState,
            StructureTemplateManager structureTemplateManager,
            long seed,
            ChunkPos chunkPos,
            int references,
            LevelHeightAccessor heightAccessor,
            Predicate<Holder<Biome>> predicate
    ) {
        return structure.generate(
                registryAccess,
                generator,
                source,
                randomState,
                structureTemplateManager,
                seed,
                chunkPos,
                references,
                heightAccessor,
                VanillaEndStructureCompat.expandPredicate(source, registryAccess, predicate)
        );
    }
}
