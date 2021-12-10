package com.github.reoseah.catwalksinc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

/**
 * TODO Add Crankwheel (lever with 16 states)
 *
 * TODO Add sound to using wrench
 *
 * TODO Make arrows/projectiles go through ladders
 *
 * FIXME Fix forced handrails desyncing after rejoining a world
 *
 * TODO disable Iron Rod recipe if Modern Industrialization is present
 *
 * TODO middle clicking catwalk stairs should select normal catwalk instead of doing nothing
 *
 * TODO break Cage Lamp when base is broken
 *
 * TODO refactor paint roller, get rid of SimpleCustomDurabilityItem
 * 
 * TODO add melting recipes for stuff back into iron
 * - possible more efficient recipes if tech mods are available
 */
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
	}
}
