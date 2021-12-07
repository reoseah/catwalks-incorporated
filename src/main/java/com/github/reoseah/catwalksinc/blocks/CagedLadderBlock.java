package com.github.reoseah.catwalksinc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class CagedLadderBlock extends WaterloggableBlock {
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty EXTENSION = BooleanProperty.of("extension");

	public static final VoxelShape COLLISION_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(),
			Block.createCuboidShape(1, 0, 1, 15, 16, 15), BooleanBiFunction.ONLY_FIRST);

	public static final VoxelShape[] EXTENSION_COLLISION_SHAPES;
	static {
		EXTENSION_COLLISION_SHAPES = new VoxelShape[4];
		for (int i = 0; i < 4; i++) {
			EXTENSION_COLLISION_SHAPES[i] = VoxelShapes.combineAndSimplify(COLLISION_SHAPE,
					IndustrialLadderBlock.COLLISION_SHAPES[i], BooleanBiFunction.ONLY_FIRST);
		}
	}

	public CagedLadderBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(FACING, Direction.NORTH) //
				.with(EXTENSION, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING, EXTENSION);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(EXTENSION) ? EXTENSION_COLLISION_SHAPES[state.get(FACING).getHorizontal()] : COLLISION_SHAPE;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
		return state.with(EXTENSION, this.shouldChangeToExtension(state, ctx.getWorld(), ctx.getBlockPos()));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
			WorldAccess world, BlockPos pos, BlockPos posFrom) {
		state = super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos);
		state = state.with(EXTENSION, this.shouldChangeToExtension(state, world, pos));

		return state;
	}

	private boolean shouldChangeToExtension(BlockState state, WorldAccess world, BlockPos pos) {
		Direction supportDirection = state.get(FACING).getOpposite();
		BlockPos supportPos = pos.offset(supportDirection);
		BlockState support = world.getBlockState(supportPos);
		if (support.getBlock()instanceof Catwalk catwalk
				&& catwalk.canCatwalkConnect(support, world, supportPos, supportDirection.getOpposite())) {
			return true;
		} else {
			return false;
		}
	}
}
