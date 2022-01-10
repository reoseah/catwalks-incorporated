package com.github.reoseah.catwalksinc.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/**
 * A block that can be changed with a paint roller.
 */
public interface Paintable {
	default boolean canPaintBlock(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	/**
	 * @return 1 unit = 1/8 of a vanilla dye, i.e. one dye item will allow to paint
	 *         8 blocks
	 */
	default int getPaintConsumption(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
		return 1;
	}

	void paintBlock(DyeColor color, BlockState state, WorldAccess world, BlockPos pos);
}
