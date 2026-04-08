package org.betterx.wover.block.api.model;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface BlockModelProvider {
    @OnlyIn(Dist.CLIENT)
    void provideBlockModels(Object generator);
}
