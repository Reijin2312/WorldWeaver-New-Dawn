package org.betterx.wover.surface.impl;

import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.util.PriorityLinkedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

public class AssignedSurfaceRuleImpl extends AssignedSurfaceRule {
    public static final Codec<AssignedSurfaceRule> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    MaterialRule.CODEC.fieldOf("ruleSource").forGetter(o -> o.ruleSource),
                    Identifier.CODEC.fieldOf("biome").forGetter(o -> o.biomeID),
                    Codec.INT.fieldOf("priority").orElse(PriorityLinkedList.DEFAULT_PRIORITY).forGetter(o -> o.priority)
            )
            .apply(instance, AssignedSurfaceRuleImpl::new)
    );

    AssignedSurfaceRuleImpl(
            MaterialRule ruleSource,
            Identifier biomeID,
            int priority
    ) {
        super(ruleSource, biomeID, priority);
    }
}
