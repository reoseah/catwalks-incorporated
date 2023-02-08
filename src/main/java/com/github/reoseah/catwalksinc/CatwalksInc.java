package com.github.reoseah.catwalksinc;

import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.lib.multipart.api.NativeMultipart;
import alexiil.mc.lib.multipart.impl.LibMultiPart;
import com.github.reoseah.catwalksinc.block.*;
import com.github.reoseah.catwalksinc.item.WrenchItem;
import com.github.reoseah.catwalksinc.part.CageLampPart;
import com.github.reoseah.catwalksinc.part.CatwalkPart;
import com.github.reoseah.catwalksinc.part.CrankWheelPart;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class CatwalksInc implements ModInitializer, ClientModInitializer {
    public static final String ID = "catwalksinc";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(CatwalkBlock.ITEM));

    public static Identifier id(String name) {
        return new Identifier(ID, name);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, "catwalksinc:catwalk", CatwalkBlock.INSTANCE);
        Registry.register(Registry.ITEM, "catwalksinc:catwalk", CatwalkBlock.ITEM);
        CatwalkPart.DEFINITION.register();

        Registry.register(Registry.BLOCK, "catwalksinc:catwalk_stairs", CatwalkStairsBlock.INSTANCE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, "catwalksinc:catwalk_stairs", CatwalkStairsBlockEntity.TYPE);

        Registry.register(Registry.BLOCK, "catwalksinc:caged_ladder", CagedLadderBlock.INSTANCE);

        Registry.register(Registry.BLOCK, "catwalksinc:cage_lamp", CageLampBlock.INSTANCE);
        Registry.register(Registry.ITEM, "catwalksinc:cage_lamp", CageLampBlock.ITEM);
        CageLampPart.DEFINITION.register();

        Registry.register(Registry.BLOCK, "catwalksinc:crank_wheel", CrankWheelBlock.INSTANCE);
        Registry.register(Registry.ITEM, "catwalksinc:crank_wheel", CrankWheelBlock.ITEM);
        CrankWheelPart.DEFINITION.register();

        Registry.register(Registry.ITEM, "catwalksinc:wrench", WrenchItem.INSTANCE);

        UseBlockCallback.EVENT.register(CatwalksInc::interact);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(CatwalkBlock.INSTANCE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CatwalkStairsBlock.INSTANCE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CagedLadderBlock.INSTANCE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CageLampBlock.INSTANCE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CrankWheelBlock.INSTANCE, RenderLayer.getCutoutMipped());
    }

    private static ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!player.canModifyBlocks()) {
            return ActionResult.PASS;
        }
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockPos pos = hitResult.getBlockPos().offset(hitResult.getSide());
            ItemPlacementContext placementCtx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
            BlockState currentState = world.getBlockState(pos);
            if (currentState.canReplace(placementCtx) && !currentState.isOf(LibMultiPart.BLOCK) && !(currentState.getBlock() instanceof NativeMultipart) && NativeMultipart.LOOKUP.getProvider(currentState.getBlock()) == null) {
                // just place normally
                return ActionResult.PASS;
            }
            // try placing as multipart
            BlockState placementState = block.getPlacementState(placementCtx);
            if (placementState != null && placementState.canPlaceAt(world, pos)) {
                if (block instanceof NativeMultipart nativeMultipart && block instanceof CatwalksIncBlock) {
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
    }
}
