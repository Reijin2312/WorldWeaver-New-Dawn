package org.betterx.wover.recipe.mixin;

import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.recipe.impl.RecipeRuntimeProviderImpl;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeManager;

import com.google.common.base.Stopwatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Lnet/minecraft/core/HolderLookup;)Lnet/minecraft/world/item/crafting/RecipeMap;"
            )
    )
    private HolderLookup<Recipe<?>> wover_addRuntimeRecipes(
            HolderLookup<Recipe<?>> datapackRecipes,
            @Local(argsOnly = true) HolderLookup.Provider registries
    ) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        long count = datapackRecipes.listElements().count();
        HolderLookup<Recipe<?>> withRuntime = RecipeRuntimeProviderImpl.loadedRecipes(datapackRecipes, registries);

        LibWoverRecipe.C.LOG.info(
                "Added {} recipes in {}ms",
                withRuntime.listElements().count() - count,
                stopwatch.stop().elapsed().toMillis()
        );
        return withRuntime;
    }
}
