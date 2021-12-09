package com.github.reoseah.catwalksinc.blocks.catwalks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

/**
 * "CatwalkAccessible" is a block that catwalks should connect to, in other
 * words to not have a handrail at the side touching this block, and possible
 * causing catwalks to transform into stairs shape.
 */
public interface CatwalkAccessible {
	/**
	 * Returns whether a catwalk connects at this side. <br>
	 * E.g. catwalk stairs only connect at two ends and never at sides
	 */
	boolean shouldCatwalksDisableHandrail(BlockState state, BlockView world, BlockPos pos, Direction side);
}