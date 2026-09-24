package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WithTemplates;
import org.betterx.wover.feature.api.features.TemplateFeature;
import org.betterx.wover.feature.impl.features.FeatureTemplateImpl;
import org.betterx.wover.util.RandomizedWeightedList;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithTemplatesImpl extends FeatureConfiguratorImpl implements WithTemplates {
   private final RandomizedWeightedList<TemplateFeature.FeatureTemplate> templates = new RandomizedWeightedList();

   WithTemplatesImpl(@Nullable BootstrapContext<Feature> ctx, @Nullable ResourceKey<Feature> key) {
      super(ctx, key);
   }

   @Override
   public WithTemplates add(Identifier location) {
      return this.add(location, 0, 1.0F);
   }

   @Override
   public WithTemplates add(Identifier location, float weight) {
      return this.add(location, 0, weight);
   }

   @Override
   public WithTemplates add(Identifier location, int offsetY, float weight) {
      this.templates.add(FeatureTemplateImpl.createTemplate(location, offsetY), weight);
      return this;
   }

   @NotNull
   @Override
   protected Feature createFeature() {
      if (this.templates.isEmpty()) {
         this.throwStateError("Template Feature must have at least one template!");
      }

      return TemplateFeature.of(this.templates);
   }

   public static class Key extends FeatureKey<WithTemplates> {
      public Key(Identifier id) {
         super(id);
      }

      public WithTemplates bootstrap(@NotNull BootstrapContext<Feature> ctx) {
         return new WithTemplatesImpl(ctx, this.key);
      }
   }
}
