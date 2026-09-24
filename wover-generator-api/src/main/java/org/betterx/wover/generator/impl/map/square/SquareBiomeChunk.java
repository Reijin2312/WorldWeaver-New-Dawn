package org.betterx.wover.generator.impl.map.square;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.map.BiomeChunk;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class SquareBiomeChunk implements BiomeChunk {
   private static final int BIT_OFFSET = 4;
   protected static final int WIDTH = 16;
   private static final int SM_WIDTH = 8;
   private static final int SM_BIT_OFFSET = 2;
   private static final int MASK_OFFSET = 7;
   protected static final int MASK_WIDTH = 15;
   private static final int SM_CAPACITY = 64;
   private static final int CAPACITY = 256;
   private final WoverBiomePicker.PickableBiome[] biomes;

   public SquareBiomeChunk(WorldgenRandom random, WoverBiomePicker picker) {
      WoverBiomePicker.PickableBiome[] PreBio = new WoverBiomePicker.PickableBiome[64];
      this.biomes = new WoverBiomePicker.PickableBiome[256];

      for (int x = 0; x < 8; x++) {
         int offset = x << 2;

         for (int z = 0; z < 8; z++) {
            PreBio[offset | z] = picker.getBiome(random);
         }
      }

      for (int x = 0; x < 16; x++) {
         int offset = x << 4;

         for (int z = 0; z < 16; z++) {
            this.biomes[offset | z] = PreBio[this.getSmIndex(this.offsetXZ(x, random), this.offsetXZ(z, random))].getSubBiome(random);
         }
      }
   }

   @Override
   public WoverBiomePicker.PickableBiome getBiome(int x, int z) {
      return this.biomes[this.getIndex(x & 15, z & 15)];
   }

   @Override
   public void setBiome(int x, int z, WoverBiomePicker.PickableBiome biome) {
      this.biomes[this.getIndex(x & 15, z & 15)] = biome;
   }

   @Override
   public int getSide() {
      return 16;
   }

   private int offsetXZ(int x, WorldgenRandom random) {
      return x + random.nextInt(2) >> 1 & 7;
   }

   private int getIndex(int x, int z) {
      return x << 4 | z;
   }

   private int getSmIndex(int x, int z) {
      return x << 2 | z;
   }
}

