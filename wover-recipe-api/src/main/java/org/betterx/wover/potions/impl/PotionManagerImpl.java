package org.betterx.wover.potions.impl;

import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.potions.api.BrewingBuilder;
import org.betterx.wover.potions.api.OnBootstrapPotions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.packs.VanillaBrewingProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class PotionManagerImpl {
    public static final EventImpl<OnBootstrapPotions> BOOTSTRAP_POTIONS =
            new EventImpl<>("BOOTSTRAP_POTIONS");

    public static void bootstrapBrewingRecipes(RecipeOutput output) {
        Recorder extensions = new Recorder();
        BOOTSTRAP_POTIONS.emit(c -> c.bootstrap(extensions));
        if (extensions.isEmpty()) return;

        Set<ResourceKey<Recipe<?>>> vanillaKeys = new HashSet<>();
        new WoverBrewingProvider(
                new ForwardingRecipeOutput(output, (key, recipe) -> vanillaKeys.add(key)),
                new Recorder()
        ).buildRecipes();
        new WoverBrewingProvider(
                new ForwardingRecipeOutput(output, (key, recipe) -> {
                    if (!vanillaKeys.contains(key)) output.accept(key, recipe, null);
                }),
                extensions
        ).buildRecipes();
    }

    private record ForwardingRecipeOutput(
            RecipeOutput delegate,
            BiConsumer<ResourceKey<Recipe<?>>, Recipe<?>> sink
    ) implements RecipeOutput {
        @Override
        public void accept(
                ResourceKey<Recipe<?>> key,
                Recipe<?> recipe,
                @Nullable AdvancementHolder advancement,
                ICondition... conditions
        ) {
            sink.accept(key, recipe);
        }

        @Override
        public Advancement.Builder advancement() {
            return delegate.advancement();
        }

        @Override
        public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
            return delegate.lookup(key);
        }

        @Override
        public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
            return delegate.listContextElements(key);
        }
    }

    private static final class Recorder implements BrewingBuilder {
        private final List<Item> containers = new ArrayList<>();
        private final List<ContainerRecipe> containerRecipes = new ArrayList<>();
        private final List<Mix> mixes = new ArrayList<>();
        private final List<StartMix> startMixes = new ArrayList<>();

        private boolean isEmpty() {
            return containers.isEmpty() && containerRecipes.isEmpty() && mixes.isEmpty() && startMixes.isEmpty();
        }

        @Override
        public void addContainer(Item container) {
            containers.add(container);
        }

        @Override
        public void addContainerRecipe(Item container, Item reagent, Item result) {
            containerRecipes.add(new ContainerRecipe(container, reagent, result));
        }

        @Override
        public void addMix(Holder<Potion> from, Item reagent, Holder<Potion> to) {
            mixes.add(new Mix(from, reagent, to));
        }

        @Override
        public void addStartMix(Item reagent, Holder<Potion> potion) {
            startMixes.add(new StartMix(reagent, potion));
        }

        private record ContainerRecipe(Item container, Item reagent, Item result) {}
        private record Mix(Holder<Potion> from, Item reagent, Holder<Potion> to) {}
        private record StartMix(Item reagent, Holder<Potion> potion) {}
    }

    private static final class WoverBrewingProvider extends VanillaBrewingProvider {
        private final Recorder extensions;

        private WoverBrewingProvider(RecipeOutput output, Recorder extensions) {
            super(output);
            this.extensions = extensions;
        }

        @Override
        protected void addContainers() {
            super.addContainers();
            extensions.containers.forEach(this::addContainer);
        }

        @Override
        protected void addContainerTransformations() {
            super.addContainerTransformations();
            extensions.containerRecipes.forEach(r -> addContainerTransformation(r.container(), r.reagent(), r.result()));
        }

        @Override
        protected void buildMixes() {
            super.buildMixes();
            extensions.mixes.forEach(m -> buildMix(m.from(), m.reagent(), m.to()));
            extensions.startMixes.forEach(m -> buildStartMix(m.reagent(), m.potion()));
        }
    }
}
