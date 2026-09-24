package org.betterx.wover.generator.impl.map.square;

import com.google.common.collect.Maps;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.map.BiomeChunk;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.math.api.MathHelper;
import org.betterx.wover.math.api.noise.OpenSimplexNoise;
import org.betterx.wover.util.function.TriConsumer;
import java.util.Map;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class SquareBiomeMap implements BiomeMap {
   private final Map<ChunkPos, SquareBiomeChunk> maps = Maps.newConcurrentMap();
   private final OpenSimplexNoise noiseX;
   private final OpenSimplexNoise noiseZ;
   private final WorldgenRandom random;
   private final WoverBiomePicker picker;
   private final int sizeXZ;
   private final int depth;
   private final int size;
   private TriConsumer<Integer, Integer, Integer> processor;

   public SquareBiomeMap(long seed, int size, WoverBiomePicker picker) {
      this.random = new WorldgenRandom(new LegacyRandomSource(seed));
      this.noiseX = new OpenSimplexNoise(this.random.nextLong());
      this.noiseZ = new OpenSimplexNoise(this.random.nextLong());
      this.sizeXZ = size;
      this.depth = (int)Math.ceil(Math.log(size) / Math.log(2.0)) - 2;
      this.size = 1 << this.depth;
      this.picker = picker;
   }

   @Override
   public void clearCache() {
      if (this.maps.size() > 32) {
         this.maps.clear();
      }
   }

   @Override
   public WoverBiomePicker.PickableBiome getBiome(double x, double y, double z) {
      WoverBiomePicker.PickableBiome biome = this.getRawBiome(x, z);
      if (biome.getEdge() != null || biome.getParentBiome() != null && biome.getParentBiome().getEdge() != null) {
         WoverBiomePicker.PickableBiome search = biome;
         if (biome.getParentBiome() != null) {
            search = biome.getParentBiome();
         }

         int size = search.edgeSize;
         boolean edge = !search.isSame(this.getRawBiome(x + size, z));
         edge = edge || !search.isSame(this.getRawBiome(x - size, z));
         edge = edge || !search.isSame(this.getRawBiome(x, z + size));
         edge = edge || !search.isSame(this.getRawBiome(x, z - size));
         edge = edge || !search.isSame(this.getRawBiome(x - 1.0, z - 1.0));
         edge = edge || !search.isSame(this.getRawBiome(x - 1.0, z + 1.0));
         edge = edge || !search.isSame(this.getRawBiome(x + 1.0, z - 1.0));
         edge = edge || !search.isSame(this.getRawBiome(x + 1.0, z + 1.0));
         if (edge) {
            biome = search.getEdge();
         }
      }

      return biome;
   }

   @Override
   public void setChunkProcessor(TriConsumer<Integer, Integer, Integer> processor) {
      this.processor = processor;
   }

   @Override
   public BiomeChunk getChunk(int cx, int cz, boolean update) {
      ChunkPos cpos = new ChunkPos(cx, cz);
      SquareBiomeChunk chunk = this.maps.get(cpos);
      if (chunk == null) {
         synchronized (this.random) {
            this.random.setLargeFeatureWithSalt(0L, cpos.x(), cpos.z(), 0);
            chunk = new SquareBiomeChunk(this.random, this.picker);
         }

         this.maps.put(cpos, chunk);
         if (update && this.processor != null) {
            this.processor.accept(cx, cz, chunk.getSide());
         }
      }

      return chunk;
   }

   private WoverBiomePicker.PickableBiome getRawBiome(double bx, double bz) {
      double x = bx * this.size / this.sizeXZ;
      double z = bz * this.size / this.sizeXZ;
      double px = bx * 0.2;
      double pz = bz * 0.2;

      for (int i = 0; i < this.depth; i++) {
         double nx = (x + this.noiseX.eval(px, pz)) / 2.0;
         double nz = (z + this.noiseZ.eval(px, pz)) / 2.0;
         x = nx;
         z = nz;
         px = px / 2.0 + i;
         pz = pz / 2.0 + i;
      }

      int ix = MathHelper.floor(x);
      int iz = MathHelper.floor(z);
      if ((ix & 15) == 15) {
         x += iz / 2 & 1;
      }

      if ((iz & 15) == 15) {
         z += ix / 2 & 1;
      }

      ChunkPos cpos = new ChunkPos(MathHelper.floor(x / 16.0), MathHelper.floor(z / 16.0));
      SquareBiomeChunk chunk = this.maps.get(cpos);
      if (chunk == null) {
         synchronized (this.random) {
            this.random.setLargeFeatureWithSalt(0L, cpos.x(), cpos.z(), 0);
            chunk = new SquareBiomeChunk(this.random, this.picker);
         }

         this.maps.put(cpos, chunk);
      }

      return chunk.getBiome(MathHelper.floor(x), MathHelper.floor(z));
   }
}

