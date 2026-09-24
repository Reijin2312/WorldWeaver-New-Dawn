package org.betterx.wover.generator.impl.client;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceConfigPanel;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import de.ambertation.wunderlib.ui.layout.components.Checkbox;
import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wunderlib.ui.layout.components.Range;
import de.ambertation.wunderlib.ui.layout.components.VerticalStack;
import de.ambertation.wunderlib.ui.layout.values.Value;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class EndConfigPage implements BiomeSourceConfigPanel<WoverEndBiomeSource, WoverEndConfig> {
   private final Checkbox endLegacy;
   private final Checkbox endCustomTerrain;
   private final Checkbox generateEndVoid;
   private final Range<Integer> landBiomeSize;
   private final Range<Integer> voidBiomeSize;
   private final Range<Integer> centerBiomeSize;
   private final Range<Integer> barrensBiomeSize;
   private final Range<Integer> caveBiomeSize;
   private final Range<Integer> innerRadius;
   private final Range<Integer> caveTopY;
   private final Range<Integer> caveTopJitter;
   private final VerticalStack pageComponent;

   public EndConfigPage(@NotNull WoverEndConfig endConfig) {
      VerticalStack content = (VerticalStack)new VerticalStack(Value.fill(), Value.fit()).centerHorizontal();
      content.addSpacer(8);
      this.endLegacy = content.addCheckbox(
         Value.fit(),
         Value.fit(),
         Component.translatable("title.screen.wover.worldgen.legacy_square"),
         endConfig.mapVersion == WoverEndConfig.EndBiomeMapType.SQUARE
      );
      this.endCustomTerrain = content.addCheckbox(
         Value.fit(),
         Value.fit(),
         Component.translatable("title.screen.wover.worldgen.custom_end_terrain"),
         endConfig.generatorVersion != WoverEndConfig.EndBiomeGeneratorType.VANILLA
      );
      this.generateEndVoid = content.addCheckbox(
         Value.fit(), Value.fit(), Component.translatable("title.screen.wover.worldgen.end_void"), endConfig.withVoidBiomes
      );
      content.addSpacer(12);
      content.addText(Value.fit(), Value.fit(), Component.translatable("title.screen.wover.worldgen.avg_biome_size")).centerHorizontal();
      content.addHorizontalSeparator(8).alignTop();
      this.landBiomeSize = content.addRange(
         Value.fixed(200), Value.fit(), Component.translatable("title.screen.wover.worldgen.land_biome_size"), 1, 512, endConfig.landBiomesSize / 16
      );
      this.voidBiomeSize = content.addRange(
         Value.fixed(200), Value.fit(), Component.translatable("title.screen.wover.worldgen.void_biome_size"), 1, 512, endConfig.voidBiomesSize / 16
      );
      this.centerBiomeSize = content.addRange(
         Value.fixed(200), Value.fit(), Component.translatable("title.screen.wover.worldgen.center_biome_size"), 1, 512, endConfig.centerBiomesSize / 16
      );
      this.barrensBiomeSize = content.addRange(
         Value.fixed(200), Value.fit(), Component.translatable("title.screen.wover.worldgen.barrens_biome_size"), 1, 512, endConfig.barrensBiomesSize / 16
      );
      this.caveBiomeSize = content.addRange(
         Value.fixed(200),
         Value.fit(),
         Component.translatable("title.screen.wover.worldgen.cave_biome_size"),
         1,
         512,
         Math.max(1, endConfig.caveBiomesSize / 16)
      );
      content.addSpacer(12);
      content.addText(Value.fit(), Value.fit(), Component.translatable("title.screen.wover.worldgen.other")).centerHorizontal();
      content.addHorizontalSeparator(8).alignTop();
      this.innerRadius = content.addRange(
         Value.fixed(200),
         Value.fit(),
         Component.translatable("title.screen.wover.worldgen.central_radius"),
         1,
         512,
         (int)Math.sqrt(endConfig.innerVoidRadiusSquared) / 16
      );
      this.caveTopY = content.addRange(
         Value.fixed(200), Value.fit(), Component.translatable("title.screen.wover.worldgen.cave_top_y"), 0, 256, endConfig.caveBiomesTopY
      );
      this.caveTopJitter = content.addRange(
         Value.fixed(200), Value.fit(), Component.translatable("title.screen.wover.worldgen.cave_top_jitter"), 0, 16, endConfig.caveBiomesTopJitter
      );
      this.endCustomTerrain.onChange((cb, state) -> {
         this.landBiomeSize.setEnabled(state);
         this.voidBiomeSize.setEnabled(state && this.generateEndVoid.isChecked());
         this.centerBiomeSize.setEnabled(state);
         this.barrensBiomeSize.setEnabled(state);
      });
      this.generateEndVoid.onChange((cb, state) -> this.voidBiomeSize.setEnabled(state && this.endCustomTerrain.isChecked()));
      content.addSpacer(8);
      this.pageComponent = (VerticalStack)content.setDebugName("End Page");
   }

   @Override
   public LayoutComponent<?, ?> getPanel() {
      return this.pageComponent;
   }

   @Override
   public ChunkGenerator updateSettings(ChunkGenerator endGenerator) {
      if (endGenerator.getBiomeSource() instanceof BiomeSourceWithConfig<?, ?> bsc && bsc.getBiomeSourceConfig() instanceof WoverEndConfig currentConfig) {
         WoverEndConfig endConfig = new WoverEndConfig(
            this.endLegacy.isChecked() ? WoverEndConfig.EndBiomeMapType.SQUARE : WoverEndConfig.EndBiomeMapType.HEX,
            this.endCustomTerrain.isChecked() ? WoverEndConfig.EndBiomeGeneratorType.PAULEVS : WoverEndConfig.EndBiomeGeneratorType.VANILLA,
            this.generateEndVoid.isChecked(),
            (int)Math.pow((Integer)this.innerRadius.getValue() * 16, 2.0),
            (Integer)this.centerBiomeSize.getValue() * 16,
            (Integer)this.voidBiomeSize.getValue() * 16,
            (Integer)this.landBiomeSize.getValue() * 16,
            (Integer)this.barrensBiomeSize.getValue() * 16,
            (Integer)this.caveBiomeSize.getValue() * 16,
            (Integer)this.caveTopY.getValue(),
            (Integer)this.caveTopJitter.getValue()
         );
         @SuppressWarnings("unchecked")
         BiomeSourceWithConfig<?, WoverEndConfig> typed = (BiomeSourceWithConfig<?, WoverEndConfig>)bsc;
         typed.setBiomeSourceConfig(endConfig);
      }

      return endGenerator;
   }
}
