package org.betterx.wover.potions.api;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

public interface BrewingBuilder {
    void addContainer(Item container);

    void addContainerRecipe(Item container, Item reagent, Item result);

    void addMix(Holder<Potion> from, Item reagent, Holder<Potion> to);

    void addStartMix(Item reagent, Holder<Potion> potion);
}
