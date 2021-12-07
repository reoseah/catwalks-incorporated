package com.github.reoseah.catwalksinc.blocks;

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
	 * @return 1 unit = 1/8 of a vanilla dye, since vanilla dyes 8 glass blocks with
	 *         one dye
	 */
	int getPaintConsumption(DyeColor color, BlockState state, BlockView world, BlockPos pos);

	void paintBlock(DyeColor color, BlockState state, WorldAccess world, BlockPos pos);
}
