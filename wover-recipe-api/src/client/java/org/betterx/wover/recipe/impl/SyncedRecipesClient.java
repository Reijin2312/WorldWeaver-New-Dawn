package org.betterx.wover.recipe.impl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

/** Keeps the client half of the loader-neutral synchronized-recipe query populated. */
@EventBusSubscriber(modid = "wover-recipe", value = Dist.CLIENT)
public final class SyncedRecipesClient {
    private SyncedRecipesClient() {
    }

    @SubscribeEvent
    public static void receiveRecipes(RecipesReceivedEvent event) {
        SyncedRecipesImpl.setClientRecipes(event.getRecipeMap());
    }
}
