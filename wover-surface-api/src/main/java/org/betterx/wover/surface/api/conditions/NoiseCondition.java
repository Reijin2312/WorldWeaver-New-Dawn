package org.betterx.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

/**
 * A {@link net.minecraft.world.level.levelgen.MaterialCondition} that
 * is based on a custom noise function.
 */
public interface NoiseCondition extends MaterialCondition {
    /**
     * Tests the condition when evaluation a {@link net.minecraft.world.level.levelgen.MaterialRule}
     *
     * @param context the evaluation context
     * @return whether the condition is true
     */
    boolean test(MaterialRuleContext context);
}
