package io.github.reoseah.catwalksinc;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

public class CatwalksIncClient {
    public static void initialize(Platform platform) {
        platform.registerRenderLayer(RenderLayer.getCutout(), CatwalksInc.CATWALK, CatwalksInc.CATWALK_STAIRS);
    }

    public interface Platform {
        void registerRenderLayer(RenderLayer layer, Block... blocks);
    }
}
