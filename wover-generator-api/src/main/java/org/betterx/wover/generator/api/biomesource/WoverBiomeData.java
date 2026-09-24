package org.betterx.wover.generator.api.biomesource;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function12;
import com.mojang.datafixers.util.Function13;
import com.mojang.datafixers.util.Function14;
import com.mojang.datafixers.util.Function15;
import com.mojang.datafixers.util.Function16;
import com.mojang.datafixers.util.Function9;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.generator.impl.biomesource.WoverBiomeDataImpl;
import org.betterx.wover.state.api.WorldState;
import java.util.Comparator;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoverBiomeData extends BiomeData {
   public static final MapCodec<WoverBiomeData> CODEC = codec(WoverBiomeData::new);
   public final float terrainHeight;
   public final float genChance;
   public final int edgeSize;
   public final boolean vertical;
   @Nullable
   public final ResourceKey<Biome> edge;
   @Nullable
   public final ResourceKey<BiomeData> edgeData;
   @Nullable
   public final ResourceKey<Biome> parent;
   @Nullable
   public final ResourceKey<BiomeData> parentData;
   @Nullable
   private Optional<WoverBiomeData> edgeParent = null;

   public WoverBiomeData(
      float fogDensity,
      @NotNull ResourceKey<Biome> biome,
      @NotNull BiomeGenerationDataContainer generationData,
      float terrainHeight,
      float genChance,
      int edgeSize,
      boolean vertical,
      @Nullable ResourceKey<Biome> edge,
      @Nullable ResourceKey<Biome> parent
   ) {
      super(fogDensity, biome, generationData);
      this.terrainHeight = terrainHeight;
      this.genChance = genChance;
      this.edgeSize = edgeSize;
      this.vertical = vertical;
      this.edge = edge;
      this.parent = parent;
      this.edgeData = edge == null ? null : BiomeDataRegistry.createKey(edge.identifier());
      this.parentData = parent == null ? null : BiomeDataRegistry.createKey(parent.identifier());
   }

   public static WoverBiomeData of(ResourceKey<Biome> biome) {
      return new WoverBiomeData(1.0F, biome, BiomeGenerationDataContainer.EMPTY, 0.1F, 1.0F, 0, false, null, null);
   }

   public static WoverBiomeData withEdge(ResourceKey<Biome> biome, ResourceKey<Biome> edge) {
      return new WoverBiomeData(1.0F, biome, BiomeGenerationDataContainer.EMPTY, 0.1F, 1.0F, 4, false, edge, null);
   }

   public static WoverBiomeData tempWithEdge(ResourceKey<Biome> biome, ResourceKey<Biome> edge) {
      return new WoverBiomeData.InMemoryWoverBiomeData(1.0F, biome, BiomeGenerationDataContainer.EMPTY, 0.1F, 1.0F, 4, false, edge, null);
   }

   public static <T extends WoverBiomeData> MapCodec<T> codec(
      Function9<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null)
         )
      );
   }

   public static <T extends WoverBiomeData, P10> MapCodec<T> codec(
      RecordCodecBuilder<T, P10> p10,
      Function10<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         p10,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9
         )
      );
   }

   public static <T extends WoverBiomeData, P10, P11> MapCodec<T> codec(
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      Function11<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         p10,
         p11,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10
         )
      );
   }

   public static <T extends WoverBiomeData, P10, P11, P12> MapCodec<T> codec(
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      Function12<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         p10,
         p11,
         p12,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11
         )
      );
   }

   public static <T extends WoverBiomeData, P10, P11, P12, P13> MapCodec<T> codec(
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      Function13<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         p10,
         p11,
         p12,
         p13,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11, w12
         )
      );
   }

   public static <T extends WoverBiomeData, P10, P11, P12, P13, P14> MapCodec<T> codec(
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      RecordCodecBuilder<T, P14> p14,
      Function14<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         p10,
         p11,
         p12,
         p13,
         p14,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11, w12, w13
         )
      );
   }

   public static <T extends WoverBiomeData, P10, P11, P12, P13, P14, P15> MapCodec<T> codec(
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      RecordCodecBuilder<T, P14> p14,
      RecordCodecBuilder<T, P15> p15,
      Function15<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, P15, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         p10,
         p11,
         p12,
         p13,
         p14,
         p15,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11, w12, w13, w14
         )
      );
   }

   public static <T extends WoverBiomeData, P10, P11, P12, P13, P14, P15, P16> MapCodec<T> codec(
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      RecordCodecBuilder<T, P14> p14,
      RecordCodecBuilder<T, P15> p15,
      RecordCodecBuilder<T, P16> p16,
      Function16<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, P15, P16, T> factory
   ) {
      WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
      return BiomeData.codec(
         a.t0,
         a.t1,
         a.t2,
         a.t3,
         a.t4,
         a.t5,
         p10,
         p11,
         p12,
         p13,
         p14,
         p15,
         p16,
         (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15) -> factory.apply(
            w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11, w12, w13, w14, w15
         )
      );
   }

   @NotNull
   public static Registry<BiomeData> getDataRegistry(String forWhat, ResourceKey<Biome> ofBiome) throws IllegalStateException {
      RegistryAccess acc = WorldState.registryAccess();
      if (acc == null) {
         if (WorldState.allStageRegistryAccess() == null) {
            throw new IllegalStateException("Accessing " + forWhat + " of " + ofBiome + " before any registry is ready!");
         }

         if (preFinalAccessWarning++ < 5) {
            LibWoverBiome.C.log.verboseWarning("Accessing " + forWhat + " of " + ofBiome + " before registry is ready!");
         }

         acc = WorldState.allStageRegistryAccess();
      }

      return acc != null ? (Registry)acc.lookup(BiomeDataRegistry.BIOME_DATA_REGISTRY).orElse(null) : null;
   }

   public WoverBiomeData findEdgeParent() {
      if (this.edgeParent != null) {
         return this.edgeParent.orElse(null);
      } else {
         Registry<BiomeData> reg = getDataRegistry("edge parent", this.biomeKey);
         WoverBiomeData found = reg.entrySet()
            .stream()
            .map(Entry::getValue)
            .filter(data -> data instanceof WoverBiomeData b && this.isSame(b.edge))
            .map(data -> (WoverBiomeData)data)
            .min(Comparator.comparing(b -> b.biomeKey.identifier().toString()))
            .orElse(null);
         this.edgeParent = Optional.ofNullable(found);
         return found;
      }
   }

   public boolean isPickable() {
      return this.parent == null && this.findEdgeParent() == null;
   }

   public float genChance() {
      return this.genChance;
   }

   @Nullable
   public BiomeData getEdgeData() {
      if (this.edgeData == null) {
         return null;
      } else {
         Registry<BiomeData> reg = getDataRegistry("edge biome", this.biomeKey);
         return reg.get(this.edgeData).<BiomeData>map(Reference::value).orElse(null);
      }
   }

   @Nullable
   public BiomeData getParentData() {
      if (this.parentData == null) {
         return null;
      } else {
         Registry<BiomeData> reg = getDataRegistry("parent biome", this.biomeKey);
         return reg.get(this.parentData).<BiomeData>map(Reference::value).orElse(null);
      }
   }

   public MapCodec<? extends WoverBiomeData> codec() {
      return CODEC;
   }

   public static class InMemoryWoverBiomeData extends WoverBiomeData {
      private InMemoryWoverBiomeData(
         float fogDensity,
         @NotNull ResourceKey<Biome> biome,
         @NotNull BiomeGenerationDataContainer generationData,
         float terrainHeight,
         float genChance,
         int edgeSize,
         boolean vertical,
         @Nullable ResourceKey<Biome> edge,
         @Nullable ResourceKey<Biome> parent
      ) {
         super(fogDensity, biome, generationData, terrainHeight, genChance, edgeSize, vertical, edge, parent);
      }

      public boolean isTemp() {
         return true;
      }
   }
}

