package org.betterx.wover.surface.api.rules;

import org.betterx.wover.surface.impl.rules.MaterialRuleRegistryImpl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

import org.jetbrains.annotations.NotNull;

/**
 * A helper class for registering material rules in {@link net.minecraft.core.registries.BuiltInRegistries#MATERIAL_RULE}
 */
public class MaterialRuleManager {
    /**
     * Registers a new rule source.
     *
     * @param location The location of the rule source.
     * @param rule     The rule source.
     * @return The key for the rule source.
     */
    public static ResourceKey<MapCodec<? extends MaterialRule>> register(
            Identifier location,
            MapCodec<? extends MaterialRule> rule
    ) {
        return MaterialRuleRegistryImpl.register(MaterialRuleRegistryImpl.createKey(location), rule);
    }

    /**
     * Registers a new rule source.
     *
     * @param key  The key for the rule source.
     * @param rule The rule source.
     * @return The key for the rule source.
     */
    public static ResourceKey<MapCodec<? extends MaterialRule>> register(
            ResourceKey<MapCodec<? extends MaterialRule>> key,
            MapCodec<? extends MaterialRule> rule
    ) {
        return MaterialRuleRegistryImpl.register(key, rule);
    }

    /**
     * Creates a {@link ResourceKey} for a new rule source.
     *
     * @param location The location of the rule source.
     * @return The key for the rule source.
     */
    @NotNull
    public static ResourceKey<MapCodec<? extends MaterialRule>> createKey(Identifier location) {
        return MaterialRuleRegistryImpl.createKey(location);
    }

    private MaterialRuleManager() {
    }
}
