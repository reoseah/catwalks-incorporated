package com.github.reoseah.catwalksinc.blocks;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.github.reoseah.catwalksinc.CIBlocks;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.BlockView;

public class PaintedCatwalkStairsBlock extends CatwalkStairsBlock {
	public static final Map<DyeColor, PaintedCatwalkStairsBlock> INSTANCES = new EnumMap<>(DyeColor.class);

	protected final DyeColor color;

	public PaintedCatwalkStairsBlock(DyeColor color, Block.Settings settings) {
		super(settings);
		this.color = color;
		INSTANCES.put(color, this);
	}

	public static PaintedCatwalkStairsBlock byColor(DyeColor color) {
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
}
