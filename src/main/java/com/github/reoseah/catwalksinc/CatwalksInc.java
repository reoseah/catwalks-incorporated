package com.github.reoseah.catwalksinc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

/**
 * FIXME Add loot tables for everything
 * 
 * TODO Make mod icon, description
 * 
 * TODO Add REI info to all complex items
 * 
 * TODO Add industrial lamps (RedPower style)
 * 
 * TODO Add Crankwheel (lever with 16 states)
 * 
 * TODO Add sound to using wrench
 * 
 * TODO Add industrial ladders
 * 
 * FIXME Fix forced handrails desyncing after rejoining world
 * 
 * @author reoseah
 *
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
	}
}
