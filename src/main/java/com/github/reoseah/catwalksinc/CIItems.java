package com.github.reoseah.catwalksinc;

import static com.github.reoseah.catwalksinc.CatwalksInc.id;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class CIItems {
	public static final ItemGroup ITEMGROUP = FabricItemGroupBuilder.build(id("main"),
			() -> new ItemStack(CIItems.CATWALK));

	public static final Item CATWALK = registerBlockItem(CIBlocks.CATWALK);

	private static Item registerBlockItem(Block block) {
		return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, defaultSettings()));
	}

	private static Item.Settings defaultSettings() {
		return new Item.Settings().group(ITEMGROUP);
	}
}
