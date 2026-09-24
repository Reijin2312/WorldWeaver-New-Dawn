package org.betterx.wover.generator.mixin.biomesource;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithNoiseRelatedSettings;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithSeed;
import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public abstract class ServerLevelMixin extends Level {
   protected ServerLevelMixin(
      MinecraftServer minecraftServer,
      Executor executor,
      LevelStorageAccess levelStorageAccess,
      ServerLevelData serverLevelData,
      ResourceKey<Level> resourceKey,
      LevelStem levelStem,
      boolean bl,
      long l,
      List<CustomSpawner> list,
      boolean bl2
   ) {
      super(serverLevelData, resourceKey, minecraftServer.registryAccess(), levelStem.type(), false, bl, l, minecraftServer.getMaxChainedNeighborUpdates());
   }

   @Inject(
      method = {"<init>*"},
      at = {@At("TAIL")}
   )
   private void wover_onServerWorldInit(
      MinecraftServer minecraftServer,
      Executor executor,
      LevelStorageAccess levelStorageAccess,
      ServerLevelData serverLevelData,
      ResourceKey resourceKey,
      LevelStem levelStem,
      boolean bl,
      long l,
      List list,
      boolean bl2,
      CallbackInfo ci
   ) {
      ServerLevel level = ServerLevel.class.cast(this);
      if (levelStem.generator().getBiomeSource() instanceof BiomeSourceWithSeed source) {
         source.setSeed(level.getSeed());
      }

      if (levelStem.generator().getBiomeSource() instanceof BiomeSourceWithNoiseRelatedSettings bcl
         && levelStem.generator() instanceof NoiseBasedChunkGenerator noiseGenerator) {
         bcl.onLoadGeneratorSettings((NoiseGeneratorSettings)noiseGenerator.generatorSettings().value());
      }
   }
}

