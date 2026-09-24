package org.betterx.wover.recipe.impl;

import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.potions.impl.PotionManagerImpl;
import org.betterx.wover.recipe.api.OnBootstrapRecipes;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeRuntimeProviderImpl {
    public static final EventImpl<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            new EventImpl<>("BOOTSTRAP_RECIPES");

    private static volatile Contribution pendingAdvancements;

    private record Contribution(HolderLookup.Provider registries, List<AdvancementHolder> advancements) {
    }

    @ApiStatus.Internal
    public static HolderLookup<Recipe<?>> loadedRecipes(
            HolderLookup<Recipe<?>> loaded,
            HolderLookup.Provider registries
    ) {
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
                recipesById.put(recipeId, new RecipeHolder<>(recipeId, recipe));
                if (advancementHolder != null) advancements.add(advancementHolder);
            }

            @Override
            @SuppressWarnings("removal")
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder
                        .recipeAdvancement()
                        .parent(net.minecraft.data.recipes.RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
                return registries.lookupOrThrow(key);
            }

            @Override
            public <S> Stream<net.minecraft.core.Holder.Reference<S>> listContextElements(
                    ResourceKey<? extends Registry<? extends S>> key
            ) {
                return registries.lookupOrThrow(key).listElements();
            }
        };

        BOOTSTRAP_RECIPES.emit(c -> c.bootstrap(new RecipeBuilder.Context(registries, context)));
        PotionManagerImpl.bootstrapBrewingRecipes(context);
        pendingAdvancements = new Contribution(registries, List.copyOf(advancements));
        return RecipeLookups.extended(loaded, recipesById);
    }

    @ApiStatus.Internal
    public static List<AdvancementHolder> takeContributedAdvancements(HolderLookup.Provider registries) {
        final Contribution contribution = pendingAdvancements;
        pendingAdvancements = null;
        if (contribution == null || contribution.registries() != registries) return List.of();
        return contribution.advancements();
    }
}
