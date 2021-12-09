package com.github.reoseah.catwalksinc.blocks.catwalks;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.Material;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public interface Catwalk extends Walkable {
	static boolean shouldDisableHandrail(BlockState state, WorldAccess world, BlockPos pos, Direction side) {
		Block block = state.getBlock();
		if (block instanceof Walkable walkable) {
			return walkable.shouldDisableHandrail(state, world, pos, side);
		}
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
				|| block instanceof HopperBlock) {
			return true;
		}
		return state.isSideSolidFullSquare(world, pos, side) && state.getMaterial() != Material.AGGREGATE;
	};
}
