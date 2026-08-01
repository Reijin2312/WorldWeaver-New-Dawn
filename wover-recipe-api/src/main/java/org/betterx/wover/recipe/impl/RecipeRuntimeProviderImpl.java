package org.betterx.wover.recipe.impl;

import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.recipe.api.OnBootstrapRecipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeRuntimeProviderImpl {
    public static final EventImpl<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            new EventImpl<>("BOOTSTRAP_RECIPES");

    public record LoadedRecipes(List<RecipeHolder<?>> recipes) {
    }

    private static volatile Contribution pendingAdvancements;

    private record Contribution(HolderLookup.Provider registries, List<AdvancementHolder> advancements) {
    }

    @ApiStatus.Internal
    public static LoadedRecipes loadedRecipes(
            LoadedRecipes loaded,
            HolderLookup.Provider registries
    ) {
        final boolean[] didInit = {false};
        final List<AdvancementHolder> advancements = new ArrayList<>();
        final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> recipesById = new LinkedHashMap<>();

        RecipeOutput context = new RecipeOutput() {
            @Override
            public void accept(
                    ResourceKey<Recipe<?>> recipeId,
                    Recipe<?> recipe,
                    @Nullable AdvancementHolder advancementHolder,
                    ICondition... conditions
            ) {
                if (!didInit[0]) {
                    loaded.recipes().forEach(existing -> recipesById.put(existing.id(), existing));
                    didInit[0] = true;
                }
                recipesById.put(recipeId, new RecipeHolder<>(recipeId, recipe));
                if (advancementHolder != null) advancements.add(advancementHolder);
            }

            @Override
            @SuppressWarnings("removal")
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder
                        .recipeAdvancement()
                        .parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void includeRootAdvancement() {
                // Runtime bootstrap merges recipes into an already loaded set; no root advancement file is written here.
            }
        };

        BOOTSTRAP_RECIPES.emit(c -> c.bootstrap(context));
        pendingAdvancements = new Contribution(registries, List.copyOf(advancements));

        if (!didInit[0]) return loaded;
        return new LoadedRecipes(List.copyOf(recipesById.values()));
    }

    @ApiStatus.Internal
    public static List<AdvancementHolder> takeContributedAdvancements(HolderLookup.Provider registries) {
        final Contribution contribution = pendingAdvancements;
        pendingAdvancements = null;
        if (contribution == null || contribution.registries() != registries) return List.of();
        return contribution.advancements();
    }
}
