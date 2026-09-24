package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeBuilder.VanillaBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.generator.impl.biomesource.builder.WoverBiomeKeyImpl;
import org.betterx.wover.generator.impl.biomesource.builder.WrappedWoverBiomeKeyImpl;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WoverBiomeBuilder<B extends BiomeBuilder<B>> {
   B edge(ResourceKey<Biome> var1);

   B parent(ResourceKey<Biome> var1);

   B parent(BiomeKey<?> var1);

   B terrainHeight(float var1);

   B genChance(float var1);

   B edgeSize(int var1);

   B vertical(boolean var1);

   static BiomeKey<WoverBiomeBuilder.Wrapped> wrappedKey(@NotNull ResourceKey<Biome> key) {
      return new WrappedWoverBiomeKeyImpl(key.identifier());
   }

   static BiomeKey<WoverBiomeBuilder.WoverBiome> biomeKey(@NotNull Identifier location) {
      return new WoverBiomeKeyImpl(location);
   }

   public abstract static class AbstractWoverBiomeBuilder<T extends WoverBiomeBuilder.AbstractWoverBiomeBuilder<T>>
      extends VanillaBuilder<T>
      implements WoverBiomeBuilder<T> {
      protected float terrainHeight;
      protected float genChance = 1.0F;
      protected int edgeSize = 0;
      protected boolean vertical;
      @Nullable
      protected ResourceKey<Biome> edge;
      @Nullable
      protected ResourceKey<Biome> parent;

      protected AbstractWoverBiomeBuilder(BiomeBootstrapContext context, BiomeKey<T> key) {
         super(context, key);
         this.terrainHeight = 0.1F;
         this.vertical = false;
      }

      public void registerBiomeData(BootstrapContext<BiomeData> dataContext) {
         dataContext.register(
            this.key.dataKey,
            new WoverBiomeData(
               this.fogDensity,
               this.key.key,
               new BiomeGenerationDataContainer(this.parameters, this.intendedPlacement),
               this.terrainHeight,
               this.genChance,
               this.edgeSize,
               this.vertical,
               this.edge,
               this.parent
            )
         );
      }

      public T edge(ResourceKey<Biome> edge) {
         this.edge = edge;
         return (T)this;
      }

      public T parent(@Nullable ResourceKey<Biome> parent) {
         this.parent = parent;
         return (T)this;
      }

      public T parent(@Nullable BiomeKey<?> parent) {
         this.parent = parent == null ? null : parent.key;
         return (T)this;
      }

      public T terrainHeight(float height) {
         this.terrainHeight = height;
         return (T)this;
      }

      public T genChance(float weight) {
         this.genChance = weight;
         return (T)this;
      }

      public T edgeSize(int size) {
         this.edgeSize = size;
         return (T)this;
      }

      public T vertical(boolean vertical) {
         this.vertical = vertical;
         return (T)this;
      }
   }

   public abstract static class WoverBiome extends WoverBiomeBuilder.AbstractWoverBiomeBuilder<WoverBiomeBuilder.WoverBiome> {
      protected WoverBiome(BiomeBootstrapContext context, BiomeKey<WoverBiomeBuilder.WoverBiome> key) {
         super(context, key);
      }
   }

   public abstract static class Wrapped extends BiomeBuilder<WoverBiomeBuilder.Wrapped> implements WoverBiomeBuilder<WoverBiomeBuilder.Wrapped> {
      protected Wrapped(BiomeBootstrapContext context, BiomeKey<WoverBiomeBuilder.Wrapped> key) {
         super(context, key);
      }

      public WoverBiomeBuilder.Wrapped isNetherBiome() {
         return (WoverBiomeBuilder.Wrapped)this.intendedPlacement(BiomeTags.IS_NETHER);
      }

      public WoverBiomeBuilder.Wrapped isEndHighlandBiome() {
         return (WoverBiomeBuilder.Wrapped)this.intendedPlacement(CommonBiomeTags.IS_END_HIGHLAND);
      }

      public WoverBiomeBuilder.Wrapped isEndMidlandBiome(BiomeKey<?> parent) {
         this.parent(parent);
         return (WoverBiomeBuilder.Wrapped)this.intendedPlacement(CommonBiomeTags.IS_END_MIDLAND);
      }

      public WoverBiomeBuilder.Wrapped isEndCenterIslandBiome() {
         return (WoverBiomeBuilder.Wrapped)this.intendedPlacement(CommonBiomeTags.IS_END_CENTER);
      }

      public WoverBiomeBuilder.Wrapped isEndBarrensBiome(BiomeKey<?> parent) {
         this.parent(parent);
         return (WoverBiomeBuilder.Wrapped)this.intendedPlacement(CommonBiomeTags.IS_END_BARRENS);
      }

      public WoverBiomeBuilder.Wrapped isEndSmallIslandBiome() {
         return (WoverBiomeBuilder.Wrapped)this.intendedPlacement(CommonBiomeTags.IS_SMALL_END_ISLAND);
      }
   }
}

