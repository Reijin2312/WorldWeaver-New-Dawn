package org.betterx.wover.generator.api.map;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.util.function.TriConsumer;

public interface BiomeMap {
   void setChunkProcessor(TriConsumer<Integer, Integer, Integer> var1);

   BiomeChunk getChunk(int var1, int var2, boolean var3);

   WoverBiomePicker.PickableBiome getBiome(double var1, double var3, double var5);

   void clearCache();
}

