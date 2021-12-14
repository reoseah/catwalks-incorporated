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
	public static final Item INDUSTRIAL_LADDER = registerBlockItem(CIBlocks.INDUSTRIAL_LADDER);
	public static final Item CAGED_LADDER = registerBlockItem(CIBlocks.CAGED_LADDER);
	public static final Item CAGE_LAMP = registerBlockItem(CIBlocks.CAGE_LAMP);
	public static final Item CRANK_WHEEL = registerBlockItem(CIBlocks.CRANK_WHEEL);

	public static final Item YELLOW_CATWALK = registerBlockItem(CIBlocks.YELLOW_CATWALK);
	public static final Item YELLOW_LADDER = registerBlockItem(CIBlocks.YELLOW_LADDER);
	public static final Item YELLOW_CAGED_LADDER = registerBlockItem(CIBlocks.YELLOW_CAGED_LADDER);

	public static final Item IRON_ROD = register("iron_rod", new Item(defaultSettings()));
	public static final Item WRENCH = register("wrench", new WrenchItem(defaultSettings().maxDamage(256)));
	public static final Item PAINT_SCRAPER = register("paint_scraper",
			new PaintScraperItem(defaultSettings().maxDamage(256)));
	public static final Item PAINT_ROLLER = register("paint_roller", new Item(defaultSettings().maxCount(16)));
	public static final Item WHITE_PAINT_ROLLER = createPaintRoller(DyeColor.WHITE);
	public static final Item ORANGE_PAINT_ROLLER = createPaintRoller(DyeColor.ORANGE);
	public static final Item MAGENTA_PAINT_ROLLER = createPaintRoller(DyeColor.MAGENTA);
	public static final Item LIGHT_BLUE_PAINT_ROLLER = createPaintRoller(DyeColor.LIGHT_BLUE);
	public static final Item YELLOW_PAINT_ROLLER = createPaintRoller(DyeColor.YELLOW);
	public static final Item LIME_PAINT_ROLLER = createPaintRoller(DyeColor.LIME);
	public static final Item PINK_PAINT_ROLLER = createPaintRoller(DyeColor.PINK);
	public static final Item GRAY_PAINT_ROLLER = createPaintRoller(DyeColor.GRAY);
	public static final Item LIGHT_GRAY_PAINT_ROLLER = createPaintRoller(DyeColor.LIGHT_GRAY);
	public static final Item CYAN_PAINT_ROLLER = createPaintRoller(DyeColor.CYAN);
	public static final Item PURPLE_PAINT_ROLLER = createPaintRoller(DyeColor.PURPLE);
	public static final Item BLUE_PAINT_ROLLER = createPaintRoller(DyeColor.BLUE);
	public static final Item BROWN_PAINT_ROLLER = createPaintRoller(DyeColor.BROWN);
	public static final Item GREEN_PAINT_ROLLER = createPaintRoller(DyeColor.GREEN);
	public static final Item RED_PAINT_ROLLER = createPaintRoller(DyeColor.RED);
	public static final Item BLACK_PAINT_ROLLER = createPaintRoller(DyeColor.BLACK);

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
