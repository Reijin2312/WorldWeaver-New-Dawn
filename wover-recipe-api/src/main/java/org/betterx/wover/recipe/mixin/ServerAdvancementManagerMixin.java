package org.betterx.wover.recipe.mixin;

import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.recipe.impl.RecipeRuntimeProviderImpl;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
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
    private Map<ResourceLocation, AdvancementHolder> advancements;

    @Shadow
    private AdvancementTree tree;

    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL"))
    void wover_addRuntimeRecipeAdvancements(
            Map<ResourceLocation, Advancement> loaded,
            ResourceManager resourceManager,
            ProfilerFiller profilerFiller,
            CallbackInfo ci
    ) {
        final List<AdvancementHolder> contributed =
                RecipeRuntimeProviderImpl.takeContributedAdvancements(this.registries);
        if (contributed.isEmpty()) return;

        final Map<ResourceLocation, AdvancementHolder> merged = new LinkedHashMap<>(this.advancements);
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
