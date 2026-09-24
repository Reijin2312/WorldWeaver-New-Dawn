package org.betterx.wover.biome.api;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.BiomeManagerImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeKey<B extends BiomeBuilder<B>> {
   @NotNull
   public final ResourceKey<Biome> key;
   @NotNull
   public final ResourceKey<BiomeData> dataKey;

   @Nullable
   public Holder<Biome> getHolder(@Nullable HolderGetter<Biome> getter) {
      return getter == null ? null : (Holder)getter.get(this.key).orElse(null);
   }

   @Nullable
   public Holder<Biome> getHolder(@Nullable RegistryAccess access) {
      return access == null ? null : (Holder)access.lookupOrThrow(Registries.BIOME).get(this.key).orElse(null);
   }

   @Nullable
   public Holder<Biome> getHolder(@Nullable @NotNull Provider provider) {
      return provider == null ? null : (Holder)((RegistryLookup)provider.lookup(Registries.BIOME).orElseThrow()).get(this.key).orElse(null);
   }

   public Holder<Biome> getHolder(@NotNull BootstrapContext<?> context) {
      return (Holder<Biome>)context.lookup(Registries.BIOME).get(this.key).orElse(null);
   }

   public abstract B bootstrap(BiomeBootstrapContext var1);

   protected BiomeKey(@NotNull Identifier location) {
      this.key = BiomeManagerImpl.createKey(location);
      this.dataKey = BiomeDataRegistry.createKey(location);
   }
}
