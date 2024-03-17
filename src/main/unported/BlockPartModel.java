package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.render.PartModelBaker;
import alexiil.mc.lib.multipart.api.render.PartRenderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BlockPartModel implements PartModelBaker<BlockModelKey> {
    private final Map<BlockState, BakedModel> bakedModels = new HashMap<>();

    private BakedModel getOrCreateModel(BlockState state) {
        if (!this.bakedModels.containsKey(state)) {
            this.bakedModels.put(state, new PartBakedModel(state));
        }
        return this.bakedModels.get(state);
    }

    @Override
    public void emitQuads(BlockModelKey key, PartRenderContext ctx) {
        BakedModel model = this.getOrCreateModel(key.getBlockState());
        if (model != null) {
            ctx.fallbackConsumer().accept(model);
        }
    }

    private static class PartBakedModel extends ForwardingBakedModel {
        private final BlockState state;

        public PartBakedModel(BlockState state) {
            this.state = state;
            this.wrapped = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state);
        }

        @Override
        public List<BakedQuad> getQuads(BlockState multipartState, Direction face, Random random) {
            return super.getQuads(this.state, face, random);
        }
    }
}
