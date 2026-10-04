package org.betterx.wover.recipe.impl;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.fabricmc.fabric.api.recipe.v1.FabricRecipeAccess;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;

import java.util.Collection;

/** Fabric implementation of the common recipe-query API. */
public final class SyncedRecipesImpl {
    private SyncedRecipesImpl() {
    }

    public static void register(RecipeSerializer<?> serializer) {
        RecipeSynchronization.synchronizeRecipeSerializer(serializer);
    }

    public static void registerType(RecipeType<?> type) {
        // Fabric negotiates synchronized recipes by serializer rather than by recipe type.
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> allOfType(
            Level level,
            RecipeType<T> type
    ) {
        return ((FabricRecipeAccess) level.recipeAccess()).getSynchronizedRecipes().getAllOfType(type);
    }
}
