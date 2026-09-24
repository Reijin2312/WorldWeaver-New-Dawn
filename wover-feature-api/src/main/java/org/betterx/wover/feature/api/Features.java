package org.betterx.wover.feature.api;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.feature.api.features.ConditionFeature;
import org.betterx.wover.feature.api.features.MarkPostProcessingFeature;
import org.betterx.wover.feature.api.features.PillarFeature;
import org.betterx.wover.feature.api.features.PlaceFacingBlockFeature;
import org.betterx.wover.feature.api.features.SequenceFeature;
import org.betterx.wover.feature.api.features.TemplateFeature;
import org.betterx.wover.feature.impl.FeatureManagerImpl;

public class Features {
   public static final MapCodec<PlaceFacingBlockFeature> PLACE_BLOCK = FeatureManagerImpl.PLACE_BLOCK;
   public static final MapCodec<MarkPostProcessingFeature> MARK_POSTPROCESSING = FeatureManagerImpl.MARK_POSTPROCESSING;
   public static final MapCodec<SequenceFeature> SEQUENCE = FeatureManagerImpl.SEQUENCE;
   public static final MapCodec<ConditionFeature> CONDITION = FeatureManagerImpl.CONDITION;
   public static final MapCodec<PillarFeature> PILLAR = FeatureManagerImpl.PILLAR;
   public static final MapCodec<TemplateFeature> TEMPLATE = FeatureManagerImpl.TEMPLATE;

   private Features() {
   }
}
