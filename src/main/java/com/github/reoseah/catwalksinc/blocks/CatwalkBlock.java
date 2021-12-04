package com.github.reoseah.catwalksinc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class CatwalkBlock extends Block implements Waterloggable {
	public static final BooleanProperty SOUTH = Properties.SOUTH;
	public static final BooleanProperty WEST = Properties.WEST;
	public static final BooleanProperty NORTH = Properties.NORTH;
	public static final BooleanProperty EAST = Properties.EAST;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	private static final VoxelShape[] OUTLINE_SHAPES;
	private static final VoxelShape[] COLLISION_SHAPES;

	public CatwalkBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(SOUTH, false).with(WEST, false) //
				.with(NORTH, false).with(EAST, false) //
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SOUTH, EAST, NORTH, WEST, WATERLOGGED);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int i = (state.get(SOUTH) ? 1 : 0) | (state.get(WEST) ? 2 : 0) //
				| (state.get(NORTH) ? 4 : 0) | (state.get(EAST) ? 8 : 0);
		return COLLISION_SHAPES[i];
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int i = (state.get(SOUTH) ? 1 : 0) | (state.get(WEST) ? 2 : 0) //
				| (state.get(NORTH) ? 4 : 0) | (state.get(EAST) ? 8 : 0);
		return OUTLINE_SHAPES[i];
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		int i = (state.get(SOUTH) ? 1 : 0) | (state.get(WEST) ? 2 : 0) | (state.get(NORTH) ? 4 : 0)
				| (state.get(EAST) ? 8 : 0);
		return OUTLINE_SHAPES[i];
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();

//        if (world.getBlockState(pos.up()).isAir()) {
//            for (Direction side : Direction.Type.HORIZONTAL) {
//                BlockPos neighborAbove = pos.up().offset(side);
//                BlockState neighborAboveState = world.getBlockState(neighborAbove);
//                Block neighborAboveBlock = neighborAboveState.getBlock();
//                if (neighborAboveBlock == this || neighborAboveBlock instanceof CatwalkConnectingBlock && ((CatwalkConnectingBlock) neighborAboveBlock).shouldCatwalkConnect(neighborAboveState, world, neighborAbove, side)) {
//                    return CatwalksInc.CATWALK_STAIRS.getDefaultState().with(CatwalkStairsBlock.FACING, side.getOpposite());
//                }
//            }
//        }

		boolean south = this.shouldHaveFence(world, pos, Direction.SOUTH);
		boolean west = this.shouldHaveFence(world, pos, Direction.WEST);
		boolean north = this.shouldHaveFence(world, pos, Direction.NORTH);
		boolean east = this.shouldHaveFence(world, pos, Direction.EAST);

		return this.getDefaultState().with(SOUTH, south).with(WEST, west).with(NORTH, north).with(EAST, east);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
			WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (direction.getAxis().isHorizontal()) {
			return state.with(sideToProperty(direction), this.shouldHaveFence(world, pos, direction));
		}
		return state;
	}

	public boolean shouldHaveFence(BlockView world, BlockPos pos, Direction side) {
		BlockState neighbor = world.getBlockState(pos.offset(side));
		Block block = neighbor.getBlock();

		// no fence to other catwalks
		if (block instanceof CatwalkBlock
				// and to cauldrons
				|| block == Blocks.CAULDRON
				// and to blocks with to full side
				|| neighbor.isSideSolidFullSquare(world, pos.offset(side), side.getOpposite())
						// unless they are sand/gravel
						&& neighbor.getMaterial() != Material.AGGREGATE) {
			return false;
		}
		return true;
	}

	protected static BooleanProperty sideToProperty(Direction direction) {
		switch (direction) {
		case SOUTH:
			return SOUTH;
		case WEST:
			return WEST;
		case NORTH:
			return NORTH;
		case EAST:
		default:
			return EAST;
		}
	}

	static {
		OUTLINE_SHAPES = new VoxelShape[16];
		COLLISION_SHAPES = new VoxelShape[16];

		VoxelShape floor = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

		VoxelShape south = Block.createCuboidShape(0, 0, 14, 16, 16, 16);
		VoxelShape west = Block.createCuboidShape(0, 0, 0, 2, 16, 16);
		VoxelShape north = Block.createCuboidShape(0, 0, 0, 16, 16, 2);
		VoxelShape east = Block.createCuboidShape(14, 0, 0, 16, 16, 16);

		// collision shapes are only half-pixel thick
		// otherwise you bump into edges of fences too much
		VoxelShape floorColl = Block.createCuboidShape(0.5, 0, 0.5, 15.5, 1, 15.5);
		VoxelShape southColl = Block.createCuboidShape(0.5, 0, 15, 15.5, 16, 15.5);
		VoxelShape westColl = Block.createCuboidShape(0.5, 0, 0.5, 1, 16, 15.5);
		VoxelShape northColl = Block.createCuboidShape(0.5, 0, 0.5, 15.5, 16, 1);
		VoxelShape eastColl = Block.createCuboidShape(15, 0, 0.5, 15.5, 16, 15.5);

		// used to cut out 4 squares from collision boxes
		// corresponding to empty space in the texture
		// this allows projectiles to shoot through catwalks sides
		VoxelShape cutout = VoxelShapes.union( //
				Block.createCuboidShape(0, 2, 2, 16, 13, 14), //
				Block.createCuboidShape(2, 2, 0, 14, 13, 16));

		for (int i = 0; i < 16; i++) {
			VoxelShape outline = floor;
			VoxelShape collision = floorColl;
			if ((i & 1) != 0) {
				outline = VoxelShapes.union(outline, south);
				collision = VoxelShapes.union(collision, southColl);
			}
			if ((i & 2) != 0) {
				outline = VoxelShapes.union(outline, west);
				collision = VoxelShapes.union(collision, westColl);
			}
			if ((i & 4) != 0) {
				outline = VoxelShapes.union(outline, north);
				collision = VoxelShapes.union(collision, northColl);
			}
			if ((i & 8) != 0) {
				outline = VoxelShapes.union(outline, east);
				collision = VoxelShapes.union(collision, eastColl);
			}
			OUTLINE_SHAPES[i] = outline;
			COLLISION_SHAPES[i] = VoxelShapes.combineAndSimplify(collision, cutout, BooleanBiFunction.ONLY_FIRST);
		}
	}
}
