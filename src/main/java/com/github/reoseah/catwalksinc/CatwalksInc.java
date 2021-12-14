package com.github.reoseah.catwalksinc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class CatwalksInc implements ModInitializer {
	public static final String MODID = "catwalksinc";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static Identifier id(String name) {
		return new Identifier(MODID, name);
	}

	@Override
	public void onInitialize() {
		// ensure static fields are initialized
		CIItems.CATWALK.getClass();
		CIBlocks.BlockEntityTypes.CATWALK.getClass();
		CIRecipeSerializers.PAINTROLLER_FILLING.getClass();
		CISounds.WRENCH_USE.getClass();
	}
}
