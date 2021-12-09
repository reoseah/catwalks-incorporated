package com.github.reoseah.catwalksinc.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/**
 * A block that can be changed with a paint scraper.
 */
public interface PaintScrapableBlock {
	default boolean canScrapPaint(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	void scrapPaint(BlockState state, WorldAccess world, BlockPos pos);
}
