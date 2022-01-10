package com.github.reoseah.catwalksinc.fabric;

import com.github.reoseah.catwalksinc.CatwalksInc;

import net.fabricmc.api.ModInitializer;

public class CatwalksIncFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CatwalksInc.init();
    }
}
