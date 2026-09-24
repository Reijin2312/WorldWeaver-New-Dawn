package org.betterx.wover.generator.api.biomesource.nether;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceConfig;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.generator.impl.map.hex.HexBiomeMap;
import org.betterx.wover.generator.impl.map.square.SquareBiomeMap;
import java.util.Objects;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public class WoverNetherConfig implements BiomeSourceConfig<WoverNetherBiomeSource> {
   public static final WoverNetherConfig VANILLA = new WoverNetherConfig(WoverNetherConfig.NetherBiomeMapType.VANILLA, 256, 86, false);
   public static final WoverNetherConfig MINECRAFT_17 = new WoverNetherConfig(WoverNetherConfig.NetherBiomeMapType.SQUARE, 256, 86, true);
   public static final WoverNetherConfig MINECRAFT_18;
   public static final WoverNetherConfig MINECRAFT_18_LARGE;
   public static final WoverNetherConfig MINECRAFT_18_AMPLIFIED;
   public static final WoverNetherConfig DEFAULT;
   public static final Codec<WoverNetherConfig> CODEC;

   private static Codec<WoverNetherConfig> createCodec() {
      return RecordCodecBuilder.create(instance -> instance.group(
            WoverNetherConfig.NetherBiomeMapType.CODEC.fieldOf("map_type").orElse(DEFAULT.mapVersion).forGetter(o -> o.mapVersion),
            Codec.INT.fieldOf("biome_size").orElse(DEFAULT.biomeSize).forGetter(o -> o.biomeSize),
            Codec.INT.fieldOf("biome_size_vertical").orElse(DEFAULT.biomeSizeVertical).forGetter(o -> o.biomeSizeVertical),
            Codec.BOOL.fieldOf("use_vertical_biomes").orElse(DEFAULT.useVerticalBiomes).forGetter(o -> o.useVerticalBiomes)
         )
         .apply(instance, WoverNetherConfig::new)
      );
   }
   @NotNull
   public final WoverNetherConfig.NetherBiomeMapType mapVersion;
   public final int biomeSize;
   public final int biomeSizeVertical;
   public final boolean useVerticalBiomes;

   public WoverNetherConfig(@NotNull WoverNetherConfig.NetherBiomeMapType mapVersion, int biomeSize, int biomeSizeVertical, boolean useVerticalBiomes) {
      this.mapVersion = mapVersion;
      this.biomeSize = Mth.clamp(biomeSize, 1, 8192);
      this.biomeSizeVertical = Mth.clamp(biomeSizeVertical, 1, 8192);
      this.useVerticalBiomes = useVerticalBiomes;
   }

   @Override
   public String toString() {
      return "NetherConfig{mapVersion="
         + this.mapVersion
         + ", useVerticalBiomes="
         + this.useVerticalBiomes
         + ", biomeSize="
         + this.biomeSize / 16
         + ", biomeSizeVertical="
         + this.biomeSizeVertical / 16
         + "}";
   }

   public boolean couldSetWithoutRepair(BiomeSourceConfig<?> input) {
      return input instanceof WoverNetherConfig cfg ? this.mapVersion == cfg.mapVersion : false;
   }

   public boolean sameConfig(BiomeSourceConfig<?> input) {
      return this.equals(input);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof WoverNetherConfig that ? this.mapVersion == that.mapVersion : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.mapVersion);
   }

   static {
      MINECRAFT_18 = new WoverNetherConfig(
         WoverNetherConfig.NetherBiomeMapType.HEX, MINECRAFT_17.biomeSize, MINECRAFT_17.biomeSizeVertical, MINECRAFT_17.useVerticalBiomes
      );
      MINECRAFT_18_LARGE = new WoverNetherConfig(
         WoverNetherConfig.NetherBiomeMapType.HEX, MINECRAFT_18.biomeSize * 4, MINECRAFT_18.biomeSizeVertical * 2, MINECRAFT_18.useVerticalBiomes
      );
      MINECRAFT_18_AMPLIFIED = new WoverNetherConfig(WoverNetherConfig.NetherBiomeMapType.HEX, MINECRAFT_18.biomeSize, 128, true);
      DEFAULT = MINECRAFT_18;
      CODEC = createCodec();
   }

   public static enum NetherBiomeMapType implements StringRepresentable {
      VANILLA("vanilla", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker)),
      SQUARE("square", (seed, biomeSize, picker) -> new SquareBiomeMap(seed, biomeSize, picker)),
      HEX("hex", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker));

      public static final Codec<WoverNetherConfig.NetherBiomeMapType> CODEC = StringRepresentable.fromEnum(WoverNetherConfig.NetherBiomeMapType::values);
      public final String name;
      public final MapBuilderFunction mapBuilder;

      private NetherBiomeMapType(String name, MapBuilderFunction mapBuilder) {
         this.name = name;
         this.mapBuilder = mapBuilder;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}

