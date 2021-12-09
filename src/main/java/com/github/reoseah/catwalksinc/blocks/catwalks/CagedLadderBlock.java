package com.github.reoseah.catwalksinc.blocks.catwalks;

import org.jetbrains.annotations.Nullable;

import com.github.reoseah.catwalksinc.blocks.Paintable;
import com.github.reoseah.catwalksinc.blocks.WaterloggableBlock;
import com.github.reoseah.catwalksinc.blocks.Wrenchable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
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

public class CagedLadderBlock extends WaterloggableBlock implements Wrenchable, Paintable {
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
				&& catwalk.canOthersConnect(support, world, supportPos, supportDirection.getOpposite())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		world.setBlockState(pos, state.cycle(FACING), 3);
		return true;
	}

	@Override
	public int getPaintConsumption(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
		return 2;
	}

	@Override
	public void paintBlock(DyeColor color, BlockState state, WorldAccess world, BlockPos pos) {
		world.setBlockState(pos, PaintedCagedLadderBlock.ofColor(color).getDefaultState() //
				.with(FACING, state.get(FACING)) //
				.with(WATERLOGGED, state.get(WATERLOGGED)), //
				3);
	}
}
