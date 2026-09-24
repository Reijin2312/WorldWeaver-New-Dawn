package org.betterx.wover.recipe.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class RecipeLookups {
    private RecipeLookups() {
    }

    public static HolderLookup<Recipe<?>> extended(
            HolderLookup<Recipe<?>> base,
            Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> extra
    ) {
        if (extra.isEmpty()) return base;

        final Map<ResourceKey<Recipe<?>>, Holder.Reference<Recipe<?>>> added = new LinkedHashMap<>();
        extra.forEach((key, holder) -> added.put(key, reference(base, key, holder)));
        return new Delegating(base) {
            @Override
            public Stream<Holder.Reference<Recipe<?>>> listElements() {
                return Stream.concat(
                        base.listElements().filter(ref -> !added.containsKey(ref.key())),
                        added.values().stream()
                );
            }

            @Override
            public Optional<Holder.Reference<Recipe<?>>> get(ResourceKey<Recipe<?>> key) {
                Holder.Reference<Recipe<?>> ours = added.get(key);
                return ours != null ? Optional.of(ours) : base.get(key);
            }
        };
    }

    public static HolderLookup<Recipe<?>> filtered(
            HolderLookup<Recipe<?>> base,
            Predicate<ResourceKey<Recipe<?>>> keep
    ) {
        return new Delegating(base) {
            @Override
            public Stream<Holder.Reference<Recipe<?>>> listElements() {
                return base.listElements().filter(ref -> keep.test(ref.key()));
            }

            @Override
            public Optional<Holder.Reference<Recipe<?>>> get(ResourceKey<Recipe<?>> key) {
                return keep.test(key) ? base.get(key) : Optional.empty();
            }
        };
    }

    private static Holder.Reference<Recipe<?>> reference(
            HolderLookup<Recipe<?>> base,
            ResourceKey<Recipe<?>> key,
            RecipeHolder<?> holder
    ) {
        Holder.Reference<Recipe<?>> reference = Holder.Reference.createStandAlone(base, key);
        reference.bindValue(holder.value());
        return reference;
    }

    private abstract static class Delegating implements HolderLookup<Recipe<?>> {
        private final HolderLookup<Recipe<?>> base;

        private Delegating(HolderLookup<Recipe<?>> base) {
            this.base = base;
        }

        @Override
        public Stream<HolderSet.Named<Recipe<?>>> listTags() {
            return base.listTags();
        }

        @Override
        public Optional<HolderSet.Named<Recipe<?>>> get(TagKey<Recipe<?>> tag) {
            return base.get(tag);
        }
    }
}
