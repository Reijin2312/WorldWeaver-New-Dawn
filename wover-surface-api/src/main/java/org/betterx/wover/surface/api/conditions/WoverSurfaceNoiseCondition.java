package org.betterx.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

/**
 * Surface noise condition implementation that can access package-private
 * SurfaceRules types in 1.21.1.
 */
public abstract class WoverSurfaceNoiseCondition implements NoiseCondition {
    /**
     * Calls {@link #test(SurfaceRulesContext)} with the correct context type for
     * a 2D (X/Z) location.
     */
    @Override
    public final ConditionEvaluator compile(MaterialRuleContext context) {
        return () -> test(context);
    }
}
