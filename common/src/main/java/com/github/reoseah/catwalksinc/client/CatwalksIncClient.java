package com.github.reoseah.catwalksinc.client;

import com.github.reoseah.catwalksinc.CIncBlocks;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class CatwalksIncClient {
    public static void init() {
        RenderTypeRegistry.register(RenderLayer.getCutoutMipped(),
                CIncBlocks.CATWALK, CIncBlocks.CATWALK_STAIRS,
                CIncBlocks.INDUSTRIAL_LADDER, CIncBlocks.CAGED_LADDER,
                CIncBlocks.CAGE_LAMP, CIncBlocks.CRANK_WHEEL,

                CIncBlocks.YELLOW_CATWALK, CIncBlocks.YELLOW_CATWALK_STAIRS,
                CIncBlocks.YELLOW_LADDER, CIncBlocks.YELLOW_CAGED_LADDER,

                CIncBlocks.RED_CATWALK, CIncBlocks.RED_CATWALK_STAIRS,
                CIncBlocks.RED_LADDER, CIncBlocks.RED_CAGED_LADDER);
    }
}
