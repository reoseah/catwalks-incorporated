package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.github.reoseah.catwalksinc.CIBlocks;
import com.github.reoseah.catwalksinc.CIItems;
import com.github.reoseah.catwalksinc.blocks.PaintScrapableBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class PaintedCagedLadderBlock extends CagedLadderBlock implements PaintScrapableBlock {
	protected static final Map<DyeColor, Block> INSTANCES = new EnumMap<>(DyeColor.class);

	protected final DyeColor color;

	public PaintedCagedLadderBlock(DyeColor color, Block.Settings settings) {
		super(settings);
		this.color = color;
		INSTANCES.put(color, this);
	}

	public static Block ofColor(DyeColor color) {
		return INSTANCES.get(color);
	}

	@Override
	public String getTranslationKey() {
		return CIItems.CAGED_LADDER.getTranslationKey();
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
		world.setBlockState(pos, CIBlocks.CAGED_LADDER.getDefaultState() //
				.with(FACING, state.get(FACING)) //
				.with(WATERLOGGED, state.get(WATERLOGGED)), //
				3);
	}
}
