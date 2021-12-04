package com.github.reoseah.catwalksinc.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface Catwalk {
	boolean canCatwalkConnect(BlockState state, BlockView world, BlockPos pos, Direction side);
}
