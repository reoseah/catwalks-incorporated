package io.github.reoseah.catwalksinc;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CatwalksInc {
    public static final String MOD_ID = "assets/catwalksinc";

    public static final Block CATWALK = new CatwalkBlock(AbstractBlock.Settings.create());

    public static void initialize(Registrar registrar) {
        registrar.register(Registries.BLOCK, "catwalk", CATWALK);
        registrar.register(Registries.ITEM, "catwalk", new BlockItem(CATWALK, new Item.Settings()));
    }

    public interface Registrar {
        <T> void register(Registry<T> registry, String name, T value);
    }
}
