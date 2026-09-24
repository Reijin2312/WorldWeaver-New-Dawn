package org.betterx.wover.feature.api.configured;

import org.betterx.wover.feature.api.FeatureUtils;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FeatureKey<B extends FeatureConfigurator> {
   @NotNull
   public final ResourceKey<Feature> key;

   protected FeatureKey(@NotNull Identifier id) {
      this(FeatureConfiguratorImpl.createKey(id));
   }

   protected FeatureKey(@NotNull ResourceKey<Feature> key) {
      this.key = key;
   }

   @Nullable
   public Holder<Feature> getHolder(@Nullable HolderGetter<Feature> getter) {
      return FeatureConfiguratorImpl.getHolder(getter, this.key);
   }

   @Nullable
   public Holder<Feature> getHolder(@NotNull BootstrapContext<?> context) {
      return this.getHolder(context.lookup(Registries.FEATURE));
   }

   @Nullable
   public Holder<Feature> getHolder(@NotNull RegistryAccess access) {
      return this.getHolder(access.lookupOrThrow(Registries.FEATURE));
   }

   public boolean placeInWorld(@NotNull WorldGenLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
      return this.placeInWorld(level.registryAccess(), level, pos, random, null);
   }

   public boolean placeInWorld(@NotNull WorldGenLevel level, @NotNull BlockPos pos, @NotNull RandomSource random, @Nullable ChunkGenerator generator) {
      return this.placeInWorld(level.registryAccess(), level, pos, random, generator);
   }

   public boolean placeInWorld(@Nullable RegistryAccess access, @NotNull WorldGenLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
      if (access == null) {
         return false;
      } else {
         Holder<Feature> holder = this.getHolder(access);
         return holder != null ? FeatureUtils.placeInWorld((Feature)holder.value(), level, pos, random, false) : false;
      }
   }

   public boolean placeInWorld(
      @Nullable RegistryAccess access, @NotNull WorldGenLevel level, @NotNull BlockPos pos, @NotNull RandomSource random, @Nullable ChunkGenerator generator
   ) {
      if (access == null) {
         return false;
      } else {
         Holder<Feature> holder = this.getHolder(access);
         return holder != null ? FeatureUtils.placeInWorld((Feature)holder.value(), level, pos, random, generator, false) : false;
      }
   }

   public abstract B bootstrap(@NotNull BootstrapContext<Feature> var1);
}
