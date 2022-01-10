package com.github.reoseah.catwalksinc;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class CIncSoundEvents {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(CatwalksInc.ID, Registry.SOUND_EVENT_KEY);

    public static final SoundEvent WRENCH_USE = register("wrench_use");
    public static final SoundEvent CRANK_WHEEL_USE = register("crank_wheel_use");

    public static SoundEvent register(String name) {
        SoundEvent sound = new SoundEvent(CatwalksInc.id(name));
        REGISTER.register(name, () -> sound);
        return sound;
    }
}
