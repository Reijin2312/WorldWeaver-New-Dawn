package org.betterx.wover.surface.impl.conditions;

import org.betterx.wover.surface.api.noise.NoiseParameterManager;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * Rough noise condition implementation that can access package-private
 * SurfaceRules types in 1.21.1.
 */
public abstract class WoverRoughNoiseCondition implements MaterialCondition {
    protected abstract ResourceKey<NormalNoise> noise();

    protected abstract double minThreshold();

    protected abstract double maxThreshold();

    protected abstract FloatProvider roughness();

    @Override
    public ConditionEvaluator compile(final MaterialRuleContext context) {
        final var noiseSampler = context.getNoiseSampler(noise(), true);
        final RandomSource roughnessSource = context
                .getOrCreateRandomFactory(NoiseParameterManager.ROUGHNESS_NOISE.identifier())
                .fromHashOf(NoiseParameterManager.ROUGHNESS_NOISE.identifier());
        return () -> {
            double value = noiseSampler.getAsDouble() + roughness().sample(roughnessSource);
            return value >= minThreshold() && value <= maxThreshold();
        };
    }
}
