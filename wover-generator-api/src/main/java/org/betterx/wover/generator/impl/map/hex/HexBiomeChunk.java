package org.betterx.wover.generator.impl.map.hex;

import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.map.BiomeChunk;
import java.util.Arrays;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class HexBiomeChunk implements BiomeChunk {
   private static final short SIDE = 32;
   private static final byte SIDE_PRE = 4;
   private static final short SIZE = 1024;
   private static final short MAX_SIDE = 992;
   private static final byte SCALE_PRE = 8;
   private static final byte SIZE_PRE = 16;
   private static final byte SIDE_MASK = 31;
   private static final byte SIDE_PRE_MASK = 3;
   private static final byte SIDE_OFFSET = (byte)Math.round(Math.log(32.0) / Math.log(2.0));
   private static final byte SIDE_PRE_OFFSET = (byte)Math.round(Math.log(4.0) / Math.log(2.0));
   private static final short[][] NEIGHBOURS = new short[2][6];
   private final WoverBiomePicker.PickableBiome[] biomes = new WoverBiomePicker.PickableBiome[1024];

   public HexBiomeChunk(WorldgenRandom random, WoverBiomePicker picker) {
      WoverBiomePicker.PickableBiome[][] buffers = new WoverBiomePicker.PickableBiome[2][1024];

      for (WoverBiomePicker.PickableBiome[] buffer : buffers) {
         Arrays.fill(buffer, null);
      }

      for (byte index = 0; index < 16; index++) {
         byte px = (byte)(index >> SIDE_PRE_OFFSET);
         byte pz = (byte)(index & 3);
         px = (byte)(px * 8 + random.nextInt(8));
         pz = (byte)(pz * 8 + random.nextInt(8));
         this.circle(buffers[0], this.getIndex(px, pz), picker.getBiome(random), null);
      }

      boolean hasEmptyCells = true;
      byte bufferIndex = 0;

      while (hasEmptyCells) {
         WoverBiomePicker.PickableBiome[] inBuffer = buffers[bufferIndex];
         bufferIndex = (byte)(bufferIndex + 1 & 1);
         WoverBiomePicker.PickableBiome[] outBuffer = buffers[bufferIndex];
         hasEmptyCells = false;

         for (short index = 32; index < 992; index++) {
            byte z = (byte)(index & 31);
            if (z != 0 && z != 31) {
               if (inBuffer[index] != null) {
                  outBuffer[index] = inBuffer[index];
                  short[] neighbours = this.getNeighbours(index & 31);
                  short indexSide = (short)(index + neighbours[random.nextInt(6)]);
                  if (indexSide >= 0 && indexSide < 1024 && outBuffer[indexSide] == null) {
                     outBuffer[indexSide] = inBuffer[index];
                  }
               } else {
                  hasEmptyCells = true;
               }
            }
         }
      }

      WoverBiomePicker.PickableBiome[] outBuffer = buffers[bufferIndex];
      byte preN = 29;

      for (byte indexx = 0; indexx < 32; indexx++) {
         outBuffer[this.getIndex(indexx, (byte)0)] = outBuffer[this.getIndex(indexx, (byte)2)];
         outBuffer[this.getIndex((byte)0, indexx)] = outBuffer[this.getIndex((byte)2, indexx)];
         outBuffer[this.getIndex(indexx, (byte)31)] = outBuffer[this.getIndex(indexx, preN)];
         outBuffer[this.getIndex((byte)31, indexx)] = outBuffer[this.getIndex(preN, indexx)];
      }

      int lastAction = -1;
      WoverBiomePicker.PickableBiome lBiome = null;

      for (short indexx = 0; indexx < 1024; indexx++) {
         if (outBuffer[indexx] == null) {
            lastAction = 0;
            lBiome = null;
            outBuffer[indexx] = picker.getBiome(random);
         } else if (random.nextInt(4) == 0) {
            lastAction = 1;
            lBiome = outBuffer[indexx];
            this.circle(outBuffer, indexx, outBuffer[indexx].getSubBiome(random), outBuffer[indexx]);
         }

         if (outBuffer[indexx] == null) {
            LibWoverWorldGenerator.C.log.error("Invalid Biome at " + indexx + ", " + lastAction + ", " + lBiome);
         }
      }

      System.arraycopy(outBuffer, 0, this.biomes, 0, 1024);
   }

   private void circle(WoverBiomePicker.PickableBiome[] buffer, short center, WoverBiomePicker.PickableBiome biome, WoverBiomePicker.PickableBiome mask) {
      if (buffer[center] == mask) {
         buffer[center] = biome;
      }

      short[] neighbours = this.getNeighbours(center & 31);

      for (short i : neighbours) {
         short index = (short)(center + i);
         if (index >= 0 && index < 1024 && buffer[index] == mask) {
            buffer[index] = biome;
         }
      }
   }

   private static byte wrap(int value) {
      return (byte)(value & 31);
   }

   private short getIndex(byte x, byte z) {
      return (short)((short)x << SIDE_OFFSET | z);
   }

   @Override
   public WoverBiomePicker.PickableBiome getBiome(int x, int z) {
      return this.biomes[this.getIndex(wrap(x), wrap(z))];
   }

   @Override
   public void setBiome(int x, int z, WoverBiomePicker.PickableBiome biome) {
      this.biomes[this.getIndex(wrap(x), wrap(z))] = biome;
   }

   @Override
   public int getSide() {
      return 32;
   }

   public static int scaleCoordinate(int value) {
      return value >> SIDE_OFFSET;
   }

   public static boolean isBorder(int value) {
      return wrap(value) == 31;
   }

   private short[] getNeighbours(int z) {
      return NEIGHBOURS[z & 1];
   }

   public static float scaleMap(float size) {
      return size / 8.0F;
   }

   static {
      NEIGHBOURS[0][0] = 1;
      NEIGHBOURS[0][1] = -1;
      NEIGHBOURS[0][2] = 32;
      NEIGHBOURS[0][3] = -32;
      NEIGHBOURS[0][4] = 33;
      NEIGHBOURS[0][5] = 31;
      NEIGHBOURS[1][0] = 1;
      NEIGHBOURS[1][1] = -1;
      NEIGHBOURS[1][2] = 32;
      NEIGHBOURS[1][3] = -32;
      NEIGHBOURS[1][4] = -31;
      NEIGHBOURS[1][5] = -33;
   }
}

