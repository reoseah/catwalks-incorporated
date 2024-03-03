package io.github.reoseah.catwalksinc.fabric;

import io.github.reoseah.catwalksinc.CatwalksInc;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CatwalksIncFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CatwalksInc.initialize(new CatwalksInc.Registrar() {
            @Override
            public <T> void register(Registry<T> registry, String name, T value) {
                Registry.register(registry, new Identifier("catwalksinc", name), value);
            }
        });
    }
}
