package com.github.reoseah.catwalksinc.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

/**
 * Implement on a block to make it behave like a catwalk:
 * <li>handle whether catwalks connect to the block or have handrails
 */
public interface Catwalk {
	/**
	 * Returns whather a catwalk connects at that direction. <br>
	 * E.g. catwalk stairs only connect at two ends and never at sides
	 */
	boolean canCatwalkConnect(BlockState state, BlockView world, BlockPos pos, Direction side);
}
