package com.github.reoseah.catwalksinc;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CatwalksInc {
    public static final String ID = "catwalksinc";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(CIncItems.IRON_ROD));

    public static Identifier id(String name) {
        return new Identifier(ID, name);
    }

    public static void init() {
        CIncBlocks.CATWALK.getClass();
        CIncBlockEntityTypes.CATWALK.getClass();
        CIncItems.CATWALK.getClass();
        CIncSoundEvents.WRENCH_USE.getClass();
        CIRecipeSerializers.PAINTROLLER_FILLING.getClass();
    }
}
