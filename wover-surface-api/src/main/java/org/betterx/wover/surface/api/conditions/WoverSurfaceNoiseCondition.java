package org.betterx.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.SurfaceRules;

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
    public final SurfaceRules.Condition apply(SurfaceRules.Context context2) {
        final WoverSurfaceNoiseCondition self = this;

        class Generator extends SurfaceRules.LazyXZCondition {
            Generator() {
                super(context2);
            }

            @Override
            protected boolean compute() {
                final SurfaceRulesContext context = SurfaceRulesContext.class.cast(this.context);
                return context != null && self.test(context);
            }
        }

        return new Generator();
    }
}
