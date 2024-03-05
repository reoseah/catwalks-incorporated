package io.github.reoseah.catwalksinc;

import io.github.reoseah.catwalksinc.block.CatwalkBlock;
import io.github.reoseah.catwalksinc.block.CatwalkStairsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class CatwalksIncClient {
    public static void initialize(Platform platform) {
        platform.registerRenderLayer(RenderLayer.getCutout(),
                CatwalksInc.CATWALK,
                CatwalksInc.CATWALK_STAIRS,
                CatwalksInc.CRANK_WHEEL);

        platform.registerBlockOutlineHandler(CatwalksIncClient::renderWrenchHelpers);
    }

    public interface Platform {
        void registerRenderLayer(RenderLayer layer, Block... blocks);

        void registerBlockOutlineHandler(OutlineHandler handler);

        interface OutlineHandler {
            void renderOutline(BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ);
        }
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
