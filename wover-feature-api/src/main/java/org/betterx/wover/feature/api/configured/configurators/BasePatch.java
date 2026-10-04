package org.betterx.wover.feature.api.configured.configurators;

public interface BasePatch<W extends BasePatch<W>> {
   W likeDefaultNetherVegetation();

   W likeDefaultNetherVegetation(int var1, int var2);

   default W likeDefaultBonemeal() {
      return this.tries(9).spreadXZ(3).spreadY(1);
   }

   W tries(int var1);

   W spreadXZ(int var1);

   W spreadY(int var1);
}
