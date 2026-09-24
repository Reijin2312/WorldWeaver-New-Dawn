package org.betterx.wover.biome.impl.modification.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.config.api.Configs;
import org.betterx.wover.entrypoint.LibWoverCore;
import de.ambertation.wunderlib.configs.AbstractConfig;
import net.minecraft.resources.Identifier;

public record ConfigIs(Identifier configFile, String path, String key, String testValue) implements BiomePredicate {
   public static final MapCodec<ConfigIs> DIRECT_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Identifier.CODEC.fieldOf("config_file").forGetter(ConfigIs::configFile),
            Codec.STRING.fieldOf("path").forGetter(ConfigIs::path),
            Codec.STRING.fieldOf("key").forGetter(ConfigIs::key),
            Codec.STRING.fieldOf("value").forGetter(ConfigIs::testValue)
         )
         .apply(instance, ConfigIs::new)
   );
   public static final MapCodec<ConfigIs> CODEC = DIRECT_CODEC;

   @Override
   public MapCodec<? extends BiomePredicate> codec() {
      return CODEC;
   }

   public static <T, R extends AbstractConfig<?>.Value<T, R>> ConfigIs of(AbstractConfig<?>.Value<T, R> value, T targetValue) {
      if (value.getParentFile() == null) {
         throw new IllegalArgumentException("Value " + value + " must have a parent file.");
      } else {
         return new ConfigIs(value.getParentFile().location, value.token.path(), value.token.key(), String.valueOf(targetValue));
      }
   }

   @Override
   public boolean test(BiomePredicate.Context ctx) {
      AbstractConfig<?> config = Configs.get(this.configFile);
      if (config == null) {
         LibWoverCore.C.log.verboseWarning("Config file %s not found", new Object[]{this.configFile});
         return false;
      } else {
         AbstractConfig<?>.Value<?, ? extends AbstractConfig<?>.Value<?, ?>> value = config.getValue(this.path, this.key);
         if (value == null) {
            LibWoverCore.C.log.verboseWarning("Config value %s.%s not found in %s", new Object[]{this.path, this.key, this.configFile});
            return false;
         } else {
            return value.valueEquals(this.testValue);
         }
      }
   }
}
