package com.github.reoseah.catwalksinc.fabric;

import com.github.reoseah.catwalksinc.client.CatwalksIncClient;
import net.fabricmc.api.ClientModInitializer;

public class CatwalksIncClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CatwalksIncClient.init();
    }
}
