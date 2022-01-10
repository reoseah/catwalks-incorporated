package com.github.reoseah.catwalksinc.client;

import com.github.reoseah.catwalksinc.CIncBlocks;
import com.github.reoseah.catwalksinc.CIncItems;
import com.github.reoseah.catwalksinc.block.Wrenchable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CatwalksIncClient {
    public static void init(TriConsumer<Item, Identifier, UnclampedModelPredicateProvider> modelPredicateRegister) {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
                CIncBlocks.CATWALK, CIncBlocks.CATWALK_STAIRS,
                CIncBlocks.INDUSTRIAL_LADDER, CIncBlocks.CAGED_LADDER,
                CIncBlocks.CAGE_LAMP, CIncBlocks.CRANK_WHEEL,

                CIncBlocks.YELLOW_CATWALK, CIncBlocks.YELLOW_CATWALK_STAIRS,
                CIncBlocks.YELLOW_LADDER, CIncBlocks.YELLOW_CAGED_LADDER,

                CIncBlocks.RED_CATWALK, CIncBlocks.RED_CATWALK_STAIRS,
                CIncBlocks.RED_LADDER, CIncBlocks.RED_CAGED_LADDER);

        modelPredicateRegister.accept(CIncItems.WRENCH, new Identifier("open"),
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
    }
}
