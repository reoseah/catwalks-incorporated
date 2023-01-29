package com.github.reoseah.catwalks;

import alexiil.mc.lib.multipart.api.NativeMultipart;
import alexiil.mc.lib.multipart.api.PartDefinition;
import com.github.reoseah.catwalks.block.CageLampBlock;
import com.github.reoseah.catwalks.part.CageLampPart;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class CatwalksInc implements ModInitializer, ClientModInitializer {
    public static final String ID = "catwalks";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(CageLampBlock.ITEM));

    public static Identifier id(String name) {
        return new Identifier(ID, name);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, "catwalks:cage_lamp", CageLampBlock.INSTANCE);
        Registry.register(Registry.ITEM, "catwalks:cage_lamp", CageLampBlock.ITEM);
        PartDefinition.PARTS.put(CageLampPart.DEFINITION.identifier, CageLampPart.DEFINITION);
        NativeMultipart.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) -> CageLampPart.NATIVE_MULTIPART, CageLampBlock.INSTANCE);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), CageLampBlock.INSTANCE);
    }
}
