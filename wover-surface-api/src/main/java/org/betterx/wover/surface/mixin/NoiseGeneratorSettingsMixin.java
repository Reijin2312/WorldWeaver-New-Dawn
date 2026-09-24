package org.betterx.wover.surface.mixin;

import org.betterx.wover.common.surface.api.SurfaceRuleProvider;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseGeneratorSettings.class)
public abstract class NoiseGeneratorSettingsMixin implements SurfaceRuleProvider {
    @Mutable
    @Final
    @Shadow
    private MaterialRule surfaceRule;

    public void wover_overwriteSurfaceRules(MaterialRule surfaceRule) {
        if (surfaceRule == null || surfaceRule == this.surfaceRule) return;
        if (this.wover_containsOverride) {
            // Avoid referencing split module entrypoint/loggers from injected MC code in datagen/dev.
        }
        this.wover_containsOverride = true;
        this.surfaceRule = surfaceRule;
    }

    public MaterialRule wover_getOriginalSurfaceRules() {
        return this.surfaceRule;
    }

    private boolean wover_containsOverride = false;
}
