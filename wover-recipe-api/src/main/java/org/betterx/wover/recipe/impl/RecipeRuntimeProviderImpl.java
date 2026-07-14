package org.betterx.wover.recipe.impl;

import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.recipe.api.OnBootstrapRecipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeRuntimeProviderImpl {
    public static final EventImpl<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            new EventImpl<>("BOOTSTRAP_RECIPES");

    public record LoadedRecipes(Multimap<RecipeType<?>, RecipeHolder<?>> byType,
                                Map<ResourceLocation, RecipeHolder<?>> byName) {
    }


    @ApiStatus.Internal
    public static LoadedRecipes loadedRecipes(
            LoadedRecipes loaded
    ) {
        final boolean[] didInit = {false};
        final ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> typeBuilder = ImmutableMultimap
                .<RecipeType<?>, RecipeHolder<?>>builder();

        final ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> nameBuilder = ImmutableMap
                .<ResourceLocation, RecipeHolder<?>>builder();

        RecipeOutput context = new RecipeOutput() {
            @Override
            public void accept(
                    ResourceLocation resourceLocation,
                    Recipe<?> recipe,
                    @Nullable AdvancementHolder advancementHolder
            ) {
                if (!didInit[0]) {
                    typeBuilder.putAll(loaded.byType);
                    nameBuilder.putAll(loaded.byName);
                    didInit[0] = true;
                }
                RecipeHolder<?> recipeHolder = new RecipeHolder<>(resourceLocation, recipe);
                typeBuilder.put(recipe.getType(), recipeHolder);
                nameBuilder.put(resourceLocation, recipeHolder);
            }

            @Override
            @SuppressWarnings("removal")
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder
                        .recipeAdvancement()
                        .parent(net.minecraft.data.recipes.RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }
        };

        BOOTSTRAP_RECIPES.emit(c -> c.bootstrap(context));

        if (!didInit[0]) return loaded;
        return new LoadedRecipes(typeBuilder.build(), nameBuilder.build());
    }
}
