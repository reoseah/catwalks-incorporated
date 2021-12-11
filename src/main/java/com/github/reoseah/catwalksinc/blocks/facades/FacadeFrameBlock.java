package com.github.reoseah.catwalksinc.blocks.facades;

import com.github.reoseah.catwalksinc.blocks.WaterloggableBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class FacadeFrameBlock extends WaterloggableBlock {
	public FacadeFrameBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		if (stateFrom.isOf(this)) {
			return true;
		}
		return false;
	}
}
