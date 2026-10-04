package org.betterx.wover.surface.impl.rules;

import org.betterx.wover.entrypoint.LibWoverSurface;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.surface.api.rules.MaterialRuleManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MaterialRuleRegistryImpl {
    public static ResourceKey<MapCodec<? extends MaterialRule>> SWITCH_RULE
            = MaterialRuleManager.createKey(LibWoverSurface.C.id("switch_rule"));

    public static ResourceKey<MapCodec<? extends MaterialRule>> register(
            ResourceKey<MapCodec<? extends MaterialRule>> key,
            MapCodec<? extends MaterialRule> rule
    ) {
        Registry.register(BuiltInRegistries.MATERIAL_RULE_TYPE, key, rule);
        return key;
    }

    @NotNull
    public static ResourceKey<MapCodec<? extends MaterialRule>> createKey(Identifier location) {
        return ResourceKey.create(
                BuiltInRegistries.MATERIAL_RULE_TYPE.key(),
                location
        );
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        register(SWITCH_RULE, SwitchRuleSource.CODEC);

        if (LegacyHelper.isLegacyEnabled()) {
            Registry.register(
                    BuiltInRegistries.MATERIAL_RULE_TYPE,
                    "bclib_switch_rule",
                    LegacyHelper.wrap(SwitchRuleSource.CODEC)
            );
        }
    }
}
