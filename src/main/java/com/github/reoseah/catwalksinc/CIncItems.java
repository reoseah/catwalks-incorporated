package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.item.PaintRollerItem;
import com.github.reoseah.catwalksinc.item.PaintScraperItem;
import com.github.reoseah.catwalksinc.item.WrenchItem;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

public class CIncItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(CatwalksInc.ID, Registry.ITEM_KEY);

    public static final Item CATWALK = register("catwalk", CIncBlocks.CATWALK);
    public static final Item INDUSTRIAL_LADDER = register("industrial_ladder", CIncBlocks.INDUSTRIAL_LADDER);
    public static final Item CAGED_LADDER = register("caged_ladder", CIncBlocks.CAGED_LADDER);
    public static final Item CAGE_LAMP = register("cage_lamp", CIncBlocks.CAGE_LAMP);
    public static final Item CRANK_WHEEL = register("crank_wheel", CIncBlocks.CRANK_WHEEL);

    public static final Item IRON_ROD = register("iron_rod", new Item(defaultSettings()));
    public static final Item WRENCH = register("wrench", new WrenchItem(defaultSettings().maxDamage(256)));
    public static final Item PAINT_SCRAPER = register("paint_scraper", new PaintScraperItem(defaultSettings().maxDamage(256)));
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

    public static final Item YELLOW_CATWALK = register("yellow_catwalk", CIncBlocks.YELLOW_CATWALK);
    public static final Item YELLOW_LADDER = register("yellow_ladder", CIncBlocks.YELLOW_LADDER);
    public static final Item YELLOW_CAGED_LADDER = register("yellow_caged_ladder", CIncBlocks.YELLOW_CAGED_LADDER);

    public static final Item RED_CATWALK = register("red_catwalk", CIncBlocks.RED_CATWALK);
    public static final Item RED_LADDER = register("red_ladder", CIncBlocks.RED_LADDER);
    public static final Item RED_CAGED_LADDER = register("red_caged_ladder", CIncBlocks.RED_CAGED_LADDER);

    private static Item register(String name, Item item) {
        REGISTER.register(name, () -> item);
        return item;
    }

    private static Item register(String name, Block block) {
        return register(name, new BlockItem(block, defaultSettings()));
    }

    private static Item.Settings defaultSettings() {
        return new Item.Settings().group(CatwalksInc.ITEM_GROUP);
    }

    private static Item createPaintRoller(DyeColor color) {
        return register(color.getName() + "_paint_roller", new PaintRollerItem(color, defaultSettings().maxCount(1)));
    }
}
