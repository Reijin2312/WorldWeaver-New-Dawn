package org.betterx.wover.feature.impl;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.entrypoint.LibWoverFeature;
import org.betterx.wover.feature.api.features.ConditionFeature;
import org.betterx.wover.feature.api.features.MarkPostProcessingFeature;
import org.betterx.wover.feature.api.features.PillarFeature;
import org.betterx.wover.feature.api.features.PlaceFacingBlockFeature;
import org.betterx.wover.feature.api.features.SequenceFeature;
import org.betterx.wover.feature.api.features.TemplateFeature;
import org.betterx.wover.feature.impl.random.RandomPatchFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FeatureManagerImpl {
   private static final ConcurrentMap<ResourceKey<MapCodec<? extends Feature>>, MapCodec<? extends Feature>> PENDING = new ConcurrentHashMap<>();

   public static final MapCodec<PlaceFacingBlockFeature> PLACE_BLOCK = register(LibWoverFeature.C.id("place_block"), PlaceFacingBlockFeature.CODEC);
   public static final MapCodec<MarkPostProcessingFeature> MARK_POSTPROCESSING = register(
      LibWoverFeature.C.id("mark_postprocessing"), MarkPostProcessingFeature.CODEC
   );
   public static final MapCodec<SequenceFeature> SEQUENCE = register(LibWoverFeature.C.id("sequence"), SequenceFeature.CODEC);
   public static final MapCodec<ConditionFeature> CONDITION = register(LibWoverFeature.C.id("condition"), ConditionFeature.CODEC);
   public static final MapCodec<PillarFeature> PILLAR = register(LibWoverFeature.C.id("pillar"), PillarFeature.CODEC);
   public static final MapCodec<TemplateFeature> TEMPLATE = register(LibWoverFeature.C.id("template"), TemplateFeature.CODEC);
   public static final MapCodec<RandomPatchFeature> RANDOM_PATCH = register(LibWoverFeature.C.id("random_patch"), RandomPatchFeature.CODEC);

   public static <F extends Feature> MapCodec<F> register(@NotNull Identifier id, @NotNull MapCodec<F> codec) {
      return register(createKey(id), codec);
   }

   public static <F extends Feature> MapCodec<F> register(@NotNull ResourceKey<MapCodec<? extends Feature>> key, @NotNull MapCodec<F> codec) {
      MapCodec<? extends Feature> previous = PENDING.putIfAbsent(key, codec);
      if (previous != null && previous != codec) {
         throw new IllegalStateException("Feature type already queued: " + key.identifier());
      }
      return codec;
   }

   public static void onRegister(RegisterEvent event) {
      if (event.getRegistryKey().equals(Registries.FEATURE_TYPE)) {
         event.register(Registries.FEATURE_TYPE, helper ->
            PENDING.forEach((key, codec) -> helper.register(key.identifier(), codec))
         );
      }
   }

   @NotNull
   public static ResourceKey<MapCodec<? extends Feature>> createKey(Identifier location) {
      return ResourceKey.create(BuiltInRegistries.FEATURE_TYPE.key(), location);
   }

   @Internal
   public static void ensureStaticInitialization() {
   }
}
