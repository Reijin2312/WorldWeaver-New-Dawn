package org.betterx.wover.feature.impl.placed.modifiers;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.entrypoint.LibWoverFeature;
import org.betterx.wover.feature.api.placed.modifiers.All;
import org.betterx.wover.feature.api.placed.modifiers.Debug;
import org.betterx.wover.feature.api.placed.modifiers.EveryLayer;
import org.betterx.wover.feature.api.placed.modifiers.Extend;
import org.betterx.wover.feature.api.placed.modifiers.ExtendXYZ;
import org.betterx.wover.feature.api.placed.modifiers.FindInDirection;
import org.betterx.wover.feature.api.placed.modifiers.InBiome;
import org.betterx.wover.feature.api.placed.modifiers.Is;
import org.betterx.wover.feature.api.placed.modifiers.IsBasin;
import org.betterx.wover.feature.api.placed.modifiers.IsNextTo;
import org.betterx.wover.feature.api.placed.modifiers.Merge;
import org.betterx.wover.feature.api.placed.modifiers.NoiseFilter;
import org.betterx.wover.feature.api.placed.modifiers.Offset;
import org.betterx.wover.feature.api.placed.modifiers.OffsetProvider;
import org.betterx.wover.feature.api.placed.modifiers.Stencil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlacementModifiersImpl {
   private static final ConcurrentMap<Identifier, MapCodec<? extends PlacementModifier>> PENDING = new ConcurrentHashMap<>();

   public static final MapCodec<Stencil> STENCIL = register("stencil", Stencil.CODEC);
   public static final MapCodec<IsNextTo> IS_NEXT_TO = register("is_next_to", IsNextTo.CODEC);
   public static final MapCodec<NoiseFilter> NOISE_FILTER = register("noise_filter", NoiseFilter.CODEC);
   public static final MapCodec<Debug> DEBUG = register("debug", Debug.CODEC);
   public static final MapCodec<Merge> FOR_ALL = register("for_all", Merge.CODEC);
   public static final MapCodec<FindInDirection> SOLID_IN_DIR = register("solid_in_dir", FindInDirection.CODEC);
   public static final MapCodec<All> ALL = register("all", All.CODEC);
   public static final MapCodec<IsBasin> IS_BASIN = register("is_basin", IsBasin.CODEC);
   public static final MapCodec<Is> IS = register("is", Is.CODEC);
   public static final MapCodec<Offset> OFFSET = register("offset", Offset.CODEC);
   public static final MapCodec<OffsetProvider> OFFSET_PROVIDER = register("offset_provider", OffsetProvider.CODEC);
   public static final MapCodec<Extend> EXTEND = register("extend", Extend.CODEC);
   public static final MapCodec<InBiome> IN_BIOME = register("in_biome", InBiome.CODEC);
   public static final MapCodec<ExtendXYZ> EXTEND_XZ = register("extend_xyz", ExtendXYZ.CODEC);
   public static final MapCodec<EveryLayer> EVERY_LAYER = register("every_layer", EveryLayer.CODEC);

   private static <P extends PlacementModifier> MapCodec<P> register(String path, MapCodec<P> codec) {
      return register(LibWoverFeature.C.id(path), codec);
   }

   public static <P extends PlacementModifier> MapCodec<P> register(Identifier location, MapCodec<P> codec) {
      MapCodec<? extends PlacementModifier> previous = PENDING.putIfAbsent(location, codec);
      if (previous != null && previous != codec) {
         throw new IllegalStateException("Placement modifier type already queued: " + location);
      }
      return codec;
   }

   public static void onRegister(RegisterEvent event) {
      if (event.getRegistryKey().equals(Registries.PLACEMENT_MODIFIER_TYPE)) {
         event.register(Registries.PLACEMENT_MODIFIER_TYPE, helper -> PENDING.forEach(helper::register));
      }
   }

   @Internal
   public static void ensureStaticInitialization() {
   }
}
