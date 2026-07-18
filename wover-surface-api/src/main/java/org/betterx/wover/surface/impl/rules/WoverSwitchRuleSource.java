package org.betterx.wover.surface.impl.rules;

import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.noise.NumericProvider;

import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

/**
 * Switch rule source implementation that can access package-private
 * SurfaceRules types in 1.21.1.
 */
public abstract class WoverSwitchRuleSource implements SurfaceRules.RuleSource {
    protected abstract NumericProvider selector();

    protected abstract List<SurfaceRules.RuleSource> collection();

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return (x, y, z) -> {
            final SurfaceRulesContext ctx = SurfaceRulesContext.class.cast(context);
            int nr = Math.max(0, selector().getNumber(ctx)) % collection().size();
            return collection().get(nr).apply(context).tryApply(x, y, z);
        };
    }
}
