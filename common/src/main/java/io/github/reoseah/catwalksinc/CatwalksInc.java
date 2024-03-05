package io.github.reoseah.catwalksinc;

import io.github.reoseah.catwalksinc.block.CatwalkBlock;
import io.github.reoseah.catwalksinc.block.CatwalkBlockEntity;
import io.github.reoseah.catwalksinc.block.CatwalkStairsBlock;
import io.github.reoseah.catwalksinc.block.CatwalkStairsBlockEntity;
import io.github.reoseah.catwalksinc.item.WrenchItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class CatwalksInc {
    public static final String MOD_ID = "catwalksinc";

    public static final AbstractBlock.Settings CATWALK_SETTINGS = AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque();
    public static final Block CATWALK = new CatwalkBlock(CATWALK_SETTINGS);
    public static final Block CATWALK_STAIRS = new CatwalkStairsBlock(CATWALK_SETTINGS);
    public static final BlockEntityType<?> CATWALK_BE = BlockEntityType.Builder.create(CatwalkBlockEntity::new, CATWALK).build(null);
    public static final BlockEntityType<?> CATWALK_STAIRS_BE = BlockEntityType.Builder.create(CatwalkStairsBlockEntity::new, CATWALK_STAIRS).build(null);

    public static final Item WRENCH = new WrenchItem(new Item.Settings().maxDamage(255));

    public static final TagKey<Item> WRENCHES = TagKey.of(RegistryKeys.ITEM, new Identifier("c:wrenches"));

//    public static final SoundEvent WRENCH_USE = SoundEvent.of(new Identifier("catwalksinc:wrench_use"));

    public static void initialize(Registrar registrar) {
        registrar.register(Registries.BLOCK, "catwalk", CATWALK);
        registrar.register(Registries.BLOCK, "catwalk_stairs", CATWALK_STAIRS);

        registrar.register(Registries.BLOCK_ENTITY_TYPE, "catwalk", CATWALK_BE);
        registrar.register(Registries.BLOCK_ENTITY_TYPE, "catwalk_stairs", CATWALK_STAIRS_BE);

        registrar.register(Registries.ITEM, "catwalk", new BlockItem(CATWALK, new Item.Settings()));

        registrar.register(Registries.ITEM, "wrench", WRENCH);

        registrar.appendToGroup("building_blocks", CATWALK);
        registrar.appendToGroup("tools_and_utilities", WRENCH);
    }

    public interface Registrar {
        <T> void register(Registry<T> registry, String name, T value);

        default void appendToGroup(String id, ItemConvertible... items) {
            this.appendToGroup(RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(id)), items);
        }

        void appendToGroup(RegistryKey<ItemGroup> group, ItemConvertible... items);
    }
}
