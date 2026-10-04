package org.betterx.wover.datagen.api.provider.multi;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.impl.BiomeBootstrapContextImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.AbstractMultiProvider;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverTagProvider.ForBiomes;
import org.betterx.wover.datagen.api.provider.WoverBiomeDataProvider;
import org.betterx.wover.datagen.api.provider.WoverBiomeOnlyProvider;
import org.betterx.wover.datagen.api.provider.WoverSurfaceRuleProvider;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import java.util.List;
import java.util.Objects;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public abstract class WoverBiomeProvider extends AbstractMultiProvider {
   private BiomeBootstrapContextImpl context;

   public WoverBiomeProvider(@NotNull ModCore modCore) {
      super(modCore);
   }

   public WoverBiomeProvider(@NotNull ModCore modCore, Identifier providerId) {
      super(modCore, providerId);
   }

   protected abstract void bootstrap(BiomeBootstrapContext var1);

   private <T> BiomeBootstrapContextImpl initContext(BootstrapContext<T> ctx) {
      synchronized (this) {
         if (this.context == null) {
            this.context = new BiomeBootstrapContextImpl();
            this.context.setLookupContext(ctx);
            this.bootstrap(this.context);
         } else {
            this.context.setLookupContext(ctx);
         }

         return this.context;
      }
   }

   private void bootstrapBiomes(BootstrapContext<Biome> ctx) {
      BiomeBootstrapContextImpl context = this.initContext(ctx);
      context.bootstrapBiome(ctx);
   }

   private void bootstrapData(BootstrapContext<BiomeData> ctx) {
      BiomeBootstrapContextImpl context = this.initContext(ctx);
      context.bootstrapBiomeData(ctx);
   }

   private void bootstrapSurface(BootstrapContext<AssignedSurfaceRule> ctx) {
      BiomeBootstrapContextImpl context = this.initContext(ctx);
      context.bootstrapSurfaceRules(ctx);
   }

   private void prepareBiomeTags(TagBootstrapContext<Biome> ctx) {
      BiomeBootstrapContextImpl context = this.initContext(null);
      context.prepareTags(ctx);
   }

   public void registerAllProviders(PackBuilder pack) {
      pack.addRegistryProvider(modCore -> new WoverBiomeOnlyProvider(modCore, this.providerId) {
         {
            Objects.requireNonNull(WoverBiomeProvider.this);
         }

         protected void bootstrap(BootstrapContext<Biome> context) {
            WoverBiomeProvider.this.bootstrapBiomes(context);
         }
      });
      pack.addRegistryProvider(modCore -> new WoverBiomeDataProvider(modCore, this.providerId) {
         {
            Objects.requireNonNull(WoverBiomeProvider.this);
         }

         @Override
         protected void bootstrap(BootstrapContext<BiomeData> context) {
            WoverBiomeProvider.this.bootstrapData(context);
         }
      });
      pack.addRegistryProvider(modCore -> new WoverSurfaceRuleProvider(modCore, this.providerId) {
         {
            Objects.requireNonNull(WoverBiomeProvider.this);
         }

         @Override
         protected void bootstrap(BootstrapContext<AssignedSurfaceRule> context) {
            WoverBiomeProvider.this.bootstrapSurface(context);
         }
      });
      pack.addProvider(modCore -> new ForBiomes(modCore, List.of(modCore.namespace, modCore.modId)) {
         {
            Objects.requireNonNull(WoverBiomeProvider.this);
         }

         public void prepareTags(TagBootstrapContext<Biome> provider) {
            WoverBiomeProvider.this.prepareBiomeTags(provider);
         }
      });
   }
}
