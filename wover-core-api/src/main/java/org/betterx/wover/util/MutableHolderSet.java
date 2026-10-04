package org.betterx.wover.util;

import com.mojang.datafixers.util.Either;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MutableHolderSet<T> extends HolderSet.ListBacked<T> {
    private final List<Holder<T>> contents;

    protected MutableHolderSet(List<Holder<T>> contents) {
        this.contents = contents;
    }

    public static <T> MutableHolderSet<T> of(List<Holder<T>> contents) {
        return new MutableHolderSet<>(contents);
    }

    @Nullable
    public static <T> MutableHolderSet<T> of(HolderSet<T> contents) {
        List<Holder<T>> content = contents.unwrap().right().orElse(null);
        return content == null ? null : new MutableHolderSet<>(new LinkedList<>(content));
    }

    public HolderSet<T> asDirectHolderSet() {
        return HolderSet.direct(contents);
    }

    @Override
    @NotNull
    public List<Holder<T>> contents() {
        return contents;
    }

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    @NotNull
    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(contents);
    }

    @Override
    public boolean contains(Holder<T> holder) {
        return contents.contains(holder);
    }

    @Override
    @NotNull
    public Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof MutableHolderSet<?> holder && contents.equals(holder.contents);
    }

    @Override
    public int hashCode() {
        return contents.hashCode();
    }
}
