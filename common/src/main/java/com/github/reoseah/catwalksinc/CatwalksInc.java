package com.github.reoseah.catwalksinc;

import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CatwalksInc {
    public static final String ID = "catwalksinc";

    public static final ItemGroup ITEM_GROUP = CreativeTabRegistry.create(id("main"), () -> new ItemStack(CIncItems.IRON_ROD));

    public static Identifier id(String name) {
        return new Identifier(ID, name);
    }

    public static void init() {
        CIncBlocks.REGISTER.register();
        CIncBlockEntityTypes.REGISTER.register();
        CIncItems.REGISTER.register();
        CIncSoundEvents.REGISTER.register();
    }
}
