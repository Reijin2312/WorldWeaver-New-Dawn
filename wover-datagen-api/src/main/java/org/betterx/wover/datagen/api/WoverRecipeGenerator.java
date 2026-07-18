package org.betterx.wover.datagen.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

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
    void buildRecipes(HolderLookup.Provider lookup, RecipeOutput exporter);
}
