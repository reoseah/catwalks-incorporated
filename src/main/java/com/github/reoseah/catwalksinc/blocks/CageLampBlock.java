package com.github.reoseah.catwalksinc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CageLampBlock extends Block {
	public static final EnumProperty<Direction> FACING = Properties.FACING;

	public static final VoxelShape[] SHAPES = { //
			Block.createCuboidShape(4, 6, 4, 12, 16, 12), //
			Block.createCuboidShape(4, 0, 4, 12, 10, 12), //
			Block.createCuboidShape(4, 4, 6, 12, 12, 16), //
			Block.createCuboidShape(4, 4, 0, 12, 12, 10), //
			Block.createCuboidShape(6, 4, 4, 16, 12, 12), //
			Block.createCuboidShape(0, 4, 4, 10, 12, 12), //
	};

	public CageLampBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.DOWN));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES[state.get(FACING).getId()];
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getSide().getOpposite());
	}
}
