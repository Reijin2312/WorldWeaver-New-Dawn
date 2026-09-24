package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.resources.Identifier;

public interface WithTemplates extends FeatureConfigurator {
   WithTemplates add(Identifier var1);

   WithTemplates add(Identifier var1, float var2);

   WithTemplates add(Identifier var1, int var2, float var3);
}
