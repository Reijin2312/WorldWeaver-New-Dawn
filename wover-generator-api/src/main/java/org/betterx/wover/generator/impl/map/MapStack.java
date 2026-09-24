package org.betterx.wover.generator.impl.map;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.map.BiomeChunk;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.math.api.noise.OpenSimplexNoise;
import org.betterx.wover.util.function.TriConsumer;
import java.util.Random;
import net.minecraft.util.Mth;

public class MapStack implements BiomeMap {
   private final OpenSimplexNoise noise;
   private final BiomeMap[] maps;
   private final double layerDistortion;
   private final int worldHeight;
   private final int minValue;
   private final int maxValue;
   private final int maxIndex;

   public MapStack(long seed, int size, WoverBiomePicker picker, int mapHeight, int worldHeight, MapBuilderFunction mapConstructor) {
      int mapCount = Mth.ceil((float)worldHeight / mapHeight);
      this.maxIndex = mapCount - 1;
      this.worldHeight = worldHeight;
      this.layerDistortion = mapHeight * 0.1;
      this.minValue = Mth.floor(mapHeight * 0.5F + 0.5F);
      this.maxValue = Mth.floor(worldHeight - mapHeight * 0.5F + 0.5F);
      this.maps = new BiomeMap[mapCount];
      Random random = new Random(seed);

      for (int i = 0; i < mapCount; i++) {
         this.maps[i] = mapConstructor.create(random.nextLong(), size, picker);
         this.maps[i].setChunkProcessor(this::onChunkCreation);
      }

      this.noise = new OpenSimplexNoise(random.nextInt());
   }

   @Override
   public void clearCache() {
      for (BiomeMap map : this.maps) {
         map.clearCache();
      }
   }

   @Override
   public void setChunkProcessor(TriConsumer<Integer, Integer, Integer> processor) {
   }

   @Override
   public BiomeChunk getChunk(int cx, int cz, boolean update) {
      return null;
   }

   @Override
   public WoverBiomePicker.PickableBiome getBiome(double x, double y, double z) {
      int mapIndex;
      if (y < this.minValue) {
         mapIndex = 0;
      } else if (y > this.maxValue) {
         mapIndex = this.maxIndex;
      } else {
         mapIndex = Mth.floor((y + this.noise.eval(x * 0.03, z * 0.03) * this.layerDistortion) / this.worldHeight * this.maxIndex + 0.5);
         mapIndex = Mth.clamp(mapIndex, 0, this.maxIndex);
      }

      return this.maps[mapIndex].getBiome(x, y, z);
   }

   private void onChunkCreation(int cx, int cz, int side) {
      WoverBiomePicker.PickableBiome[][] biomeMap = new WoverBiomePicker.PickableBiome[side][side];
      BiomeChunk[] chunks = new BiomeChunk[this.maps.length];
      boolean isNoEmpty = false;

      for (int i = 0; i < this.maps.length; i++) {
         chunks[i] = this.maps[i].getChunk(cx, cz, false);

         for (int x = 0; x < side; x++) {
            for (int z = 0; z < side; z++) {
               if (biomeMap[x][z] == null) {
                  WoverBiomePicker.PickableBiome biome = chunks[i].getBiome(x, z);
                  if (biome == null) {
                     biome = chunks[i].getBiome(x, z);
                  }

                  if (biome.isVertical) {
                     biomeMap[x][z] = biome;
                     isNoEmpty = true;
                  }
               }
            }
         }
      }

      if (isNoEmpty) {
         for (int i = 0; i < this.maps.length; i++) {
            for (int x = 0; x < side; x++) {
               for (int zx = 0; zx < side; zx++) {
                  if (biomeMap[x][zx] != null) {
                     chunks[i].setBiome(x, zx, biomeMap[x][zx]);
                  }
               }
            }
         }
      }
   }
}

