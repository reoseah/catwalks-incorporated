package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.block.*;
import dev.architectury.registry.block.BlockProperties;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

public class CIncBlocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(CatwalksInc.ID, Registry.BLOCK_KEY);

    public static final Block CATWALK = register("catwalk", new CatwalkBlock(BlockSettings.IRON_SCAFFOLDING));
    public static final Block CATWALK_STAIRS = register("catwalk_stairs", new CatwalkStairsBlock(BlockSettings.IRON_SCAFFOLDING));
    public static final Block INDUSTRIAL_LADDER = register("industrial_ladder", new MetalLadderBlock(BlockSettings.IRON_SCAFFOLDING));
    public static final Block CAGED_LADDER = register("caged_ladder", new CagedLadderBlock(BlockSettings.IRON_SCAFFOLDING));

    public static final Block CAGE_LAMP = register("cage_lamp", new CageLampBlock(BlockSettings.CAGE_LAMP));
    public static final Block CRANK_WHEEL = register("crank_wheel", new CrankWheelBlock(BlockSettings.IRON_SCAFFOLDING));

    public static final Block YELLOW_CATWALK = register("yellow_catwalk", new CatwalkBlock.PaintedCatwalkBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));
    public static final Block YELLOW_CATWALK_STAIRS = register("yellow_catwalk_stairs", new CatwalkStairsBlock.PaintedCatwalkStairsBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));
    public static final Block YELLOW_LADDER = register("yellow_ladder", new MetalLadderBlock.PaintedLadderBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));
    public static final Block YELLOW_CAGED_LADDER = register("yellow_caged_ladder", new CagedLadderBlock.PaintedCagedLadderBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));

    public static final Block RED_CATWALK = register("red_catwalk", new CatwalkBlock.PaintedCatwalkBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));
    public static final Block RED_CATWALK_STAIRS = register("red_catwalk_stairs", new CatwalkStairsBlock.PaintedCatwalkStairsBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));
    public static final Block RED_LADDER = register("red_ladder", new MetalLadderBlock.PaintedLadderBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));
    public static final Block RED_CAGED_LADDER = register("red_caged_ladder", new CagedLadderBlock.PaintedCagedLadderBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));

    public static Block register(String name, Block block) {
        REGISTER.register(name, () -> block);
        return block;
    }

    public static class BlockSettings {
        private static final AbstractBlock.Settings IRON_SCAFFOLDING = BlockProperties
                .of(Material.METAL, MapColor.GRAY).sounds(BlockSoundGroup.LANTERN)
                .strength(2F, 10F).nonOpaque();

        private static final AbstractBlock.Settings CAGE_LAMP = BlockProperties.copy(IRON_SCAFFOLDING)
                .luminance(state -> 14);

        private static final AbstractBlock.Settings YELLOW_SCAFFOLDING = BlockProperties.copy(IRON_SCAFFOLDING)
                .mapColor(MapColor.YELLOW);
        private static final AbstractBlock.Settings RED_SCAFFOLDING = BlockProperties.copy(IRON_SCAFFOLDING)
                .mapColor(MapColor.RED);
    }
}
