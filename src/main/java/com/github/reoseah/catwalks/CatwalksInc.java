package com.github.reoseah.catwalks;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class CatwalksInc {
    public static final String ID = "catwalks";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(Items.BEDROCK /* FIXME */));

    public static Identifier id(String name) {
        return new Identifier(ID, name);
    }

    public static void init() {
    }
}
