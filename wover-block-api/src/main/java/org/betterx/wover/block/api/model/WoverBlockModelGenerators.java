package org.betterx.wover.block.api.model;

import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public class WoverBlockModelGenerators {
    public static final ResourceLocation CROSS = ResourceLocation.withDefaultNamespace("block/cross");
    public static final ResourceLocation CUBE = ResourceLocation.withDefaultNamespace("block/cube");
    public static final ResourceLocation CUBE_ALL = ResourceLocation.withDefaultNamespace("block/cube_all");
    public static final ResourceLocation COMPOSTER = LibWoverBlock.C.id("block/composter");

    public static final ModelTemplate COMPOSTER_MODEL = new ModelTemplate(Optional.of(COMPOSTER), Optional.empty(), TextureSlot.SIDE, TextureSlot.BOTTOM, TextureSlot.TOP);
    public final BlockModelGenerators vanillaGenerator;

    public WoverBlockModelGenerators(
            BlockModelGenerators vanillaGenerator
    ) {
        this.vanillaGenerator = vanillaGenerator;
    }

    public void createObsidianVariants(WoverBlockModelGenerators generators, Block obsidianBlock) {
        var model = generators.getTextureModels(obsidianBlock, TexturedModel.CUBE.get(obsidianBlock));
        var template = model.getTemplate();
        var modelLocation = template.create(obsidianBlock, model.getMapping(), generators.vanillaGenerator.modelOutput);
        final VariantProperties.Rotation[] rotations = {
                VariantProperties.Rotation.R0,
                VariantProperties.Rotation.R90,
                VariantProperties.Rotation.R180,
                VariantProperties.Rotation.R270
        };

        final Variant[] variants = new Variant[16];
        int idx = 0;
        for (VariantProperties.Rotation rotation : rotations) {
            for (VariantProperties.Rotation rotationY : rotations) {
                variants[idx] = Variant
                        .variant()
                        .with(VariantProperties.MODEL, modelLocation);
                if (rotation != VariantProperties.Rotation.R0)
                    variants[idx] = variants[idx].with(VariantProperties.X_ROT, rotation);

                if (rotationY != VariantProperties.Rotation.R0)
                    variants[idx] = variants[idx].with(VariantProperties.Y_ROT, rotationY);

                idx++;
            }
        }

        generators.acceptBlockState(MultiVariantGenerator.multiVariant(obsidianBlock, variants));
    }

    public void acceptBlockState(BlockStateGenerator blockStateGenerator) {
        this.vanillaGenerator.blockStateOutput.accept(blockStateGenerator);
    }

    public void acceptModelOutput(ResourceLocation id, Supplier<JsonElement> supplier) {
        this.vanillaGenerator.modelOutput.accept(id, supplier);
    }

    public void delegateItemModel(Block block) {
        this.vanillaGenerator.delegateItemModel(block, TextureMapping.getBlockTexture(block));
    }

    public void delegateItemModel(Block block, ResourceLocation resourceLocation) {
        this.vanillaGenerator.delegateItemModel(block, resourceLocation);
    }

    public TexturedModel getTextureModels(Block block, TexturedModel defaultModel) {
        return vanillaGenerator.texturedModels.getOrDefault(block, defaultModel);
    }

    public Builder modelFor(Block block) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return modelFor(texturedModel);
    }

    public Builder modelFor(TexturedModel texturedModel) {
        return new Builder(texturedModel, texturedModel.getMapping());
    }

    public Builder modelFor(TexturedModel texturedModel, TextureMapping textureMapping) {
        return new Builder(texturedModel, textureMapping);
    }

    public Builder modelFor(Block block, TextureMapping textureMappingOverride) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return new Builder(texturedModel, textureMappingOverride);
    }

    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA
    ) {
        return new TextureMapping().put(slotA, locationA);
    }

    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA,
            TextureSlot slotB,
            ResourceLocation locationB
    ) {
        return textureMappingOf(slotA, locationA).put(slotB, locationB);
    }

    public void createBookshelf(Block shelf, Block planks) {
        TextureMapping textureMapping = TextureMapping.column(TextureMapping.getBlockTexture(shelf), TextureMapping.getBlockTexture(planks));
        ResourceLocation resourceLocation = ModelTemplates.CUBE_COLUMN.create(shelf, textureMapping, vanillaGenerator.modelOutput);
        acceptBlockState(vanillaGenerator.createSimpleBlock(shelf, resourceLocation));
    }

    public void createLadder(Block ladderBlock) {
        vanillaGenerator.createNonTemplateHorizontalBlock(ladderBlock);
        vanillaGenerator.createSimpleFlatItemModel(ladderBlock);
    }

    private final Map<ResourceLocation, ResourceLocation> PARTICLE_ONLY_MODELS = Maps.newHashMap();

    public ResourceLocation particleOnlyModel(Block block) {
        var name = ModelLocationUtils.getModelLocation(block).withSuffix("_particles");
        if (name.getNamespace().equals("minecraft")) name = LibWoverBlock.C.mk(name.getPath());
        ResourceLocation finalName = name;
        return PARTICLE_ONLY_MODELS.computeIfAbsent(name, (n) -> ModelTemplates.PARTICLE_ONLY.create(
                finalName,
                new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block)),
                vanillaGenerator.modelOutput
        ));
    }

    public void createSign(Block baseBlock, Block signBlock, Block wallSignBlock) {
        final ResourceLocation particleLocation = particleOnlyModel(baseBlock);

        acceptBlockState(BlockModelGenerators.createSimpleBlock(signBlock, particleLocation));
        acceptBlockState(BlockModelGenerators.createSimpleBlock(wallSignBlock, particleLocation));

        vanillaGenerator.createSimpleFlatItemModel(signBlock.asItem());
        vanillaGenerator.skipAutoItemBlock(wallSignBlock);
    }

    public void createHangingSign(Block baseBlock, Block hangingSignBlock, Block wallHangingSignBlock) {
        ResourceLocation resourceLocation = particleOnlyModel(baseBlock);
        acceptBlockState(vanillaGenerator.createSimpleBlock(hangingSignBlock, resourceLocation));
        acceptBlockState(vanillaGenerator.createSimpleBlock(wallHangingSignBlock, resourceLocation));
        vanillaGenerator.createSimpleFlatItemModel(hangingSignBlock.asItem());
        vanillaGenerator.skipAutoItemBlock(wallHangingSignBlock);
    }

    public void createBarrel(Block barrelBlock) {
        ResourceLocation resourceLocation = TextureMapping.getBlockTexture(barrelBlock, "_top_open");
        acceptBlockState(MultiVariantGenerator
                .multiVariant(barrelBlock)
                .with(vanillaGenerator.createColumnWithFacing())
                .with(PropertyDispatch
                        .property(BlockStateProperties.OPEN)
                        .select(false, Variant
                                .variant()
                                .with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM.create(barrelBlock, this.vanillaGenerator.modelOutput))
                        )
                        .select(true, Variant
                                .variant()
                                .with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM
                                        .get(barrelBlock)
                                        .updateTextures((textureMapping) -> {
                                            textureMapping.put(TextureSlot.TOP, resourceLocation);
                                        })
                                        .createWithSuffix(barrelBlock, "_open", this.vanillaGenerator.modelOutput)
                                )
                        )
                )
        );
    }

    public void createComposter(Block composterBlock) {
        var mapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(composterBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(composterBlock, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(composterBlock, "_bottom"));
        var location = COMPOSTER_MODEL.create(composterBlock, mapping, vanillaGenerator.modelOutput);
        acceptBlockState(MultiPartGenerator
                .multiPart(composterBlock)
                .with(Variant.variant().with(VariantProperties.MODEL, location))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents1")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents2")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents3")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents4")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents5")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents6")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents7")))
                .with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8), Variant
                        .variant()
                        .with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))));
    }

    public void createBlockTopSideBottom(Block bottomBlock, Block coverBlock, boolean withVariants) {
        var mapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(coverBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(coverBlock, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(bottomBlock));
        var location = ModelTemplates.CUBE_BOTTOM_TOP.create(coverBlock, mapping, vanillaGenerator.modelOutput);

        if (withVariants) acceptBlockState(randomTopModelVariant(coverBlock, location));
        else acceptBlockState(BlockModelGenerators.createSimpleBlock(coverBlock, location));
    }

    public void createCubeModel(Block block) {
        final var model = TexturedModel.CUBE.get(block);
        final TextureMapping mapping = this.getTextureModels(block, model).getMapping();
        final var location = model.getTemplate().create(block, mapping, vanillaGenerator.modelOutput);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(block, location));
    }

    public void createPressurePlate(Block plateBlock, ResourceLocation textureLocation) {
        createPressurePlate(plateBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createPressurePlate(Block materialBlock, Block plateBlock) {
        createPressurePlate(plateBlock, this
                .getTextureModels(plateBlock, TexturedModel.CUBE.get(materialBlock))
                .getMapping());
    }

    private void createPressurePlate(Block plateBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.PRESSURE_PLATE_UP,
                ModelTemplates.PRESSURE_PLATE_DOWN
        ).map(template -> template.create(plateBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createPressurePlate(plateBlock, locations.get(0), locations.get(1)));
    }


    public void createButton(Block buttonBlock, ResourceLocation textureLocation) {
        createButton(buttonBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createButton(Block materialBlock, Block buttonBlock) {
        createButton(buttonBlock, this
                .getTextureModels(buttonBlock, TexturedModel.CUBE.get(materialBlock))
                .getMapping());
    }

    private void createButton(Block buttonBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.BUTTON,
                ModelTemplates.BUTTON_PRESSED
        ).map(template -> template.create(buttonBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createButton(buttonBlock, locations.get(0), locations.get(1)));
        createItemModel(buttonBlock, ModelTemplates.BUTTON_INVENTORY, mapping);
    }

    public void createFence(Block fenceBlock, ResourceLocation textureLocation) {
        createFence(fenceBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createFence(Block materialBlock, Block fenceBlock) {
        createFence(fenceBlock, this
                .getTextureModels(fenceBlock, TexturedModel.CUBE.get(materialBlock))
                .getMapping());
    }

    public void createFence(Block fenceBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.FENCE_POST,
                ModelTemplates.FENCE_SIDE
        ).map(template -> template.create(fenceBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createFence(fenceBlock, locations.get(0), locations.get(1)));
        createInventoryModel(fenceBlock, ModelTemplates.FENCE_INVENTORY, mapping);
    }

    public void createFenceGate(Block gateBlock, ResourceLocation textureLocation) {
        createFence(gateBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createFenceGate(Block materialBlock, Block gateBlock) {
        createFenceGate(gateBlock, this
                .getTextureModels(gateBlock, TexturedModel.CUBE.get(materialBlock))
                .getMapping());
    }

    public void createFenceGate(Block gateBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.FENCE_GATE_OPEN,
                ModelTemplates.FENCE_GATE_CLOSED,
                ModelTemplates.FENCE_GATE_WALL_OPEN,
                ModelTemplates.FENCE_GATE_WALL_CLOSED
        ).map(template -> template.create(gateBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createFenceGate(gateBlock, locations.get(0), locations.get(1), locations.get(2), locations.get(3), true));
    }

    public void createStairs(
            Block stairBlock,
            ResourceLocation topTextureLocation,
            ResourceLocation sideTextureLocation,
            ResourceLocation bottomTextureLocation
    ) {
        createStairs(stairBlock, new TextureMapping()
                .put(TextureSlot.TOP, topTextureLocation)
                .put(TextureSlot.SIDE, sideTextureLocation)
                .put(TextureSlot.BOTTOM, bottomTextureLocation));
    }

    public void createStairs(Block materialBlock, Block stairBlock) {
        createStairs(stairBlock, this
                .getTextureModels(stairBlock, TexturedModel.CUBE.get(materialBlock))
                .getMapping());
    }

    public void createStairsWithModels(
            Block stairBlock,
            ResourceLocation stair,
            ResourceLocation outer,
            ResourceLocation inner
    ) {
        acceptBlockState(BlockModelGenerators.createStairs(stairBlock, inner, stair, outer));
        delegateItemModel(stairBlock, stair);
    }

    public void createStairs(Block stairBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream
                .of(
                        ModelTemplates.STAIRS_INNER,
                        ModelTemplates.STAIRS_STRAIGHT,
                        ModelTemplates.STAIRS_OUTER
                )
                .map(template -> template.create(stairBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createStairs(stairBlock, locations.get(0), locations.get(1), locations.get(2)));
        delegateItemModel(stairBlock, locations.get(1));
    }

    public void createWall(Block materialBlock, Block wallBlock) {
        createWall(wallBlock, this
                .getTextureModels(wallBlock, TexturedModel.CUBE.get(materialBlock))
                .getMapping());
    }

    public void createWall(Block wallBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.WALL_POST,
                ModelTemplates.WALL_LOW_SIDE,
                ModelTemplates.WALL_TALL_SIDE
        ).map(template -> template.create(wallBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createWall(wallBlock, locations.get(0), locations.get(1), locations.get(2)));
        createInventoryModel(wallBlock, ModelTemplates.WALL_INVENTORY, mapping);
    }

    public void createSlab(Block slabBlock, Block baseBlock) {
        var res = TextureMapping.getBlockTexture(baseBlock);
        createSlab(slabBlock, baseBlock, new TextureMapping()
                .put(TextureSlot.SIDE, res)
                .put(TextureSlot.BOTTOM, res)
                .put(TextureSlot.TOP, res));
    }

    public void createSlab(Block slabBlock, Block baseBlock, TextureMapping mapping) {
        final var fullBlockLocation = ModelLocationUtils.getModelLocation(baseBlock);
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.SLAB_BOTTOM,
                ModelTemplates.SLAB_TOP
        ).map(template -> template.create(slabBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createSlab(slabBlock, locations.get(0), locations.get(1), fullBlockLocation));
        delegateItemModel(slabBlock, locations.get(0));
    }

    public void createRotatedPillar(Block pillarBlock) {
        var res = TextureMapping.getBlockTexture(pillarBlock);
        createRotatedPillar(pillarBlock, new TextureMapping()
                .put(TextureSlot.SIDE, res.withSuffix("_side"))
                .put(TextureSlot.END, res.withSuffix("_top")));
    }

    public void createRotatedPillar(Block pillarBlock, TextureMapping mapping) {
        final var model = ModelTemplates.CUBE_COLUMN.create(pillarBlock, mapping, vanillaGenerator.modelOutput);

        acceptBlockState(BlockModelGenerators.createAxisAlignedPillarBlock(pillarBlock, model));
    }

    private void createInventoryModel(Block wallBlock, ModelTemplate inventoryModel, TextureMapping mapping) {
        delegateItemModel(wallBlock, inventoryModel.create(wallBlock, mapping, vanillaGenerator.modelOutput));
    }


    public void createChest(Block materialBlock, Block chestBlock) {
        final var baseModel = particleOnlyModel(materialBlock);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(chestBlock, baseModel));
    }

    public final void createItemModel(Block block, ModelTemplate template, TextureMapping mapping) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            template.create(ModelLocationUtils.getModelLocation(item), mapping, vanillaGenerator.modelOutput);
        }
        vanillaGenerator.skipAutoItemBlock(block);
    }

    public void createFlatItem(Block block) {
        vanillaGenerator.createSimpleFlatItemModel(block);
        vanillaGenerator.skipAutoItemBlock(block);
    }


    public void createWallItem(Block block, ResourceLocation textureLocation) {
        createInventoryModel(block, ModelTemplates.WALL_INVENTORY, new TextureMapping().put(TextureSlot.WALL, textureLocation));
        vanillaGenerator.skipAutoItemBlock(block);
    }

    public void createFlatItem(Block block, @Nullable ResourceLocation itemLocation) {
        if (itemLocation == null) {
            this.createFlatItem(block);
            return;
        }
        final var item = block.asItem();
        if (item != Items.AIR) {
            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(itemLocation), vanillaGenerator.modelOutput);
        }
        vanillaGenerator.skipAutoItemBlock(block);
    }

    public static MultiVariantGenerator randomTopModelVariant(Block block, ResourceLocation model) {
        return MultiVariantGenerator
                .multiVariant(
                        block,
                        Variant
                                .variant()
                                .with(VariantProperties.MODEL, model)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0)
                                .with(VariantProperties.UV_LOCK, false)
                                .with(VariantProperties.WEIGHT, 1),
                        Variant
                                .variant()
                                .with(VariantProperties.MODEL, model)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.UV_LOCK, false)
                                .with(VariantProperties.WEIGHT, 1),
                        Variant
                                .variant()
                                .with(VariantProperties.MODEL, model)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                .with(VariantProperties.UV_LOCK, false)
                                .with(VariantProperties.WEIGHT, 1),
                        Variant
                                .variant()
                                .with(VariantProperties.MODEL, model)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.UV_LOCK, false)
                                .with(VariantProperties.WEIGHT, 1)
                );
    }

    public BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput() {
        return vanillaGenerator.modelOutput;
    }

    public class Builder {
        private ResourceLocation fullBlockLocation;
        private final TexturedModel model;
        private final TextureMapping mapping;
        private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();

        private Builder(TexturedModel model, TextureMapping mapping) {
            this.model = model;
            this.mapping = mapping;
        }

        public Builder createFullBlock(Block fullBlock) {
            this.fullBlockLocation = model
                    .getTemplate()
                    .create(fullBlock, mapping, vanillaGenerator.modelOutput);

            acceptBlockState(
                    BlockModelGenerators.createSimpleBlock(
                            fullBlock,
                            fullBlockLocation
                    )
            );

            return this;
        }

        public Builder createDoor(Block doorBlock) {
            vanillaGenerator.createDoor(doorBlock);
            return this;
        }

        public Builder createCustomFence(Block fenceBlock) {
            final TextureMapping particles = TextureMapping.customParticle(fenceBlock);

            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.CUSTOM_FENCE_POST,
                    ModelTemplates.CUSTOM_FENCE_SIDE_NORTH,
                    ModelTemplates.CUSTOM_FENCE_SIDE_EAST,
                    ModelTemplates.CUSTOM_FENCE_SIDE_SOUTH,
                    ModelTemplates.CUSTOM_FENCE_SIDE_WEST
            ).map(template -> template.create(fenceBlock, particles, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createCustomFence(fenceBlock, locations.get(0), locations.get(1), locations.get(2), locations.get(3), locations.get(4)));
            createInventoryModel(fenceBlock, ModelTemplates.CUSTOM_FENCE_INVENTORY, particles);

            return this;
        }

        public Builder createCustomFenceGate(Block gateBlock) {
            final TextureMapping particles = TextureMapping.customParticle(gateBlock);

            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.CUSTOM_FENCE_GATE_OPEN,
                    ModelTemplates.CUSTOM_FENCE_GATE_CLOSED,
                    ModelTemplates.CUSTOM_FENCE_GATE_WALL_OPEN,
                    ModelTemplates.CUSTOM_FENCE_GATE_WALL_CLOSED
            ).map(template -> template.create(gateBlock, particles, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createFenceGate(gateBlock, locations.get(0), locations.get(1), locations.get(2), locations.get(3), false));

            return this;
        }


        private Builder createFullBlockVariant(Block block) {
            final TexturedModel texturedModel = getTextureModels(block, TexturedModel.CUBE.get(block));
            final ResourceLocation resourceLocation = texturedModel.create(block, vanillaGenerator.modelOutput);

            acceptBlockState(BlockModelGenerators.createSimpleBlock(block, resourceLocation));

            return this;
        }

        private void createTrapdoor(Block block, boolean hasOrientation) {
            if (!hasOrientation) {
                vanillaGenerator.createTrapdoor(block);
            } else {
                vanillaGenerator.createOrientableTrapdoor(block);
            }
        }

        public Builder createSlab(Block slabBlock) {
            if (this.fullBlockLocation == null) {
                throw new IllegalStateException("Please call createFullBlock before calling createSlab");
            } else {
                final List<ResourceLocation> locations = Stream.of(
                        ModelTemplates.SLAB_BOTTOM,
                        ModelTemplates.SLAB_TOP
                ).map(template -> this.computeModelIfAbsent(template, slabBlock)).toList();

                acceptBlockState(BlockModelGenerators.createSlab(slabBlock, locations.get(0), locations.get(1), this.fullBlockLocation));
                delegateItemModel(slabBlock, locations.get(0));

                return this;
            }
        }

        private ResourceLocation computeModelIfAbsent(ModelTemplate modelTemplate, Block block) {
            return this.models.computeIfAbsent(modelTemplate, (m) -> m.create(block, this.mapping, vanillaGenerator.modelOutput));
        }

        private void createInventoryModel(Block wallBlock, ModelTemplate inventoryModel, TextureMapping mapping) {
            delegateItemModel(wallBlock, inventoryModel.create(wallBlock, mapping, vanillaGenerator.modelOutput));
        }
    }


}
