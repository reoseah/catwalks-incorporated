package com.github.reoseah.catwalksinc.fabric.client;

import com.github.reoseah.catwalksinc.CIncItems;
import com.github.reoseah.catwalksinc.block.CatwalkBlock;
import com.github.reoseah.catwalksinc.block.CatwalkStairsBlock;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BlockOutline;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public final class CIncBlockOutline implements BlockOutline {
	@SuppressWarnings("resource")
	@Override
	public boolean onBlockOutline(WorldRenderContext wrc, BlockOutlineContext boc) {
		BlockState state = boc.blockState();
		Block block = state.getBlock();
		if (MinecraftClient.getInstance().player.getMainHandStack().getItem() == CIncItems.WRENCH //
				&& (block instanceof CatwalkBlock || block instanceof CatwalkStairsBlock)) {
			wrc.matrixStack().push();

			BlockPos pos = boc.blockPos();
			double x = pos.getX() - boc.cameraX();
			double y = pos.getY() - boc.cameraY();
			double z = pos.getZ() - boc.cameraZ();

			wrc.matrixStack().translate(x, y, z);

			VertexConsumer lines = wrc.consumers().getBuffer(RenderLayer.getLines());

			if (block instanceof CatwalkBlock) {
				drawLine(wrc.matrixStack(), lines, 0, 1 / 16F, 0, 1, 1 / 16F, 1);
				drawLine(wrc.matrixStack(), lines, 0, 1 / 16F, 1, 1, 1 / 16F, 0);
			} else if (block instanceof CatwalkStairsBlock) {
				Direction facing = state.get(CatwalkStairsBlock.FACING);

				wrc.matrixStack().translate(0.5F, 0, 0.5F);
				wrc.matrixStack().multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(180 + facing.asRotation()));
				wrc.matrixStack().translate(-0.5F, 0, -0.5F);

				if (state.get(CatwalkStairsBlock.HALF) == DoubleBlockHalf.UPPER) {
					wrc.matrixStack().translate(0, -1, 0);
				}

				drawLine(wrc.matrixStack(), lines, 0.5F, 9 / 16F, 0, 0.5F, 9 / 16F, 0.5F);
				drawLine(wrc.matrixStack(), lines, 0.5F, 17 / 16F, 0.5F, 0.5F, 17 / 16F, 1);
			}
			wrc.matrixStack().pop();
		}
		return true;
	}

	private static void drawLine(MatrixStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float z1,
			float x2, float y2, float z2) {
		MatrixStack.Entry entry = matrices.peek();

		float dx = x2 - x1;
		float dy = y2 - y1;
		float dz = z2 - z1;
		float d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

		vertexConsumer.vertex(entry.getPositionMatrix(), x1, y1, z1).color(0, 0, 0, 0.4F)
				.normal(entry.getNormalMatrix(), dx /= d, dy /= d, dz /= d).next();
		vertexConsumer.vertex(entry.getPositionMatrix(), x2, y2, z2).color(0, 0, 0, 0.4F)
				.normal(entry.getNormalMatrix(), dx, dy, dz).next();
	}
}