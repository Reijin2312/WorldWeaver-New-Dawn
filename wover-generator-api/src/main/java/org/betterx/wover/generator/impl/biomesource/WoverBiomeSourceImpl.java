package org.betterx.wover.generator.impl.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.generator.impl.compat.BlueprintBiomeSourceCompat;
import org.betterx.wover.generator.impl.compat.CopiedEndBiomeRegistryCompat;
import org.betterx.wover.generator.impl.compat.TerraBlenderEndBiomeCompat;
import org.betterx.wover.generator.impl.compat.VanillaNetherBiomeCompat;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.util.Pair;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class WoverBiomeSourceImpl {
   public static String getNamespaces(Collection<Holder<Biome>> biomes) {
      List<String> namespaces = biomes.stream()
         .filter(h -> h.unwrapKey().isPresent())
         .map(h -> ((ResourceKey)h.unwrapKey().get()).identifier().getNamespace())
         .toList();
      return namespaces.stream().distinct().map(n -> n + "(" + namespaces.stream().filter(n::equals).count() + ")").collect(Collectors.joining(", "));
   }

   @Nullable
   public static Set<Holder<Biome>> populateBiomePickers(List<WoverBiomeSource.TagToPicker> pickers, WoverBiomeSource.PickerAdder pickerAdder) {
      RegistryAccess access = WorldState.registryAccess();
      if (access == null) {
         access = WorldState.allStageRegistryAccess();
         if (access == null) {
            if (!ModCore.isDatagen()) {
               LibWoverWorldGenerator.C.log.verbose("Unable to build Biome List yet");
            }

            return null;
         }

         LibWoverWorldGenerator.C.log.verbose("Registries were not finalized before populating BiomePickers!");
      }

      Set<Holder<Biome>> allBiomes = new LinkedHashSet<>();
      Set<BiomePlacement> addedBiomePlacements = new HashSet<>();
      Registry<Biome> biomes = access.lookupOrThrow(Registries.BIOME);
      Registry<BiomeData> biomeData = access.lookup(BiomeDataRegistry.BIOME_DATA_REGISTRY).orElse(null);

      for (WoverBiomeSource.TagToPicker mapper : pickers) {
         Optional<Named<Biome>> optionalTag = biomes.get(mapper.tag());
         if (optionalTag.isPresent()) {
            Named<Biome> tag = optionalTag.get();
            Set<Identifier> excluded = BiomeSourceManagerImpl.getExcludedBiomes(tag.key());
            tag.stream()
               .filter(holder -> holder.unwrapKey().isPresent())
               .map(holder -> new Pair(holder, (ResourceKey)holder.unwrapKey().get()))
               .filter(pair -> !addedBiomePlacements.contains(new BiomePlacement(mapper.tag(), (ResourceKey<Biome>) pair.second)))
               .filter(pair -> !excluded.contains(((ResourceKey)pair.second).identifier()))
               .sorted(Comparator.comparing(pair -> ((ResourceKey)pair.second).identifier().toString()))
               .forEach(pair -> {
                  ResourceKey<Biome> biomeKey = (ResourceKey<Biome>)pair.second;
                  BiomeData data = BiomeDataRegistryImpl.getFromRegistryOrTemp(biomeData, biomeKey);
                  final boolean isPossible;
                  if (data != null && !data.isTemp()) {
                     isPossible = data.isPickable()
                        ? pickerAdder.add(data, mapper.tag(), mapper.picker())
                        : true;
                  } else {
                     data = BlueprintBiomeSourceCompat.getImportedBiomeData(biomeKey);
                     if (data == null || data.isTemp()) {
                        data = TerraBlenderEndBiomeCompat.getImportedBiomeData(biomeKey);
                     }
                     if (data == null || data.isTemp()) {
                        data = CopiedEndBiomeRegistryCompat.getImportedBiomeData(biomeKey);
                     }
                     if (data == null || data.isTemp()) {
                        data = VanillaNetherBiomeCompat.getImportedBiomeData(biomeKey);
                     }
                     isPossible = data != null && !data.isTemp() && data.isPickable()
                        && pickerAdder.add(data, mapper.tag(), mapper.picker());
                  }

                  if (isPossible) {
                     addedBiomePlacements.add(new BiomePlacement(mapper.tag(), (ResourceKey<Biome>) pair.second));
                     allBiomes.add((Holder<Biome>)pair.first);
                  }
               });
         }
      }

      return allBiomes;
   }

   public record PopulateResult(Set<Holder<Biome>> possibleBiomes, List<WoverBiomeSource.TagToPicker> pickers) {
   }

   private record BiomePlacement(net.minecraft.tags.TagKey<Biome> tag, ResourceKey<Biome> biome) {
   }
}
