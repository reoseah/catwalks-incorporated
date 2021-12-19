package com.github.reoseah.catwalksinc.forge;

import com.github.reoseah.catwalksinc.CatwalksInc;
import com.github.reoseah.catwalksinc.client.CatwalksIncClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CatwalksInc.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CatwalksIncClientForge {
    @SubscribeEvent
    public static void onInitializeClient(final FMLClientSetupEvent event) {
        CatwalksIncClient.init();
    }
}
