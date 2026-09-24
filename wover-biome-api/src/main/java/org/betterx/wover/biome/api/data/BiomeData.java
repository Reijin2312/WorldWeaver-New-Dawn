package org.betterx.wover.biome.api.data;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function12;
import com.mojang.datafixers.util.Function13;
import com.mojang.datafixers.util.Function14;
import com.mojang.datafixers.util.Function15;
import com.mojang.datafixers.util.Function16;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.biome.impl.data.BiomeDataImpl;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.state.api.WorldState;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BiomeData {
   public static final MapCodec<BiomeData> CODEC = codec(BiomeData::new);
   @NotNull
   public final ResourceKey<Biome> biomeKey;
   public final float fogDensity;
   @NotNull
   public final BiomeGenerationDataContainer generationData;
   protected static int preFinalAccessWarning = 0;

   public BiomeData(float fogDensity, @NotNull ResourceKey<Biome> biome, @NotNull BiomeGenerationDataContainer generationData) {
      this.fogDensity = fogDensity;
      this.biomeKey = biome;
      this.generationData = generationData;
   }

   @NotNull
   public static BiomeData of(ResourceKey<Biome> biome) {
      return new BiomeData(1.0F, biome, BiomeGenerationDataContainer.EMPTY);
   }

   @NotNull
   public static BiomeData tempOf(ResourceKey<Biome> biome) {
      return new BiomeDataImpl.InMemoryBiomeData(1.0F, biome, BiomeGenerationDataContainer.EMPTY);
   }

   public static <T extends BiomeData> MapCodec<T> codec(Function3<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, T> factory) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2).apply(instance, factory));
   }

   public static <T extends BiomeData, P4> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4, Function4<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4, RecordCodecBuilder<T, P5> p5, Function5<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      Function6<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      Function7<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      Function8<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      Function9<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      RecordCodecBuilder<T, P10> p10,
      Function10<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      Function11<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      Function12<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      Function13<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      RecordCodecBuilder<T, P14> p14,
      Function14<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14).apply(instance, factory));
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      RecordCodecBuilder<T, P14> p14,
      RecordCodecBuilder<T, P15> p15,
      Function15<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(
         instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15).apply(instance, factory)
      );
   }

   public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> MapCodec<T> codec(
      RecordCodecBuilder<T, P4> p4,
      RecordCodecBuilder<T, P5> p5,
      RecordCodecBuilder<T, P6> p6,
      RecordCodecBuilder<T, P7> p7,
      RecordCodecBuilder<T, P8> p8,
      RecordCodecBuilder<T, P9> p9,
      RecordCodecBuilder<T, P10> p10,
      RecordCodecBuilder<T, P11> p11,
      RecordCodecBuilder<T, P12> p12,
      RecordCodecBuilder<T, P13> p13,
      RecordCodecBuilder<T, P14> p14,
      RecordCodecBuilder<T, P15> p15,
      RecordCodecBuilder<T, P16> p16,
      Function16<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, T> factory
   ) {
      BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
      return RecordCodecBuilder.mapCodec(
         instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16).apply(instance, factory)
      );
   }

   public MapCodec<? extends BiomeData> codec() {
      return CODEC;
   }

   @Nullable
   public Holder<Biome> biomeHolder() {
      if (WorldState.registryAccess() == null) {
         if (WorldState.allStageRegistryAccess() == null) {
            return null;
         } else {
            if (preFinalAccessWarning++ < 5) {
               LibWoverBiome.C.log.verboseWarning("Accessing biome holder for " + this.biomeKey + " before registry is ready!");
            }

            return (Holder<Biome>)WorldState.allStageRegistryAccess().lookupOrThrow(Registries.BIOME).get(this.biomeKey).orElse(null);
         }
      } else {
         return (Holder<Biome>)WorldState.registryAccess().lookupOrThrow(Registries.BIOME).get(this.biomeKey).orElse(null);
      }
   }

   @Nullable
   public Biome biome() {
      if (WorldState.registryAccess() == null) {
         if (WorldState.allStageRegistryAccess() == null) {
            return null;
         } else {
            if (preFinalAccessWarning++ < 5) {
               LibWoverBiome.C.log.verboseWarning("Accessing biome for " + this.biomeKey + " before registry is ready!");
            }

            return (Biome)WorldState.allStageRegistryAccess().lookupOrThrow(Registries.BIOME).getOptional(this.biomeKey).orElse(null);
         }
      } else {
         return (Biome)WorldState.registryAccess().lookupOrThrow(Registries.BIOME).getOptional(this.biomeKey).orElse(null);
      }
   }

   public boolean isPickable() {
      return true;
   }

   public float genChance() {
      return 1.0F;
   }

   @Internal
   public boolean isTemp() {
      return false;
   }

   public boolean isSame(ResourceKey<Biome> biome) {
      return isSame(this.biomeKey, biome);
   }

   public static boolean isSame(ResourceKey<Biome> biomeA, ResourceKey<Biome> biomeB) {
      if (biomeA == null && biomeB == null) {
         return true;
      } else {
         return biomeA != null && biomeB != null ? biomeA.identifier().equals(biomeB.identifier()) : false;
      }
   }

   public boolean isSame(BiomeData biome) {
      return biome == null ? false : this.isSame(biome.biomeKey);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof BiomeData biomeData ? Objects.equals(this.biomeKey, biomeData.biomeKey) : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.biomeKey);
   }

   public boolean isIntendedFor(@Nullable TagKey<Biome> tag) {
      return this.generationData.intendedPlacement() == null ? tag == null : this.generationData.intendedPlacement().equals(tag);
   }
}
