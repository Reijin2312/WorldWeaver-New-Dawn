package org.betterx.wover.generator.api.biomesource.end;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceConfig;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.map.hex.HexBiomeMap;
import org.betterx.wover.generator.impl.map.square.SquareBiomeMap;
import java.util.Objects;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public class WoverEndConfig implements BiomeSourceConfig<WoverEndBiomeSource> {
   public static final int DEFAULT_CAVE_BIOMES_SIZE = 128;
   public static final int DEFAULT_CAVE_BIOMES_TOP_Y = 48;
   public static final int DEFAULT_CAVE_BIOMES_TOP_JITTER = 8;
   public static final WoverEndConfig VANILLA = new WoverEndConfig(
      WoverEndConfig.EndBiomeMapType.VANILLA, WoverEndConfig.EndBiomeGeneratorType.VANILLA, true, 4096, 128, 128, 128, 128, 128, 48, 8
   );
   public static final WoverEndConfig MINECRAFT_17;
   public static final WoverEndConfig MINECRAFT_18;
   public static final WoverEndConfig MINECRAFT_18_LARGE;
   public static final WoverEndConfig MINECRAFT_18_AMPLIFIED;
   public static final WoverEndConfig MINECRAFT_20;
   public static final WoverEndConfig MINECRAFT_20_LARGE;
   public static final WoverEndConfig MINECRAFT_20_AMPLIFIED;
   public static final WoverEndConfig DEFAULT;
   public static final Codec<WoverEndConfig> CODEC;

   private static Codec<WoverEndConfig> createCodec() {
      return RecordCodecBuilder.create(
      instance -> instance.group(
            WoverEndConfig.EndBiomeMapType.CODEC.fieldOf("map_type").orElse(DEFAULT.mapVersion).forGetter(o -> o.mapVersion),
            WoverEndConfig.EndBiomeGeneratorType.CODEC.fieldOf("generator_version").orElse(DEFAULT.generatorVersion).forGetter(o -> o.generatorVersion),
            Codec.BOOL.fieldOf("with_void_biomes").orElse(DEFAULT.withVoidBiomes).forGetter(o -> o.withVoidBiomes),
            Codec.INT.fieldOf("inner_void_radius_squared").orElse(DEFAULT.innerVoidRadiusSquared).forGetter(o -> o.innerVoidRadiusSquared),
            Codec.INT.fieldOf("center_biomes_size").orElse(DEFAULT.centerBiomesSize).forGetter(o -> o.centerBiomesSize),
            Codec.INT.fieldOf("void_biomes_size").orElse(DEFAULT.voidBiomesSize).forGetter(o -> o.voidBiomesSize),
            Codec.INT.fieldOf("land_biomes_size").orElse(DEFAULT.landBiomesSize).forGetter(o -> o.landBiomesSize),
            Codec.INT.fieldOf("barrens_biomes_size").orElse(DEFAULT.barrensBiomesSize).forGetter(o -> o.barrensBiomesSize),
            Codec.INT.optionalFieldOf("cave_biomes_size", DEFAULT.caveBiomesSize).forGetter(o -> o.caveBiomesSize),
            Codec.INT.optionalFieldOf("cave_biomes_top_y", DEFAULT.caveBiomesTopY).forGetter(o -> o.caveBiomesTopY),
            Codec.INT.optionalFieldOf("cave_biomes_top_jitter", DEFAULT.caveBiomesTopJitter).forGetter(o -> o.caveBiomesTopJitter)
         )
         .apply(instance, WoverEndConfig::new)
      );
   }
   @NotNull
   public final WoverEndConfig.EndBiomeMapType mapVersion;
   @NotNull
   public final WoverEndConfig.EndBiomeGeneratorType generatorVersion;
   public final boolean withVoidBiomes;
   public final int innerVoidRadiusSquared;
   public final int voidBiomesSize;
   public final int centerBiomesSize;
   public final int landBiomesSize;
   public final int barrensBiomesSize;
   public final int caveBiomesSize;
   public final int caveBiomesTopY;
   public final int caveBiomesTopJitter;

   public WoverEndConfig(
      @NotNull WoverEndConfig.EndBiomeMapType mapVersion,
      @NotNull WoverEndConfig.EndBiomeGeneratorType generatorVersion,
      boolean withVoidBiomes,
      int innerVoidRadiusSquared,
      int centerBiomesSize,
      int voidBiomesSize,
      int landBiomesSize,
      int barrensBiomesSize,
      int caveBiomesSize,
      int caveBiomesTopY,
      int caveBiomesTopJitter
   ) {
      this.mapVersion = mapVersion;
      this.generatorVersion = generatorVersion;
      this.withVoidBiomes = withVoidBiomes;
      this.innerVoidRadiusSquared = innerVoidRadiusSquared;
      this.barrensBiomesSize = Mth.clamp(barrensBiomesSize, 1, 8192);
      this.voidBiomesSize = Mth.clamp(voidBiomesSize, 1, 8192);
      this.centerBiomesSize = Mth.clamp(centerBiomesSize, 1, 8192);
      this.landBiomesSize = Mth.clamp(landBiomesSize, 1, 8192);
      this.caveBiomesSize = Mth.clamp(caveBiomesSize, 1, 8192);
      this.caveBiomesTopY = Mth.clamp(caveBiomesTopY, 0, 256);
      this.caveBiomesTopJitter = Mth.clamp(caveBiomesTopJitter, 0, 16);
   }

   @Override
   public String toString() {
      return "EndConfig{mapVersion="
         + this.mapVersion
         + ", generatorVersion="
         + this.generatorVersion
         + ", withVoidBiomes="
         + this.withVoidBiomes
         + ", innerVoidRadius="
         + (int)Math.sqrt(this.innerVoidRadiusSquared)
         + ", voidBiomesSize="
         + this.voidBiomesSize / 16
         + ", centerBiomesSize="
         + this.centerBiomesSize / 16
         + ", landBiomesSize="
         + this.landBiomesSize / 16
         + ", barrensBiomesSize="
         + this.barrensBiomesSize / 16
         + ", caveBiomesSize="
         + this.caveBiomesSize / 16
         + ", caveBiomesTopY="
         + this.caveBiomesTopY
         + ", caveBiomesTopJitter="
         + this.caveBiomesTopJitter
         + "}";
   }

   public boolean couldSetWithoutRepair(BiomeSourceConfig<?> input) {
      return !(input instanceof WoverEndConfig cfg)
         ? false
         : this.withVoidBiomes == cfg.withVoidBiomes && this.mapVersion == cfg.mapVersion && this.generatorVersion == cfg.generatorVersion;
   }

   public boolean sameConfig(BiomeSourceConfig<?> input) {
      return this.equals(input);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         WoverEndConfig that = (WoverEndConfig)o;
         return this.withVoidBiomes == that.withVoidBiomes
            && this.innerVoidRadiusSquared == that.innerVoidRadiusSquared
            && this.voidBiomesSize == that.voidBiomesSize
            && this.centerBiomesSize == that.centerBiomesSize
            && this.landBiomesSize == that.landBiomesSize
            && this.barrensBiomesSize == that.barrensBiomesSize
            && this.caveBiomesSize == that.caveBiomesSize
            && this.caveBiomesTopY == that.caveBiomesTopY
            && this.caveBiomesTopJitter == that.caveBiomesTopJitter
            && this.mapVersion == that.mapVersion
            && this.generatorVersion == that.generatorVersion;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.mapVersion,
         this.generatorVersion,
         this.withVoidBiomes,
         this.innerVoidRadiusSquared,
         this.voidBiomesSize,
         this.centerBiomesSize,
         this.landBiomesSize,
         this.barrensBiomesSize,
         this.caveBiomesSize,
         this.caveBiomesTopY,
         this.caveBiomesTopJitter
      );
   }

   static {
      MINECRAFT_17 = new WoverEndConfig(
         WoverEndConfig.EndBiomeMapType.SQUARE,
         WoverEndConfig.EndBiomeGeneratorType.PAULEVS,
         true,
         VANILLA.innerVoidRadiusSquared * 16 * 16,
         256,
         256,
         256,
         256,
         128,
         48,
         8
      );
      MINECRAFT_18 = new WoverEndConfig(
         WoverEndConfig.EndBiomeMapType.HEX,
         IntegrationCore.RUNS_NULLSCAPE ? WoverEndConfig.EndBiomeGeneratorType.VANILLA : WoverEndConfig.EndBiomeGeneratorType.PAULEVS,
         !IntegrationCore.RUNS_NULLSCAPE,
         MINECRAFT_17.innerVoidRadiusSquared,
         MINECRAFT_17.centerBiomesSize,
         MINECRAFT_17.voidBiomesSize,
         MINECRAFT_17.landBiomesSize,
         MINECRAFT_17.barrensBiomesSize,
         MINECRAFT_17.caveBiomesSize,
         MINECRAFT_17.caveBiomesTopY,
         MINECRAFT_17.caveBiomesTopJitter
      );
      MINECRAFT_18_LARGE = new WoverEndConfig(
         WoverEndConfig.EndBiomeMapType.HEX,
         IntegrationCore.RUNS_NULLSCAPE ? WoverEndConfig.EndBiomeGeneratorType.VANILLA : WoverEndConfig.EndBiomeGeneratorType.PAULEVS,
         !IntegrationCore.RUNS_NULLSCAPE,
         MINECRAFT_18.innerVoidRadiusSquared,
         MINECRAFT_18.centerBiomesSize,
         MINECRAFT_18.voidBiomesSize * 2,
         MINECRAFT_18.landBiomesSize * 4,
         MINECRAFT_18.barrensBiomesSize * 2,
         MINECRAFT_18.caveBiomesSize,
         MINECRAFT_18.caveBiomesTopY,
         MINECRAFT_18.caveBiomesTopJitter
      );
      MINECRAFT_18_AMPLIFIED = new WoverEndConfig(
         WoverEndConfig.EndBiomeMapType.HEX,
         WoverEndConfig.EndBiomeGeneratorType.PAULEVS,
         true,
         MINECRAFT_18.innerVoidRadiusSquared,
         MINECRAFT_18.centerBiomesSize,
         MINECRAFT_18.voidBiomesSize,
         MINECRAFT_18.landBiomesSize,
         MINECRAFT_18.barrensBiomesSize,
         MINECRAFT_18.caveBiomesSize,
         MINECRAFT_18.caveBiomesTopY,
         MINECRAFT_18.caveBiomesTopJitter
      );
      MINECRAFT_20 = new WoverEndConfig(
         WoverEndConfig.EndBiomeMapType.HEX,
         WoverEndConfig.EndBiomeGeneratorType.VANILLA,
         !IntegrationCore.RUNS_NULLSCAPE,
         MINECRAFT_17.innerVoidRadiusSquared,
         MINECRAFT_17.centerBiomesSize,
         MINECRAFT_17.voidBiomesSize,
         MINECRAFT_17.landBiomesSize,
         MINECRAFT_17.barrensBiomesSize,
         MINECRAFT_17.caveBiomesSize,
         MINECRAFT_17.caveBiomesTopY,
         MINECRAFT_17.caveBiomesTopJitter
      );
      MINECRAFT_20_LARGE = new WoverEndConfig(
         WoverEndConfig.EndBiomeMapType.HEX,
         WoverEndConfig.EndBiomeGeneratorType.VANILLA,
         !IntegrationCore.RUNS_NULLSCAPE,
         MINECRAFT_18.innerVoidRadiusSquared,
         MINECRAFT_18.centerBiomesSize,
         MINECRAFT_18.voidBiomesSize * 2,
         MINECRAFT_18.landBiomesSize * 4,
         MINECRAFT_18.barrensBiomesSize * 2,
         MINECRAFT_18.caveBiomesSize,
         MINECRAFT_18.caveBiomesTopY,
         MINECRAFT_18.caveBiomesTopJitter
      );
      MINECRAFT_20_AMPLIFIED = new WoverEndConfig(
         WoverEndConfig.EndBiomeMapType.HEX,
         WoverEndConfig.EndBiomeGeneratorType.VANILLA,
         true,
         MINECRAFT_18.innerVoidRadiusSquared,
         MINECRAFT_18.centerBiomesSize,
         MINECRAFT_18.voidBiomesSize,
         MINECRAFT_18.landBiomesSize,
         MINECRAFT_18.barrensBiomesSize,
         MINECRAFT_18.caveBiomesSize,
         MINECRAFT_18.caveBiomesTopY,
         MINECRAFT_18.caveBiomesTopJitter
      );
      DEFAULT = MINECRAFT_20;
      CODEC = createCodec();
   }

   public static enum EndBiomeGeneratorType implements StringRepresentable {
      VANILLA("vanilla"),
      PAULEVS("paulevs");

      public static final Codec<WoverEndConfig.EndBiomeGeneratorType> CODEC = StringRepresentable.fromEnum(WoverEndConfig.EndBiomeGeneratorType::values);
      public final String name;

      private EndBiomeGeneratorType(String name) {
         this.name = name;
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

   public static enum EndBiomeMapType implements StringRepresentable {
      VANILLA("vanilla", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker)),
      SQUARE("square", (seed, biomeSize, picker) -> new SquareBiomeMap(seed, biomeSize, picker)),
      HEX("hex", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker));

      public static final Codec<WoverEndConfig.EndBiomeMapType> CODEC = StringRepresentable.fromEnum(WoverEndConfig.EndBiomeMapType::values);
      public final String name;
      @NotNull
      public final MapBuilderFunction mapBuilder;

      private EndBiomeMapType(String name, @NotNull MapBuilderFunction mapBuilder) {
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

