package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.biome.mixin.HolderReferenceAccessor;
import org.betterx.wover.biome.mixin.HolderSetNamedAccessor;
import org.betterx.wover.entrypoint.LibWoverBiome;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeTagModificationWorker {
   final Map<HolderSetNamedAccessor<Biome>, List<Holder<Biome>>> unfrozen = new HashMap<>();

   boolean addBiomeToTag(TagKey<Biome> tag, BiomePredicate.Context context) {
      return this.addBiomeToTag(tag, context.biomes, context.biomeKey, context.biomeHolder);
   }

   public boolean addBiomeToTag(TagKey<Biome> tag, Registry<Biome> biomes, ResourceKey<Biome> biomeKey, Holder<Biome> biomeHolder) {
      Object namedSet = biomes.getOrThrow(tag);
      if (namedSet instanceof HolderSetNamedAccessor<?> rawBiomeTagHolder) {
         @SuppressWarnings("unchecked")
         HolderSetNamedAccessor<Biome> biomeTagHolder = (HolderSetNamedAccessor<Biome>)rawBiomeTagHolder;
         if (biomeTagHolder.wover_getContents()
            .stream()
            .<Optional>map(Holder::unwrapKey)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(key -> key.equals(biomeKey))) {
            return false;
         } else {
            List<Holder<Biome>> contents = this.unfrozen.computeIfAbsent(biomeTagHolder, holder -> new LinkedList<>(biomeTagHolder.wover_getContents()));
            contents.add(biomeHolder);
            return true;
         }
      } else {
         LibWoverBiome.C.log.warn("Failed to alter BiomeTag {}", new Object[]{tag.location()});
         return false;
      }
   }

   public boolean finished() {
      if (!this.unfrozen.isEmpty()) {
         this.unfrozen.forEach((tagHolder, contents) -> {
            TagKey<Biome> tagKey = ((Named)tagHolder).key();
            tagHolder.wover_setContents(List.copyOf(contents));

            for (Holder<Biome> biomeHolder : contents) {
               if (biomeHolder instanceof HolderReferenceAccessor<?> rawAccessor) {
                  @SuppressWarnings("unchecked")
                  HolderReferenceAccessor<Biome> accessor = (HolderReferenceAccessor<Biome>)rawAccessor;
                  Set<TagKey<Biome>> existing = accessor.wover_getTags();
                  Set<TagKey<Biome>> updated = existing == null ? new HashSet<>() : new HashSet<>(existing);
                  updated.add(tagKey);
                  accessor.wover_setTags(Set.copyOf(updated));
               }
            }
         });
         this.unfrozen.clear();
         return true;
      } else {
         return false;
      }
   }
}
