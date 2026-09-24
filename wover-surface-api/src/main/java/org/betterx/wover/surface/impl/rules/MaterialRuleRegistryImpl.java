package org.betterx.wover.surface.impl.rules;

import org.betterx.wover.entrypoint.LibWoverSurface;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.surface.api.rules.MaterialRuleManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

import net.neoforged.neoforge.registries.RegisterEvent;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MaterialRuleRegistryImpl {
    public static ResourceKey<MapCodec<? extends MaterialRule>> SWITCH_RULE
            = MaterialRuleManager.createKey(LibWoverSurface.C.id("switch_rule"));

    public static ResourceKey<MapCodec<? extends MaterialRule>> register(
            ResourceKey<MapCodec<? extends MaterialRule>> key,
            MapCodec<? extends MaterialRule> rule
    ) {
        return key;
    }

    @NotNull
    public static ResourceKey<MapCodec<? extends MaterialRule>> createKey(Identifier location) {
        return ResourceKey.create(
                Registries.MATERIAL_RULE_TYPE,
                location
        );
    }

    @ApiStatus.Internal
    public static void register(RegisterEvent event) {
        event.register(Registries.MATERIAL_RULE_TYPE, helper -> {
            helper.register(SWITCH_RULE.identifier(), SwitchRuleSource.CODEC);

            if (LegacyHelper.isLegacyEnabled()) {
                helper.register(
                        LegacyHelper.BCLIB_CORE.convertNamespace(SWITCH_RULE.identifier()),
                        LegacyHelper.wrap(SwitchRuleSource.CODEC)
                );
            }
        });
    }
}
