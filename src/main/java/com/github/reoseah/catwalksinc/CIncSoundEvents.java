package com.github.reoseah.catwalksinc;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class CIncSoundEvents {
    public static final SoundEvent WRENCH_USE = register("wrench_use");
    public static final SoundEvent CRANK_WHEEL_USE = register("crank_wheel_use");

    public static SoundEvent register(String name) {
        SoundEvent event = new SoundEvent(CatwalksInc.id(name));
        return Registry.register(Registry.SOUND_EVENT, CatwalksInc.id(name), event);
    }
}
