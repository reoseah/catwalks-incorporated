package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.block.*;
import com.github.reoseah.catwalksinc.item.WrenchItem;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class CatwalksInc implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "catwalksinc";

    public static final AbstractBlock.Settings CATWALK_SETTINGS = AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque();
    public static final Block CATWALK = new CatwalkBlock(CATWALK_SETTINGS);
    public static final Block GRATE_CATWALK = new CatwalkBlock(CATWALK_SETTINGS);
    public static final Block CATWALK_STAIRS = new CatwalkStairsBlock(CATWALK_SETTINGS);
    public static final Block CRANK_WHEEL = new CrankWheelBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque());
    public static final Block CAGE_LAMP = new CageLampBlock(AbstractBlock.Settings.create().luminance(state -> 14).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque());

    public static final BlockEntityType<?> CATWALK_BE = BlockEntityType.Builder.create(CatwalkBlockEntity::new, CATWALK).build(null);
    public static final BlockEntityType<?> CATWALK_STAIRS_BE = BlockEntityType.Builder.create(CatwalkStairsBlockEntity::new, CATWALK_STAIRS).build(null);

    public static final Item WRENCH = new WrenchItem(new Item.Settings().maxDamage(255));

    public static final TagKey<Item> WRENCHES = TagKey.of(RegistryKeys.ITEM, new Identifier("c:wrenches"));

    public static final SoundEvent WRENCH_USE = SoundEvent.of(new Identifier("catwalksinc:wrench_use"));

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, "catwalksinc:catwalk", CATWALK);
        Registry.register(Registries.BLOCK, "catwalksinc:grate_catwalk", GRATE_CATWALK);
        Registry.register(Registries.BLOCK, "catwalksinc:catwalk_stairs", CATWALK_STAIRS);
        Registry.register(Registries.BLOCK, "catwalksinc:crank_wheel", CRANK_WHEEL);
        Registry.register(Registries.BLOCK, "catwalksinc:cage_lamp", CAGE_LAMP);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, "catwalksinc:catwalk", CATWALK_BE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, "catwalksinc:catwalk_stairs", CATWALK_STAIRS_BE);

        Registry.register(Registries.ITEM, "catwalksinc:catwalk", new BlockItem(CATWALK, new Item.Settings()));
        Registry.register(Registries.ITEM, "catwalksinc:grate_catwalk", new BlockItem(GRATE_CATWALK, new Item.Settings()));
        Registry.register(Registries.ITEM, "catwalksinc:crank_wheel", new BlockItem(CRANK_WHEEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "catwalksinc:cage_lamp", new BlockItem(CAGE_LAMP, new Item.Settings()));

        Registry.register(Registries.ITEM, "catwalksinc:wrench", WRENCH);

        appendToGroup("building_blocks", CATWALK);
        appendToGroup("building_blocks", GRATE_CATWALK);
        appendToGroup("building_blocks", CRANK_WHEEL);
        appendToGroup("building_blocks", CAGE_LAMP);
        appendToGroup("redstone_blocks", CRANK_WHEEL);
        appendToGroup("tools_and_utilities", WRENCH);
    }

    private void appendToGroup(String group, ItemConvertible... items) {
        ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(group)))
                .register(entries -> {
                    for (ItemConvertible item : items) {
                        entries.add(item);
                    }
                });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(CatwalksInc.CATWALK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CatwalksInc.GRATE_CATWALK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CatwalksInc.CATWALK_STAIRS, RenderLayer.getCutoutMipped());
//        BlockRenderLayerMap.INSTANCE.putBlock(CagedLadderBlock.INSTANCE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CatwalksInc.CAGE_LAMP, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CatwalksInc.CRANK_WHEEL, RenderLayer.getCutoutMipped());

        WorldRenderEvents.BLOCK_OUTLINE.register((worldCtx, blockCtx) -> {
            BlockState state = blockCtx.blockState();
            BlockPos pos = blockCtx.blockPos();
            MatrixStack matrices = worldCtx.matrixStack();
            VertexConsumerProvider vertexConsumers = worldCtx.consumers();
            double cameraX = blockCtx.cameraX();
            double cameraY = blockCtx.cameraY();
            double cameraZ = blockCtx.cameraZ();

            renderWrenchHelpers(state, pos, matrices, vertexConsumers, cameraX, cameraY, cameraZ);

            return true;
        });
    }

    private static void renderWrenchHelpers(BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        ItemStack stack = MinecraftClient.getInstance().player.getOffHandStack();
        if (stack.isEmpty()) {
            stack = MinecraftClient.getInstance().player.getMainHandStack();
        }
        if (stack.isIn(CatwalksInc.WRENCHES)) {
            Block block = state.getBlock();
            if (block instanceof CatwalkBlock || block instanceof CatwalkStairsBlock) {
                double x = pos.getX() - cameraX;
                double y = pos.getY() - cameraY;
                double z = pos.getZ() - cameraZ;

                matrices.push();
                matrices.translate(x, y, z);

                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

                if (block instanceof CatwalkBlock) {
                    drawLine(matrices, vertexConsumer, 0, 0.0625F, 0, 1, 0.0625F, 1);
                    drawLine(matrices, vertexConsumer, 0, 0.0625F, 1, 1, 0.0625F, 0);
                } else {
                    Direction facing = state.get(CatwalkStairsBlock.FACING);
                    matrices.translate(0.5F, 0, 0.5F);
                    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180 + facing.asRotation()));
                    matrices.translate(-0.5F, 0, -0.5F);

                    if (state.get(CatwalkStairsBlock.HALF) == DoubleBlockHalf.UPPER) {
                        matrices.translate(0, -1, 0);
                    }

                    drawLine(matrices, vertexConsumer, 0.5F, 0.5625F, 0, 0.5F, 0.5625F, 0.5F);
                    drawLine(matrices, vertexConsumer, 0.5F, 1.0625F, 0.5F, 0.5F, 1.0625F, 1);
                }
                matrices.pop();
            }
        }
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2) {
        MatrixStack.Entry entry = matrices.peek();

        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

        vertexConsumer.vertex(entry.getPositionMatrix(), x1, y1, z1).color(0, 0, 0, 0.4F).normal(entry.getNormalMatrix(), dx /= d, dy /= d, dz /= d).next();
        vertexConsumer.vertex(entry.getPositionMatrix(), x2, y2, z2).color(0, 0, 0, 0.4F).normal(entry.getNormalMatrix(), dx, dy, dz).next();
    }
}
