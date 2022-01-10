package com.github.reoseah.catwalksinc.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class CatwalkHelper {
	public static boolean hasBuiltinCatwalksAccess(BlockState state, WorldAccess world, BlockPos pos, Direction side) {
		Block block = state.getBlock();
		if (block instanceof DoorBlock) {
			return state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
		}
		if (block instanceof LadderBlock) {
			return state.get(LadderBlock.FACING) == side;
		}
		if (block instanceof InventoryProvider provider) {
			return provider.getInventory(state, world, pos) != null;
		}
		if (block instanceof AbstractCauldronBlock //
				|| block instanceof HopperBlock //
				|| block instanceof AbstractChestBlock<?>) {
			return true;
		}
		return state.isSideSolidFullSquare(world, pos, side) && state.getMaterial() != Material.AGGREGATE;
	}

	public static boolean hasBuiltinCatwalksConnectivity(BlockState state, WorldAccess world, BlockPos pos,
			Direction side) {
		Block block = state.getBlock();
		if (block instanceof DoorBlock) {
			return state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
		}
		return false;
	}
}
