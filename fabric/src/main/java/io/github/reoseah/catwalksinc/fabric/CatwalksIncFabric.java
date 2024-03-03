package io.github.reoseah.catwalksinc.fabric;

import io.github.reoseah.catwalksinc.CatwalksInc;
import io.github.reoseah.catwalksinc.CatwalksIncClient;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class CatwalksIncFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        CatwalksInc.initialize(new CatwalksInc.Registrar() {
            @Override
            public <T> void register(Registry<T> registry, String name, T value) {
                Registry.register(registry, new Identifier("catwalksinc", name), value);
            }

            @Override
            public void appendToGroup(RegistryKey<ItemGroup> group, ItemConvertible... items) {
                ItemGroupEvents.modifyEntriesEvent(group).register(entries -> {
                    for (ItemConvertible item : items) {
                        entries.add(item);
                    }
                });
            }
        });
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient() {
        CatwalksIncClient.initialize(new CatwalksIncClient.Platform() {
            @Override
            public void registerRenderLayer(RenderLayer layer, Block... blocks) {
                BlockRenderLayerMap.INSTANCE.putBlocks(layer, blocks);
            }
        });
    }
}
