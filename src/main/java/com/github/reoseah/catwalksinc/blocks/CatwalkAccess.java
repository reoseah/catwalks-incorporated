package com.github.reoseah.catwalksinc.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

/**
 * A block at a border with which, catwalks or similar blocks will not place a
 * hand-rail.
 */
public interface CatwalkAccess {
	/**
	 * Called when a catwalk o similar block decides whether to have or not a
	 * hand-rail at a side touching this block.
	 */
	boolean needsCatwalkAccess(BlockState state, BlockView world, BlockPos pos, Direction side);

	/**
	 * Called when a catwalk decides whether it should transform into stairs
	 * variant. Called for four blocks placed diagonally to the catwalk (to the side
	 * and up). By default only done for doors.
	 */
	default boolean needsCatwalkConnectivity(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return false;
	}
}
