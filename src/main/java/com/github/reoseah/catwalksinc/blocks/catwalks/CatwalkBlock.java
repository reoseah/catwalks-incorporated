package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.Optional;

import com.github.reoseah.catwalksinc.CIBlocks;
import com.github.reoseah.catwalksinc.blocks.Paintable;
import com.github.reoseah.catwalksinc.blocks.WaterloggableBlock;
import com.github.reoseah.catwalksinc.blocks.Wrenchable;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkBlockEntity.Handrail;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CatwalkBlock extends WaterloggableBlock implements BlockEntityProvider, Catwalk, Wrenchable, Paintable {
	public static final BooleanProperty SOUTH_RAIL = Properties.SOUTH;
	public static final BooleanProperty WEST_RAIL = Properties.WEST;
	public static final BooleanProperty NORTH_RAIL = Properties.NORTH;
	public static final BooleanProperty EAST_RAIL = Properties.EAST;

	private static final VoxelShape[] OUTLINE_SHAPES;
	private static final VoxelShape[] COLLISION_SHAPES;
	static {
		OUTLINE_SHAPES = new VoxelShape[16];
		COLLISION_SHAPES = new VoxelShape[16];

		VoxelShape floor = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

		VoxelShape south = Block.createCuboidShape(0, 0, 14, 16, 16, 16);
		VoxelShape west = Block.createCuboidShape(0, 0, 0, 2, 16, 16);
		VoxelShape north = Block.createCuboidShape(0, 0, 0, 16, 16, 2);
		VoxelShape east = Block.createCuboidShape(14, 0, 0, 16, 16, 16);

		// collision shapes are only half-pixel thick
		// otherwise you bump into edges of handrails too much
		VoxelShape floorColl = Block.createCuboidShape(0.5, 0, 0.5, 15.5, 1, 15.5);
		VoxelShape southColl = Block.createCuboidShape(0.5, 0, 15, 15.5, 16, 15.5);
		VoxelShape westColl = Block.createCuboidShape(0.5, 0, 0.5, 1, 16, 15.5);
		VoxelShape northColl = Block.createCuboidShape(0.5, 0, 0.5, 15.5, 16, 1);
		VoxelShape eastColl = Block.createCuboidShape(15, 0, 0.5, 15.5, 16, 15.5);

		VoxelShape sidesCutout = VoxelShapes.union( //
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
			OUTLINE_SHAPES[i] = VoxelShapes.combineAndSimplify(outline, sidesCutout, BooleanBiFunction.ONLY_FIRST);
			COLLISION_SHAPES[i] = VoxelShapes.combineAndSimplify(collision, sidesCutout, BooleanBiFunction.ONLY_FIRST);
		}
	}

	public CatwalkBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(SOUTH_RAIL, true).with(WEST_RAIL, true) //
				.with(NORTH_RAIL, true).with(EAST_RAIL, true));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(SOUTH_RAIL, EAST_RAIL, NORTH_RAIL, WEST_RAIL);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION_SHAPES[getCollisionIndex(state)];
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPES[getCollisionIndex(state)];
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return OUTLINE_SHAPES[getCollisionIndex(state)];
	}

	protected static int getCollisionIndex(BlockState state) {
		return (state.get(SOUTH_RAIL) ? 1 : 0) //
				| (state.get(WEST_RAIL) ? 2 : 0) //
				| (state.get(NORTH_RAIL) ? 4 : 0) //
				| (state.get(EAST_RAIL) ? 8 : 0);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();

		if (world.getBlockState(pos.up()).isAir()) {
			Optional<Direction> stairsFacing = findStairsUpDirection(world, pos);
			if (stairsFacing.isPresent()) {
				return this.getMatchingStairs() //
						.with(CatwalkStairsBlock.FACING, stairsFacing.get().getOpposite());
			}
		}

		return super.getPlacementState(ctx) //
				.with(SOUTH_RAIL, this.shouldHaveHandrail(world, pos, Direction.SOUTH)) //
				.with(WEST_RAIL, this.shouldHaveHandrail(world, pos, Direction.WEST)) //
				.with(NORTH_RAIL, this.shouldHaveHandrail(world, pos, Direction.NORTH)) //
				.with(EAST_RAIL, this.shouldHaveHandrail(world, pos, Direction.EAST));
	}

	protected static Optional<Direction> findStairsUpDirection(WorldAccess world, BlockPos pos) {
		for (Direction facing : Direction.Type.HORIZONTAL) {
			BlockPos exitPos = pos.up().offset(facing);
			BlockState exitState = world.getBlockState(exitPos);
			if (Catwalk.shouldConvertToStairsToConnect(exitState, world, exitPos, facing.getOpposite())) {
				return Optional.of(facing);
			}
		}

		return Optional.empty();
	}

	protected BlockState getMatchingStairs() {
		return CIBlocks.CATWALK_STAIRS.getDefaultState();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
			WorldAccess world, BlockPos pos, BlockPos posFrom) {
		super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos);

		if (direction.getAxis().isHorizontal()) {
			return state.with(getHandrailProperty(direction), this.shouldHaveHandrail(world, pos, direction));
		}
		return state;
	}

	protected static BooleanProperty getHandrailProperty(Direction direction) {
		switch (direction) {
		case SOUTH:
			return SOUTH_RAIL;
		case WEST:
			return WEST_RAIL;
		case NORTH:
			return NORTH_RAIL;
		case EAST:
		default:
			return EAST_RAIL;
		}
	}

	protected boolean shouldHaveHandrail(WorldAccess world, BlockPos pos, Direction side) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CatwalkBlockEntity catwalk) {
			Optional<Handrail> handrail = catwalk.getHandrailState(side);
			if (handrail.isPresent()) {
				return handrail.get() == Handrail.ALWAYS ? true : false;
			}
		}

		BlockPos neighborPos = pos.offset(side);
		BlockState neighborState = world.getBlockState(neighborPos);

		return !Catwalk.shouldDisableHandrail(neighborState, world, neighborPos, side.getOpposite());
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		// we add block entity manually with world.addBlockEntity
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!(state.getBlock() instanceof CatwalkBlock)) {
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean shouldCatwalksDisableHandrail(BlockState state, BlockView world, BlockPos pos, Direction side) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CatwalkBlockEntity catwalk) {
			return !catwalk.isHandrailForced(side);
		}
		return true;
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		Direction dir = getTargetedQuarter(pos, hitPos);

		CatwalkBlockEntity catwalk = (CatwalkBlockEntity) world.getBlockEntity(pos);
		if (catwalk == null) {
			catwalk = new CatwalkBlockEntity(pos, state);
			world.addBlockEntity(catwalk);
		}

		world.setBlockState(pos, catwalk.useWrench(dir, state, player));

		if (catwalk.canBeRemoved()) {
			world.removeBlockEntity(pos);
		}

		return true;
	}

	public static Direction getTargetedQuarter(BlockPos pos, Vec3d point) {
		double dx = point.getX() - pos.getX();
		double dz = point.getZ() - pos.getZ();

		if (Math.abs(dx - 0.5) > Math.abs(dz - 0.5)) {
			if (dx > 0.5) {
				return Direction.EAST;
			} else {
				return Direction.WEST;
			}
		} else {
			if (dz > 0.5) {
				return Direction.SOUTH;
			} else {
				return Direction.NORTH;
			}
		}
	}

	@Override
	public boolean canPaintBlock(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
		Block block = PaintedCatwalkBlock.ofColor(color);
		return block != null;
	}

	@Override
	public void paintBlock(DyeColor color, BlockState state, WorldAccess world, BlockPos pos) {
		Block block = PaintedCatwalkBlock.ofColor(color);
		if (block != null) {
			world.setBlockState(pos, block.getDefaultState() //
					.with(NORTH_RAIL, state.get(NORTH_RAIL)) //
					.with(SOUTH_RAIL, state.get(SOUTH_RAIL)) //
					.with(WEST_RAIL, state.get(WEST_RAIL)) //
					.with(EAST_RAIL, state.get(EAST_RAIL)) //
					.with(WATERLOGGED, state.get(WATERLOGGED)), //
					3);
		}
	}
}
