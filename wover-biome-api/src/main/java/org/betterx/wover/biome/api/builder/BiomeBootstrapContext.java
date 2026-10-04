package org.betterx.wover.biome.api.builder;

import org.betterx.wover.core.api.registry.LookupProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public interface BiomeBootstrapContext extends LookupProvider {
   void register(@NotNull BiomeBuilder<?> var1);

   <S> HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> var1);
}
