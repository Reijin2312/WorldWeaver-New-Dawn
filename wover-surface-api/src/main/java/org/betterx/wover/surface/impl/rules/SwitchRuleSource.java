package org.betterx.wover.surface.impl.rules;

import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

//
public final class SwitchRuleSource extends WoverSwitchRuleSource implements MaterialRule {
    public static final MapCodec<SwitchRuleSource> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(
                            NumericProvider.CODEC.fieldOf("selector").forGetter(SwitchRuleSource::selector),
                            MaterialRule.CODEC.listOf().fieldOf("collection").forGetter(SwitchRuleSource::collection)
                    )
                    .apply(
                            instance,
                            SwitchRuleSource::new
                    ));

    private final NumericProvider selector;
    private final List<MaterialRule> collection;

    public SwitchRuleSource(NumericProvider selector, List<MaterialRule> collection) {
        this.selector = selector;
        this.collection = collection;
    }

    @Override
    public NumericProvider selector() {
        return selector;
    }

    @Override
    public List<MaterialRule> collection() {
        return collection;
    }

    @Override
    public @NotNull MapCodec<? extends MaterialRule> codec() {
        return CODEC;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SwitchRuleSource that)) return false;
        return Objects.equals(selector, that.selector) && Objects.equals(collection, that.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, collection);
    }

    @Override
    public String toString() {
        return "SwitchRuleSource[selector=" + selector + ", collection=" + collection + "]";
    }

}
