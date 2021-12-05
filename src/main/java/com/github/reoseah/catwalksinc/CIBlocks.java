package com.github.reoseah.catwalksinc;

import static com.github.reoseah.catwalksinc.CatwalksInc.id;

import com.github.reoseah.catwalksinc.blocks.CageLampBlock;
import com.github.reoseah.catwalksinc.blocks.CatwalkBlock;
import com.github.reoseah.catwalksinc.blocks.CatwalkBlock.CatwalkData;
import com.github.reoseah.catwalksinc.blocks.CatwalkStairsBlock;
import com.github.reoseah.catwalksinc.blocks.CatwalkStairsBlock.CatwalkStairsData;
import com.github.reoseah.catwalksinc.blocks.IndustrialLadderBlock;
import com.github.reoseah.catwalksinc.blocks.IronRodsBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class CIBlocks {
	public static final Block CATWALK = register("catwalk", new CatwalkBlock(BlockSettings.IRON_SCAFFOLDING));
	public static final Block CATWALK_STAIRS = register("catwalk_stairs",
			new CatwalkStairsBlock(BlockSettings.IRON_SCAFFOLDING));
	public static final Block IRON_BARS = register("iron_rods", new IronRodsBlock(BlockSettings.IRON_SCAFFOLDING));
	public static final Block CAGE_LAMP = register("cage_lamp", new CageLampBlock(BlockSettings.CAGE_LAMP));
	public static final Block INDUSTRIAL_LADDER = register("industrial_ladder",
			new IndustrialLadderBlock(BlockSettings.IRON_SCAFFOLDING));

	private static Block register(String name, Block entry) {
		return Registry.register(Registry.BLOCK, id(name), entry);
	}

	public static class BlockSettings {
		private static final FabricBlockSettings IRON_SCAFFOLDING = FabricBlockSettings
				.of(Material.METAL, MapColor.GRAY) //
				.strength(2F, 10F) //
				.nonOpaque() //
				.sounds(BlockSoundGroup.LANTERN) //
				.breakByTool(FabricToolTags.PICKAXES);

		private static final FabricBlockSettings CAGE_LAMP = FabricBlockSettings.copyOf(BlockSettings.IRON_SCAFFOLDING)
				.luminance(state -> 14);

	}

	public static class BlockEntityTypes {
		public static final BlockEntityType<CatwalkData> CATWALK = register("catwalk",
				FabricBlockEntityTypeBuilder.create(CatwalkData::new, CIBlocks.CATWALK).build());
		public static final BlockEntityType<CatwalkStairsData> CATWALK_STAIRS = register("catwalk_stairs",
				FabricBlockEntityTypeBuilder.create(CatwalkStairsData::new, CIBlocks.CATWALK_STAIRS).build());

		private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
			return Registry.register(Registry.BLOCK_ENTITY_TYPE, id(name), type);
		}
	}
}
