package org.betterx.wover.recipe.mixin;

import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.recipe.impl.RecipeRuntimeProviderImpl;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {
    @Shadow
    @Final
    @Mutable
    private Map<Identifier, AdvancementHolder> advancements;

    @Shadow
    private AdvancementTree tree;

    @Inject(method = "<init>", at = @At("TAIL"))
    void wover_addRuntimeRecipeAdvancements(
            HolderLookup.Provider registries,
            CallbackInfo ci
    ) {
        final List<AdvancementHolder> contributed =
                RecipeRuntimeProviderImpl.takeContributedAdvancements(registries);
        if (contributed.isEmpty()) return;

        final Map<Identifier, AdvancementHolder> merged = new LinkedHashMap<>(this.advancements);
        final List<AdvancementHolder> added = new ArrayList<>(contributed.size());
        for (AdvancementHolder holder : contributed) {
            if (merged.putIfAbsent(holder.id(), holder) == null) added.add(holder);
        }
        if (added.isEmpty()) return;

        this.advancements = ImmutableMap.copyOf(merged);
        this.tree.addAll(added);
        LibWoverRecipe.C.LOG.info("Added {} recipe unlock advancements", added.size());
    }
}
