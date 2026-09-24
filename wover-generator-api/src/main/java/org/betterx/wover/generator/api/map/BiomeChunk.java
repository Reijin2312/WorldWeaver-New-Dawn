package org.betterx.wover.generator.api.map;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;

public interface BiomeChunk {
   void setBiome(int var1, int var2, WoverBiomePicker.PickableBiome var3);

   WoverBiomePicker.PickableBiome getBiome(int var1, int var2);

   int getSide();
}

