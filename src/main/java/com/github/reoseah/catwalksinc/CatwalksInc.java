package com.github.reoseah.catwalksinc;

import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.lib.multipart.api.NativeMultipart;
import com.github.reoseah.catwalksinc.block.CageLampBlock;
import com.github.reoseah.catwalksinc.block.CrankWheelBlock;
import com.github.reoseah.catwalksinc.block.WallDecorationBlock;
import com.github.reoseah.catwalksinc.part.CageLampPart;
import com.github.reoseah.catwalksinc.part.CrankWheelPart;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class CatwalksInc implements ModInitializer, ClientModInitializer {
    public static final String ID = "catwalksinc";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(CageLampBlock.ITEM));

    public static Identifier id(String name) {
        return new Identifier(ID, name);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, "catwalksinc:cage_lamp", CageLampBlock.INSTANCE);
        Registry.register(Registry.ITEM, "catwalksinc:cage_lamp", CageLampBlock.ITEM);
        CageLampPart.DEFINITION.register();

        Registry.register(Registry.BLOCK, "catwalksinc:crank_wheel", CrankWheelBlock.INSTANCE);
        Registry.register(Registry.ITEM, "catwalksinc:crank_wheel", CrankWheelBlock.ITEM);
        CrankWheelPart.DEFINITION.register();

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                BlockPos pos = hitResult.getBlockPos().offset(hitResult.getSide());
                if (block instanceof WallDecorationBlock && block instanceof NativeMultipart nativeMultipart) {
                    BlockState placementState = block.getPlacementState(new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult)));
                    if (placementState != null && placementState.canPlaceAt(world, pos)) {
                        for (MultipartContainer.MultipartCreator creator : nativeMultipart.getMultipartConversion(world, pos, placementState)) {
                            MultipartContainer.PartOffer offer = MultipartUtil.offerNewPart(world, pos, creator);
                            if (offer != null) {
                                if (!world.isClient) {
                                    offer.apply();
                                }
                            }
                        }
                        if (!player.getAbilities().creativeMode) {
                            stack.decrement(1);
                        }
                        BlockSoundGroup sounds = block.getDefaultState().getSoundGroup();
                        world.playSound(player, pos, sounds.getPlaceSound(), SoundCategory.BLOCKS, (sounds.getVolume() + 1f) / 2f, sounds.getPitch() * 0.8f);
                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(CageLampBlock.INSTANCE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CrankWheelBlock.INSTANCE, RenderLayer.getCutoutMipped());
    }
}
