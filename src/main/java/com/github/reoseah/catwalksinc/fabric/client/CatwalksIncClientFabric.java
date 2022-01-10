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
        CatwalksIncClient.init(FabricModelPredicateProviderRegistry::register);

        WorldRenderEvents.BLOCK_OUTLINE.register(new CIncBlockOutline());
    }
}
