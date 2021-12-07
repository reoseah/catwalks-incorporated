package com.github.reoseah.catwalksinc.blocks;

import java.util.List;

import com.github.reoseah.catwalksinc.CIItems;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.BlockView;

public class PaintedLadderBlock extends IndustrialLadderBlock {
	protected final DyeColor color;

	public PaintedLadderBlock(DyeColor color, Block.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Override
	public String getTranslationKey() {
		return CIItems.INDUSTRIAL_LADDER.getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
		super.appendTooltip(stack, world, tooltip, options);
		tooltip.add(new TranslatableText("misc.catwalksinc." + this.color.asString()).formatted(Formatting.GRAY));
	}
}
