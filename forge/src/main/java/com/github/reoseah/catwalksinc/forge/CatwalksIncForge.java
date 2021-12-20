package com.github.reoseah.catwalksinc.forge;

import com.github.reoseah.catwalksinc.CatwalksInc;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CatwalksInc.ID)
public class CatwalksIncForge {
    public CatwalksIncForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CatwalksInc.ID, FMLJavaModLoadingContext.get().getModEventBus());
        CatwalksInc.init();
    }
}
