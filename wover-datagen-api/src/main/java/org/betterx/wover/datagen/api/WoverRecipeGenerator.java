package org.betterx.wover.datagen.api;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContextAccess;

/**
 * Marker for recipe data providers so they can be grouped into a single RecipeProvider instance,
 * avoiding duplicate "Recipes" providers in the DataGenerator.
 */
public interface WoverRecipeGenerator {
    /**
     * Generate recipes.
     *
     * @param lookup   Registry lookup
     * @param exporter Recipe output
     */
    void buildRecipes(BootstrapContextAccess lookup, RecipeOutput exporter);
}
