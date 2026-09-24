package org.betterx.wover.recipe.impl;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** NeoForge implementation of the common recipe-query API. */
@EventBusSubscriber(modid = "wover-recipe")
public final class SyncedRecipesImpl {
    private static final Set<RecipeType<?>> TYPES = ConcurrentHashMap.newKeySet();
    private static volatile net.minecraft.world.item.crafting.RecipeMap clientRecipes =
            net.minecraft.world.item.crafting.RecipeMap.EMPTY;

    private SyncedRecipesImpl() {
    }

    public static void register(RecipeSerializer<?> serializer) {
        // Kept for source compatibility with the loader-neutral API. NeoForge requests recipe types,
        // which are registered through registerType once the matching type is created.
    }

    public static void registerType(RecipeType<?> type) {
        TYPES.add(type);
    }

    @SubscribeEvent
    public static void syncRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(TYPES);
    }

    public static void setClientRecipes(net.minecraft.world.item.crafting.RecipeMap recipes) {
        clientRecipes = recipes;
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> allOfType(
            Level level,
            RecipeType<T> type
    ) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().getRecipes().stream()
                              .filter(holder -> holder.value().getType() == type)
                              .map(holder -> (RecipeHolder<T>) holder)
                              .toList();
        }
        return clientRecipes.byType(type);
    }
}
