package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;
import org.betterx.wover.datagen.api.WoverRecipeGenerator;
import org.betterx.wover.recipe.impl.WoverRecipeProviderAccess;

import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContextAccess;

import java.util.concurrent.CompletableFuture;

public abstract class WoverRecipeProvider implements WoverDataProvider<net.minecraft.data.DataProvider>, WoverRecipeGenerator {
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

    protected abstract void bootstrap(BootstrapContextAccess provider, RecipeOutput context);

    @Override
    public void buildRecipes(BootstrapContextAccess lookup, RecipeOutput exporter) {
        WoverRecipeProviderAccess.withLookup(lookup, () -> bootstrap(lookup, exporter));
    }

    @Override
    public net.minecraft.data.DataProvider getProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new net.minecraft.data.DataProvider() {
            @Override
            public String getName() {
                return "Recipes: " + modCore.namespace + "/" + title;
            }

            @Override
            public CompletableFuture<?> run(net.minecraft.data.CachedOutput cache) {
                return CompletableFuture.failedFuture(new IllegalStateException(
                        "WoverRecipeProvider must be registered through WoverDataGenEntryPoint"
                ));
            }
        };
    }
}
