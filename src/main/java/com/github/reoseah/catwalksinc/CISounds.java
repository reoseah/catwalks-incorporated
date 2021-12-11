package com.github.reoseah.catwalksinc;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CISounds {
	public static final SoundEvent WRENCH_USE = register("wrench_use");

	private static SoundEvent register(String name) {
		Identifier id = CatwalksInc.id(name);
		return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
	}
}
