package com.github.reoseah.catwalksinc;

import static com.github.reoseah.catwalksinc.CatwalksInc.id;

import com.github.reoseah.catwalksinc.blocks.CatwalkBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class CIBlocks {
	public static final Block CATWALK = register("catwalk", new CatwalkBlock(BlockSettings.IRON_SCAFFOLDING));

	private static Block register(String name, Block entry) {
		return Registry.register(Registry.BLOCK, id(name), entry);
	}

	public static class BlockSettings {
		private static final FabricBlockSettings IRON_SCAFFOLDING = FabricBlockSettings
				.of(Material.METAL, MapColor.GRAY) //
				.strength(2F, 10F) //
				.nonOpaque() //
				.sounds(BlockSoundGroup.LANTERN) //
				.breakByTool(FabricToolTags.PICKAXES);

	}
}
