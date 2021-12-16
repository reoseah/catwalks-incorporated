package com.github.reoseah.catwalksinc;

import static com.github.reoseah.catwalksinc.CatwalksInc.id;

import com.github.reoseah.catwalksinc.blocks.CageLampBlock;
import com.github.reoseah.catwalksinc.blocks.CrankWheelBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.CagedLadderBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.CagedLadderBlock.PaintedCagedLadderBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkBlock.PaintedCatwalkBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkBlockEntity;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkStairsBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkStairsBlock.PaintedCatwalkStairsBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkStairsBlockEntity;
import com.github.reoseah.catwalksinc.blocks.catwalks.MetalLadderBlock;
import com.github.reoseah.catwalksinc.blocks.catwalks.MetalLadderBlock.PaintedLadderBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

public class CIBlocks {
	public static final Block CATWALK = register("catwalk", new CatwalkBlock(BlockSettings.IRON_SCAFFOLDING));
	public static final Block CATWALK_STAIRS = register("catwalk_stairs",
			new CatwalkStairsBlock(BlockSettings.IRON_SCAFFOLDING));
	public static final Block INDUSTRIAL_LADDER = register("industrial_ladder",
			new MetalLadderBlock(BlockSettings.IRON_SCAFFOLDING));
	public static final Block CAGED_LADDER = register("caged_ladder",
			new CagedLadderBlock(BlockSettings.IRON_SCAFFOLDING));
	public static final Block CAGE_LAMP = register("cage_lamp", new CageLampBlock(BlockSettings.CAGE_LAMP));
	public static final Block CRANK_WHEEL = register("crank_wheel",
			new CrankWheelBlock(BlockSettings.IRON_SCAFFOLDING));

	public static final Block YELLOW_CATWALK = register("yellow_catwalk",
			new PaintedCatwalkBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));
	public static final Block YELLOW_CATWALK_STAIRS = register("yellow_catwalk_stairs",
			new PaintedCatwalkStairsBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));
	public static final Block YELLOW_LADDER = register("yellow_ladder",
			new PaintedLadderBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));
	public static final Block YELLOW_CAGED_LADDER = register("yellow_caged_ladder",
			new PaintedCagedLadderBlock(DyeColor.YELLOW, BlockSettings.YELLOW_SCAFFOLDING));

	public static final Block RED_CATWALK = register("red_catwalk",
			new PaintedCatwalkBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));
	public static final Block RED_CATWALK_STAIRS = register("red_catwalk_stairs",
			new PaintedCatwalkStairsBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));
	public static final Block RED_LADDER = register("red_ladder",
			new PaintedLadderBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));
	public static final Block RED_CAGED_LADDER = register("red_caged_ladder",
			new PaintedCagedLadderBlock(DyeColor.RED, BlockSettings.RED_SCAFFOLDING));

	private static Block register(String name, Block entry) {
		return Registry.register(Registry.BLOCK, id(name), entry);
	}

	public static class BlockSettings {
		private static final FabricBlockSettings IRON_SCAFFOLDING = FabricBlockSettings
				.of(Material.METAL, MapColor.GRAY) //
				.strength(2F, 10F) //
				.nonOpaque() //
				.sounds(BlockSoundGroup.LANTERN);

		private static final FabricBlockSettings CAGE_LAMP = FabricBlockSettings.copyOf(BlockSettings.IRON_SCAFFOLDING)
				.luminance(state -> 14);

		private static final FabricBlockSettings YELLOW_SCAFFOLDING = FabricBlockSettings
				.copyOf(BlockSettings.IRON_SCAFFOLDING).mapColor(DyeColor.YELLOW);
		private static final FabricBlockSettings RED_SCAFFOLDING = FabricBlockSettings
				.copyOf(BlockSettings.IRON_SCAFFOLDING).mapColor(DyeColor.RED);
	}

	public static class BlockEntityTypes {
		public static final BlockEntityType<CatwalkBlockEntity> CATWALK = register("catwalk",
				FabricBlockEntityTypeBuilder.create(CatwalkBlockEntity::new, //
						CIBlocks.CATWALK, //
						CIBlocks.YELLOW_CATWALK, //
						CIBlocks.RED_CATWALK //
				).build());
		public static final BlockEntityType<CatwalkStairsBlockEntity> CATWALK_STAIRS = register("catwalk_stairs",
				FabricBlockEntityTypeBuilder.create(CatwalkStairsBlockEntity::new, //
						CIBlocks.CATWALK_STAIRS, //
						CIBlocks.YELLOW_CATWALK_STAIRS, //
						CIBlocks.RED_CATWALK_STAIRS //
				).build());
		public static final BlockEntityType<CatwalkBlockEntity> CAGED_LADDER = register("caged_ladder",
				FabricBlockEntityTypeBuilder.create(CatwalkBlockEntity::new, //
						CIBlocks.CAGED_LADDER, //
						CIBlocks.YELLOW_CAGED_LADDER, //
						CIBlocks.RED_CAGED_LADDER //
				).build());

		private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
			return Registry.register(Registry.BLOCK_ENTITY_TYPE, id(name), type);
		}
	}
}
