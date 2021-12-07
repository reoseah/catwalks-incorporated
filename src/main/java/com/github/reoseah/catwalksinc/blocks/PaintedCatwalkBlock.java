package com.github.reoseah.catwalksinc.blocks;

import java.util.List;

import com.github.reoseah.catwalksinc.CIBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class PaintedCatwalkBlock extends CatwalkBlock {
	protected final DyeColor color;

	public PaintedCatwalkBlock(DyeColor color, Block.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Override
	public String getTranslationKey() {
		return CIBlocks.CATWALK.getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
		super.appendTooltip(stack, world, tooltip, options);
		tooltip.add(new TranslatableText("misc.catwalksinc." + this.color.asString()).formatted(Formatting.GRAY));
	}

	@Override
	protected BlockState convertToStairs(Direction facing) {
		return PaintedCatwalkStairsBlock.byColor(this.color).getDefaultState() //
				.with(CatwalkStairsBlock.FACING, facing.getOpposite());
	}
}
