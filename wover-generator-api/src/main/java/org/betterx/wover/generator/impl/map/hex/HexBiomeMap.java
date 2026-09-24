package org.betterx.wover.generator.impl.map.hex;

import com.google.common.collect.Maps;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.map.BiomeChunk;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.math.api.MathHelper;
import org.betterx.wover.math.api.noise.OpenSimplexNoise;
import org.betterx.wover.util.function.TriConsumer;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class HexBiomeMap implements BiomeMap {
   private static final float RAD_INNER = (float)Math.sqrt(3.0) * 0.5F;
   private static final float COEF = 0.25F * (float)Math.sqrt(3.0);
   private static final float COEF_HALF = COEF * 0.5F;
   private static final float SIN = (float)Math.sin(0.4);
   private static final float COS = (float)Math.cos(0.4);
   private static final float[] EDGE_CIRCLE_X = new float[8];
   private static final float[] EDGE_CIRCLE_Z = new float[8];
   private final Map<ChunkPos, HexBiomeChunk> chunks = Maps.newConcurrentMap();
   private final WoverBiomePicker picker;
   private final OpenSimplexNoise[] noises = new OpenSimplexNoise[2];
   private TriConsumer<Integer, Integer, Integer> processor;
   private final byte noiseIterations;
   private final float scale;
   private final int seed;

   public HexBiomeMap(long seed, int size, WoverBiomePicker picker) {
      this.picker = picker;
      this.scale = HexBiomeChunk.scaleMap(size);
      Random random = new Random(seed);
      this.noises[0] = new OpenSimplexNoise(random.nextInt());
      this.noises[1] = new OpenSimplexNoise(random.nextInt());
      this.noiseIterations = (byte)Math.min(Math.ceil(Math.log(this.scale) / Math.log(2.0)), 5.0);
      this.seed = random.nextInt();
   }

   @Override
   public void clearCache() {
      if (this.chunks.size() > 127) {
         this.chunks.clear();
      }
   }

   @Override
   public WoverBiomePicker.PickableBiome getBiome(double x, double y, double z) {
      WoverBiomePicker.PickableBiome biome = this.getRawBiome(x, z);
      WoverBiomePicker.PickableBiome edge = biome.edge;
      int size = biome.edgeSize;
      if (edge == null && biome.getParentBiome() != null) {
         edge = biome.getParentBiome().edge;
         size = biome.getParentBiome().edgeSize;
      }

      if (edge == null) {
         return biome;
      } else {
         for (byte i = 0; i < 8; i++) {
            if (!this.getRawBiome(x + size * EDGE_CIRCLE_X[i], z + size * EDGE_CIRCLE_Z[i]).isSame(biome)) {
               return edge;
            }
         }

         return biome;
      }
   }

   @Override
   public BiomeChunk getChunk(int cx, int cz, boolean update) {
      ChunkPos pos = new ChunkPos(cx, cz);
      HexBiomeChunk chunk = this.chunks.get(pos);
      if (chunk == null) {
         WorldgenRandom random = new WorldgenRandom(RandomSource.create(MathHelper.getSeed(this.seed, cx, cz)));
         chunk = new HexBiomeChunk(random, this.picker);
         if (update && this.processor != null) {
            this.processor.accept(cx, cz, chunk.getSide());
         }

         this.chunks.put(pos, chunk);
      }

      return chunk;
   }

   @Override
   public void setChunkProcessor(TriConsumer<Integer, Integer, Integer> processor) {
      this.processor = processor;
   }

   private WoverBiomePicker.PickableBiome getRawBiome(double x, double z) {
      double px = x / this.scale * RAD_INNER;
      double pz = z / this.scale;
      double dx = this.rotateX(px, pz);
      double dz = this.rotateZ(px, pz);
      double var22 = this.getNoise(dx, dz, (byte)0) * 0.2F;
      double var23 = this.getNoise(dz, dx, (byte)1) * 0.2F;
      px = dx + var22;
      pz = dz + var23;
      int cellZ = (int)Math.floor(pz);
      boolean offset = (cellZ & 1) == 1;
      if (offset) {
         px += 0.5;
      }

      int cellX = (int)Math.floor(px);
      float pointX = (float)(px - cellX - 0.5);
      float pointZ = (float)(pz - cellZ - 0.5);
      if (Math.abs(pointZ) < 0.3333F) {
         return this.getChunkBiome(cellX, cellZ);
      } else if (this.insideHexagon(pointZ * RAD_INNER, pointX)) {
         return this.getChunkBiome(cellX, cellZ);
      } else {
         cellX = pointX < 0.0F ? (offset ? cellX - 1 : cellX) : (offset ? cellX : cellX + 1);
         cellZ = pointZ < 0.0F ? cellZ - 1 : cellZ + 1;
         return this.getChunkBiome(cellX, cellZ);
      }
   }

   private WoverBiomePicker.PickableBiome getChunkBiome(int x, int z) {
      int cx = HexBiomeChunk.scaleCoordinate(x);
      int cz = HexBiomeChunk.scaleCoordinate(z);
      if ((z >> 2 & 1) == 0 && HexBiomeChunk.isBorder(x)) {
         x = 0;
         cx++;
      } else if ((x >> 2 & 1) == 0 && HexBiomeChunk.isBorder(z)) {
         z = 0;
         cz++;
      }

      return this.getChunk(cx, cz, true).getBiome(x, z);
   }

   private boolean insideHexagon(float x, float z) {
      double dx = Math.abs(x) / 1.1555F;
      double dy = Math.abs(z) / 1.1555F;
      return dy <= COEF && COEF * dx + 0.25 * dy <= COEF_HALF;
   }

   private double getNoise(double x, double z, byte state) {
      double result = 0.0;

      for (byte i = 1; i <= this.noiseIterations; i++) {
         OpenSimplexNoise noise = this.noises[state];
         state = (byte)(state + 1 & 1);
         result += noise.eval(x * i, z * i) / i;
      }

      return result;
   }

   private double rotateX(double x, double z) {
      return x * COS - z * SIN;
   }

   private double rotateZ(double x, double z) {
      return x * SIN + z * COS;
   }

   static {
      for (byte i = 0; i < 8; i++) {
         float angle = i / 4.0F * (float) Math.PI;
         EDGE_CIRCLE_X[i] = (float)Math.sin(angle);
         EDGE_CIRCLE_Z[i] = (float)Math.cos(angle);
      }
   }
}

