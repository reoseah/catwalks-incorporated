package com.github.reoseah.catwalksinc.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class IndustrialLadderBlock extends Block implements Waterloggable, Wrenchable {
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public static final VoxelShape[] OUTLINE_SHAPES = { //
			Block.createCuboidShape(0, 0, 0, 16, 16, 4), //
			Block.createCuboidShape(12, 0, 0, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 12, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 0, 4, 16, 16), //
	};

	public static final VoxelShape[] COLLISION_SHAPES = { //
			Block.createCuboidShape(0, 0, 0, 16, 16, 1), //
			Block.createCuboidShape(15, 0, 0, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 15, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 0, 1, 16, 16), //
	};

	public IndustrialLadderBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(FACING, Direction.NORTH) //
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPES[state.get(FACING).getHorizontal()];
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
			WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return state;
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		world.setBlockState(pos, state.cycle(FACING), 3);
		return true;
	}
}
