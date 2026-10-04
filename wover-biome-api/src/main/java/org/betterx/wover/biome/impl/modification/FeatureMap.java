package org.betterx.wover.biome.impl.modification;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureMap extends ArrayList<LinkedList<Holder<PlacedFeature>>> {
   public static final Codec<List<List<Holder<PlacedFeature>>>> CODEC = PlacedFeature.CODEC.listOf().listOf();

   public void addFeature(Decoration decoration, Holder<PlacedFeature> feature) {
      this.getFeatures(decoration).add(feature);
   }

   public List<Holder<PlacedFeature>> getFeatures(Decoration decoration) {
      int index = decoration.ordinal();

      while (this.size() <= index) {
         this.add(new LinkedList<>());
      }

      return this.get(index);
   }

   public static HolderSet<PlacedFeature> getFeatures(List<HolderSet<PlacedFeature>> features, Decoration decoration) {
      int index = decoration.ordinal();

      while (features.size() <= index) {
         features.add(HolderSet.direct(Collections.emptyList()));
      }

      return features.get(index);
   }

   public void forEach(BiConsumer<Decoration, Holder<PlacedFeature>> consumer) {
      for (int i = 0; i < this.size(); i++) {
         Decoration decoration = Decoration.values()[i];

         for (Holder<PlacedFeature> feature : this.getFeatures(decoration)) {
            consumer.accept(decoration, feature);
         }
      }
   }

   public List<List<Holder<PlacedFeature>>> generic() {
      return this.stream().map(list -> (List<Holder<PlacedFeature>>)list).toList();
   }

   public static FeatureMap of(List<List<Holder<PlacedFeature>>> features) {
      FeatureMap map = new FeatureMap();

      for (List<Holder<PlacedFeature>> list : features) {
         if (list instanceof LinkedList lList) {
            map.add(lList);
         } else {
            LinkedList<Holder<PlacedFeature>> nList = new LinkedList<>(list);
            map.add(nList);
         }
      }

      return map;
   }
}
