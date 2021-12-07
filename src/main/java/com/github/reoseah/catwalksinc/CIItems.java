package com.github.reoseah.catwalksinc;

import static com.github.reoseah.catwalksinc.CatwalksInc.id;

import com.github.reoseah.catwalksinc.items.PaintRollerItem;
import com.github.reoseah.catwalksinc.items.PaintScraperItem;
import com.github.reoseah.catwalksinc.items.WrenchItem;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

public class CIItems {
	public static final ItemGroup ITEMGROUP = FabricItemGroupBuilder.build(id("main"),
			() -> new ItemStack(CIItems.CATWALK));

	public static final Item CATWALK = registerBlockItem(CIBlocks.CATWALK);
	public static final Item CAGE_LAMP = registerBlockItem(CIBlocks.CAGE_LAMP);
	public static final Item INDUSTRIAL_LADDER = registerBlockItem(CIBlocks.INDUSTRIAL_LADDER);
	public static final Item CAGED_LADDER = registerBlockItem(CIBlocks.CAGED_LADDER);

	public static final Item IRON_ROD = register("iron_rod", new Item(defaultSettings()));
	public static final Item WRENCH = register("wrench", new WrenchItem(defaultSettings().maxDamage(256)));
	public static final Item PAINT_SCRAPER = register("paint_scraper",
			new PaintScraperItem(defaultSettings().maxDamage(256)));
	public static final Item PAINT_ROLLER = register("paint_roller", new Item(defaultSettings().maxCount(16)));
	public static final Item YELLOW_PAINT_ROLLER = createPaintRoller(DyeColor.YELLOW);
	public static final Item YELLOW_CATWALK = registerBlockItem(CIBlocks.YELLOW_CATWALK);
	public static final Item YELLOW_LADDER = registerBlockItem(CIBlocks.YELLOW_LADDER);
	public static final Item YELLOW_CAGED_LADDER = registerBlockItem(CIBlocks.YELLOW_CAGED_LADDER);

	private static Item registerBlockItem(Block block) {
		return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, defaultSettings()));
	}

	private static Item register(String name, Item entry) {
		return Registry.register(Registry.ITEM, id(name), entry);
	}

	private static Item.Settings defaultSettings() {
		return new Item.Settings().group(ITEMGROUP);
	}

	private static Item createPaintRoller(DyeColor color) {
		return register(color.getName() + "_paint_roller", new PaintRollerItem(color, defaultSettings().maxCount(1)));
	}
}
