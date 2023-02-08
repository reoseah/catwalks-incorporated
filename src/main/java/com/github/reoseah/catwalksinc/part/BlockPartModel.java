package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.render.PartModelBaker;
import alexiil.mc.lib.multipart.api.render.PartRenderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BlockPartModel implements PartModelBaker<BlockModelKey> {
    private final Map<BlockState, BakedModel> bakedModels = new HashMap<>();

    private BakedModel getWrapper(BlockModelKey key) {
        BlockState state = key.getState();

        if (!this.bakedModels.containsKey(state)) {
            this.bakedModels.put(state, new Baked(state));
        }
        return this.bakedModels.get(state);
    }

    @Override
    public void emitQuads(BlockModelKey key, PartRenderContext ctx) {
        ctx.fallbackConsumer().accept(getWrapper(key));
    }

    private static class Baked extends ForwardingBakedModel {
        public Baked(BlockState state) {
            this.wrapped = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state);
        }
    }
}
