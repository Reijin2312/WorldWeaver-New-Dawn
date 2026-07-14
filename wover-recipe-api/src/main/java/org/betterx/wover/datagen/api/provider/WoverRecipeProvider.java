package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;

import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public abstract class WoverRecipeProvider implements WoverDataProvider<RecipeProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    public WoverRecipeProvider(
            ModCore modCore,
            String title
    ) {
        this.title = title;
        this.modCore = modCore;
    }


    /**
     * Called, when the Recipes need to be created and added
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(HolderLookup.Provider provider, RecipeOutput context);

    @Override
    public RecipeProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new InnerRecipeProvider(output, registriesFuture) {
            @Override
            public void buildRecipes(HolderLookup.Provider lookup, RecipeOutput exporter) {
                bootstrap(lookup, exporter);
            }

            @Override
            protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
                return identifier;
            }

            @Override
            public String getName() {
                return title;
            }
        };
    }

    // Based on FabricRecipeProvider, we need a registry access to get enchantments, and fabric does not provide it.
    private static abstract class InnerRecipeProvider extends RecipeProvider {
        protected final FabricDataOutput output;

        public InnerRecipeProvider(
                FabricDataOutput output,
                CompletableFuture<HolderLookup.Provider> registriesFuture
        ) {
            super(output, registriesFuture);
            this.output = output;
        }

        /**
         * Implement this method and then use the range of methods in {@link RecipeProvider} or from one of the recipe json factories such as {@link ShapedRecipeBuilder} or {@link ShapelessRecipeBuilder}.
         */
        @Override
        public final void buildRecipes(RecipeOutput exporter) {
            //NO-OP
        }

        public abstract void buildRecipes(HolderLookup.Provider lookup, RecipeOutput exporter);

        @Override
        public CompletableFuture<?> run(CachedOutput writer, HolderLookup.Provider wrapperLookup) {
            Set<ResourceLocation> generatedRecipes = Sets.newHashSet();
            List<CompletableFuture<?>> list = new ArrayList<>();
            buildRecipes(wrapperLookup, new RecipeOutput() {
                @Override
                public void accept(
                        ResourceLocation recipeId,
                        Recipe<?> recipe,
                        @Nullable AdvancementHolder advancement
                ) {
                    ResourceLocation identifier = getRecipeIdentifier(recipeId);

                    if (!generatedRecipes.add(identifier)) {
                        throw new IllegalStateException("Duplicate recipe " + identifier);
                    }

                    RegistryOps<JsonElement> registryOps = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
                    JsonObject recipeJson = Recipe.CODEC
                            .encodeStart(registryOps, recipe)
                            .getOrThrow(IllegalStateException::new)
                            .getAsJsonObject();
                    ResourceCondition[] conditions = FabricDataGenHelper.consumeConditions(recipe);
                    FabricDataGenHelper.addConditions(recipeJson, conditions);

                    list.add(DataProvider.saveStable(writer, recipeJson, recipePathProvider.json(identifier)));

                    if (advancement != null) {
                        JsonObject advancementJson = Advancement.CODEC
                                .encodeStart(registryOps, advancement.value())
                                .getOrThrow(IllegalStateException::new)
                                .getAsJsonObject();
                        FabricDataGenHelper.addConditions(advancementJson, conditions);
                        list.add(DataProvider.saveStable(writer, advancementJson, advancementPathProvider.json(getRecipeIdentifier(advancement.id()))));
                    }
                }

                @Override
                @SuppressWarnings("removal")
                public Advancement.Builder advancement() {
                    //noinspection removal
                    return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
                }
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        }

        /**
         * Override this method to change the recipe identifier. The default implementation normalizes the namespace to the mod ID.
         */
        protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
            return ResourceLocation.fromNamespaceAndPath(output.getModId(), identifier.getPath());
        }
    }
}
