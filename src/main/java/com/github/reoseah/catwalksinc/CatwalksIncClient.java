package com.github.reoseah.catwalksinc;

import org.jetbrains.annotations.Nullable;

import com.github.reoseah.catwalksinc.blocks.Wrenchable;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class CatwalksIncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), //
				CIBlocks.CATWALK, CIBlocks.CATWALK_STAIRS, //
				CIBlocks.CAGE_LAMP, //
				CIBlocks.INDUSTRIAL_LADDER, CIBlocks.CAGED_LADDER //
		);

		FabricModelPredicateProviderRegistry.register(CIItems.WRENCH, new Identifier("open"),
				(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int i) -> {
					if (entity instanceof PlayerEntity player) {
						if (player.getMainHandStack() == stack || player.getOffHandStack() == stack) {
							float reachDistance = MinecraftClient.getInstance().interactionManager.getReachDistance();
							HitResult hit = player.raycast(reachDistance, 0, false);
							if (hit instanceof BlockHitResult blockhit) {
								BlockPos pos = blockhit.getBlockPos();
								if (entity.getEntityWorld().getBlockState(pos).getBlock() instanceof Wrenchable) {
									return 1;
								}
							}
						}
					}
					return 0;
				});
	}

}
