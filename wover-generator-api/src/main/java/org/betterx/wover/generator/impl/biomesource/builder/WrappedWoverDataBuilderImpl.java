package org.betterx.wover.generator.impl.biomesource.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class WrappedWoverDataBuilderImpl extends WoverBiomeBuilder.Wrapped {
   private float terrainHeight;
   private float genChance = 1.0F;
   private int edgeSize = 0;
   private boolean vertical;
   @Nullable
   private ResourceKey<Biome> edge;
   @Nullable
   private ResourceKey<Biome> parent;

   protected WrappedWoverDataBuilderImpl(BiomeBootstrapContext context, BiomeKey<WoverBiomeBuilder.Wrapped> key) {
      super(context, key);
      this.terrainHeight = 0.1F;
      this.vertical = false;
   }

   public void registerBiome(BootstrapContext<Biome> biomeContext) {
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

   public WoverBiomeBuilder.Wrapped edge(ResourceKey<Biome> edge) {
      this.edge = edge;
      return this;
   }

   public WoverBiomeBuilder.Wrapped parent(ResourceKey<Biome> parent) {
      this.parent = parent;
      return this;
   }

   public WoverBiomeBuilder.Wrapped parent(BiomeKey<?> parent) {
      this.parent = parent.key;
      return this;
   }

   public WoverBiomeBuilder.Wrapped terrainHeight(float height) {
      this.terrainHeight = height;
      return this;
   }

   public WoverBiomeBuilder.Wrapped genChance(float weight) {
      this.genChance = weight;
      return this;
   }

   public WoverBiomeBuilder.Wrapped edgeSize(int size) {
      this.edgeSize = size;
      return this;
   }

   public WoverBiomeBuilder.Wrapped vertical(boolean vertical) {
      this.vertical = vertical;
      return this;
   }
}

