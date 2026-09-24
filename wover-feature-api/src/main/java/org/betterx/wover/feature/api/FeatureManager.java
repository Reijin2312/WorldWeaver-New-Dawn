package org.betterx.wover.feature.api;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.feature.impl.FeatureManagerImpl;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.jetbrains.annotations.NotNull;

public class FeatureManager {
   public static <F extends Feature> MapCodec<F> register(Identifier location, MapCodec<F> codec) {
      return FeatureManagerImpl.register(FeatureManagerImpl.createKey(location), codec);
   }

   public static <F extends Feature> MapCodec<F> register(ResourceKey<MapCodec<? extends Feature>> key, MapCodec<F> codec) {
      return FeatureManagerImpl.register(key, codec);
   }

   @NotNull
   public static ResourceKey<MapCodec<? extends Feature>> createKey(Identifier location) {
      return FeatureManagerImpl.createKey(location);
   }

   private FeatureManager() {
   }
}
