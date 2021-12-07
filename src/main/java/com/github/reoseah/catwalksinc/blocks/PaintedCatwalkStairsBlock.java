package com.github.reoseah.catwalksinc.blocks;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.github.reoseah.catwalksinc.CIBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class PaintedCatwalkStairsBlock extends CatwalkStairsBlock implements PaintScrapableBlock {
	protected static final Map<DyeColor, Block> INSTANCES = new EnumMap<>(DyeColor.class);

	protected final DyeColor color;

	public PaintedCatwalkStairsBlock(DyeColor color, Block.Settings settings) {
		super(settings);
		this.color = color;
		INSTANCES.put(color, this);
	}

	public static Block ofColor(DyeColor color) {
		return INSTANCES.get(color);
	}

	@Override
	public String getTranslationKey() {
		return CIBlocks.CATWALK_STAIRS.getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
		super.appendTooltip(stack, world, tooltip, options);
		tooltip.add(new TranslatableText("misc.catwalksinc." + this.color.asString()).formatted(Formatting.GRAY));
	}

	@Override
	public boolean canPaintBlock(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Override
	public void scrapPaint(BlockState state, WorldAccess world, BlockPos pos) {
		BlockState uncolored = CIBlocks.CATWALK_STAIRS.getDefaultState() //
				.with(FACING, state.get(FACING)) //
				.with(RIGHT_RAIL, state.get(RIGHT_RAIL)) //
				.with(LEFT_RAIL, state.get(LEFT_RAIL)) //
				.with(WATERLOGGED, state.get(WATERLOGGED));

		BlockPos lower = lowerHalfPos(state, pos);
		world.setBlockState(lower, uncolored.with(HALF, DoubleBlockHalf.LOWER), 3);
		world.setBlockState(lower.up(), uncolored.with(HALF, DoubleBlockHalf.UPPER), 3);
	}
}
