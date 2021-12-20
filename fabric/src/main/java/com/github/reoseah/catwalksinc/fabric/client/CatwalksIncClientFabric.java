package com.github.reoseah.catwalksinc.fabric.client;

import com.github.reoseah.catwalksinc.CIncItems;
import com.github.reoseah.catwalksinc.block.Wrenchable;
import com.github.reoseah.catwalksinc.client.CatwalksIncClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CatwalksIncClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CatwalksIncClient.init();

        FabricModelPredicateProviderRegistry.register(CIncItems.WRENCH, new Identifier("open"),
                (ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int i) -> {
                    if (entity instanceof PlayerEntity player) {
                        if ((player.getMainHandStack() == stack) || (player.getOffHandStack() == stack)) {
                            @SuppressWarnings("resource")
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

        WorldRenderEvents.BLOCK_OUTLINE.register(new CIncBlockOutline());
    }
}
