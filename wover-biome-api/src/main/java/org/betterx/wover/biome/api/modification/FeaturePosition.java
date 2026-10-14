package org.betterx.wover.biome.api.modification;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum FeaturePosition implements StringRepresentable {
   APPEND("append"),
   PREPEND("prepend");

   public static final Codec<FeaturePosition> CODEC = StringRepresentable.fromEnum(FeaturePosition::values);
   private final String name;

   FeaturePosition(String name) {
      this.name = name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }
}
