package org.betterx.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Volume noise condition implementation that can access package-private
 * SurfaceRules types in 1.21.1.
 */
public abstract class WoverVolumeNoiseCondition implements NoiseCondition {
    /**
     * Calls {@link #test(SurfaceRulesContext)} with the correct context type for
     * a 3D (X/Y/Z) location.
     */
    @Override
    public final SurfaceRules.Condition apply(SurfaceRules.Context context2) {
        final WoverVolumeNoiseCondition self = this;

        class Generator extends SurfaceRules.LazyCondition {
            Generator() {
                super(context2);
            }

            @Override
            protected long getContextLastUpdate() {
                final SurfaceRulesContext ctx = SurfaceRulesContext.class.cast(this.context);
                return ctx.getLastUpdateY() + ctx.getLastUpdateXZ();
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
