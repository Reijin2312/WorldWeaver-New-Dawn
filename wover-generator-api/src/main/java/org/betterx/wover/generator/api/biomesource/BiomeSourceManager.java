package org.betterx.wover.generator.api.biomesource;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.generator.impl.biomesource.BiomeSourceManagerImpl;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

public class BiomeSourceManager {
   public static void register(Identifier location, MapCodec<BiomeSource> codec) {
      BiomeSourceManagerImpl.register(location, codec);
   }

   public static Set<Identifier> getExcludedBiomes(TagKey<Biome> tag) {
      return BiomeSourceManagerImpl.getExcludedBiomes(tag);
   }
}

