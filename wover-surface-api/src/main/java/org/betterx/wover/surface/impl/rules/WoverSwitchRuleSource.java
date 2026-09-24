package org.betterx.wover.surface.impl.rules;

import org.betterx.wover.surface.api.noise.NumericProvider;

import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

import java.util.List;

/**
 * Switch rule source implementation that can access package-private
 * SurfaceRules types in 1.21.1.
 */
public abstract class WoverSwitchRuleSource implements MaterialRule {
    protected abstract NumericProvider selector();

    protected abstract List<MaterialRule> collection();

    @Override
    public RuleEvaluator compile(MaterialRuleContext context) {
        return (x, y, z) -> {
            int nr = Math.max(0, selector().getNumber(context)) % collection().size();
            return collection().get(nr).compile(context).tryApply(x, y, z);
        };
    }
}
