package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.github.reoseah.catwalksinc.CIBlocks;
import com.github.reoseah.catwalksinc.CIItems;
import com.github.reoseah.catwalksinc.blocks.CatwalkAccess;
import com.github.reoseah.catwalksinc.blocks.PaintScrapableBlock;
import com.github.reoseah.catwalksinc.blocks.Paintable;
import com.github.reoseah.catwalksinc.blocks.WaterloggableBlock;
import com.github.reoseah.catwalksinc.blocks.Wrenchable;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkBlock.PaintedCatwalkBlock;
import com.github.reoseah.catwalksinc.util.Side;
import com.github.reoseah.catwalksinc.util.WrenchHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class CatwalkStairsBlock extends WaterloggableBlock
		implements BlockEntityProvider, CatwalkAccess, Wrenchable, Paintable {
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

	public static final BooleanProperty RIGHT_RAIL = BooleanProperty.of("right");
	public static final BooleanProperty LEFT_RAIL = BooleanProperty.of("left");

	private static final VoxelShape[][] OUTLINE_SHAPES;
	private static final VoxelShape[][] COLLISION_SHAPES;
	static {
		OUTLINE_SHAPES = new VoxelShape[4][4];
		COLLISION_SHAPES = new VoxelShape[4][4];

		VoxelShape[] floors = {
				VoxelShapes.union(Block.createCuboidShape(0, 8, 8, 16, 9, 16),
						Block.createCuboidShape(0, 16, 0, 16, 17, 8)),
				VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 8, 9, 16),
						Block.createCuboidShape(8, 16, 0, 16, 17, 16)),
				VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 16, 9, 8),
						Block.createCuboidShape(0, 16, 8, 16, 17, 16)),
				VoxelShapes.union(Block.createCuboidShape(8, 8, 0, 16, 9, 16),
						Block.createCuboidShape(0, 16, 0, 8, 17, 16)) };

		VoxelShape[] leftRails = {
				VoxelShapes.union(Block.createCuboidShape(0, 8, 8, 2, 24, 16),
						Block.createCuboidShape(0, 16, 0, 2, 32, 8)),
				VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 8, 24, 2),
						Block.createCuboidShape(8, 16, 0, 16, 32, 2)),
				VoxelShapes.union(Block.createCuboidShape(14, 8, 0, 16, 24, 8),
						Block.createCuboidShape(14, 16, 8, 16, 32, 16)),
				VoxelShapes.union(Block.createCuboidShape(8, 8, 14, 16, 24, 16),
						Block.createCuboidShape(0, 16, 14, 8, 32, 16)) };
		VoxelShape[] rightRails = {
				VoxelShapes.union(Block.createCuboidShape(14, 8, 8, 16, 24, 16),
						Block.createCuboidShape(14, 16, 0, 16, 32, 8)),
				VoxelShapes.union(Block.createCuboidShape(0, 8, 14, 8, 24, 16),
						Block.createCuboidShape(8, 16, 14, 16, 32, 16)),
				VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 2, 24, 8),
						Block.createCuboidShape(0, 16, 8, 2, 32, 16)),
				VoxelShapes.union(Block.createCuboidShape(8, 8, 0, 16, 24, 2),
						Block.createCuboidShape(0, 16, 0, 8, 32, 2)) };

		VoxelShape[] leftRailsColl = {
				VoxelShapes.union(Block.createCuboidShape(0.5, 8, 8, 1, 24, 16),
						Block.createCuboidShape(0.5, 16, 0, 1, 32, 8)),
				VoxelShapes.union(Block.createCuboidShape(0, 8, 0.5, 8, 24, 1),
						Block.createCuboidShape(8, 16, 0.5, 16, 32, 1)),
				VoxelShapes.union(Block.createCuboidShape(15, 8, 0, 15.5, 24, 8),
						Block.createCuboidShape(15, 16, 8, 15.5, 32, 16)),
				VoxelShapes.union(Block.createCuboidShape(8, 8, 15, 16, 24, 15.5),
						Block.createCuboidShape(0, 16, 15, 8, 32, 15.5)) };
		VoxelShape[] rightRailsColl = {
				VoxelShapes.union(Block.createCuboidShape(15, 8, 8, 15.5, 24, 16),
						Block.createCuboidShape(15, 16, 0, 15.5, 32, 8)),
				VoxelShapes.union(Block.createCuboidShape(0, 8, 15, 8, 24, 15.5),
						Block.createCuboidShape(8, 16, 15, 16, 32, 15.5)),
				VoxelShapes.union(Block.createCuboidShape(0.5, 8, 0, 1, 24, 8),
						Block.createCuboidShape(0.5, 16, 8, 1, 32, 16)),
				VoxelShapes.union(Block.createCuboidShape(8, 8, 0.5, 16, 24, 1),
						Block.createCuboidShape(0, 16, 0.5, 8, 32, 1)) };

		for (int i = 0; i < 4; i++) {
			COLLISION_SHAPES[i][0] = OUTLINE_SHAPES[i][0] = floors[i];
			OUTLINE_SHAPES[i][1] = VoxelShapes.union(floors[i], leftRails[i]);
			OUTLINE_SHAPES[i][2] = VoxelShapes.union(floors[i], rightRails[i]);
			OUTLINE_SHAPES[i][3] = VoxelShapes.union(floors[i], leftRails[i], rightRails[i]);

			COLLISION_SHAPES[i][1] = VoxelShapes.union(floors[i], leftRailsColl[i]);
			COLLISION_SHAPES[i][2] = VoxelShapes.union(floors[i], rightRailsColl[i]);
			COLLISION_SHAPES[i][3] = VoxelShapes.union(floors[i], leftRailsColl[i], rightRailsColl[i]);
		}
	}

	public CatwalkStairsBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(FACING, Direction.NORTH) //
				.with(HALF, DoubleBlockHalf.LOWER) //
				.with(RIGHT_RAIL, true) //
				.with(LEFT_RAIL, true));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING, HALF, RIGHT_RAIL, LEFT_RAIL);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int i = (state.get(LEFT_RAIL) ? 1 : 0) | (state.get(RIGHT_RAIL) ? 2 : 0);
		VoxelShape voxelShape = OUTLINE_SHAPES[state.get(FACING).getHorizontal()][i];

		return state.get(HALF) == DoubleBlockHalf.UPPER ? voxelShape.offset(0, -1, 0) : voxelShape;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int i = (state.get(LEFT_RAIL) ? 1 : 0) | (state.get(RIGHT_RAIL) ? 2 : 0);
		VoxelShape voxelShape = COLLISION_SHAPES[state.get(FACING).getHorizontal()][i];

		return state.get(HALF) == DoubleBlockHalf.UPPER ? voxelShape.offset(0, -1, 0) : voxelShape;
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		int i = (state.get(LEFT_RAIL) ? 1 : 0) | (state.get(RIGHT_RAIL) ? 2 : 0);
		VoxelShape voxelShape = OUTLINE_SHAPES[state.get(FACING).getHorizontal()][i];

		return state.get(HALF) == DoubleBlockHalf.UPPER ? voxelShape.offset(0, -1, 0) : voxelShape;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient) {
			if (player.isCreative()) {
				onBreakInCreative(world, pos, state, player);
			} else {
				dropStacks(state, world, pos, (BlockEntity) null, player, player.getMainHandStack());
			}
		}

		super.onBreak(world, pos, state, player);
	}

	protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf half = state.get(HALF);
		if (half == DoubleBlockHalf.UPPER) {
			BlockPos below = pos.down();
			BlockState stateBelow = world.getBlockState(below);
			if (stateBelow.getBlock() == state.getBlock() && stateBelow.get(HALF) == DoubleBlockHalf.LOWER) {
				world.setBlockState(below, Blocks.AIR.getDefaultState(), 35);
				world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, below, Block.getRawIdFromState(stateBelow));
			}
		}
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
			@Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
			WorldAccess world, BlockPos pos, BlockPos posFrom) {
		state = super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos);

		DoubleBlockHalf half = state.get(HALF);
		if (half == DoubleBlockHalf.LOWER && direction == Direction.UP) {
			if (!(newState.getBlock() instanceof CatwalkStairsBlock) || newState.get(HALF) != DoubleBlockHalf.UPPER) {
				return Blocks.AIR.getDefaultState();
			}
		}
		if (half == DoubleBlockHalf.UPPER && direction == Direction.DOWN) {
			if (!(newState.getBlock() instanceof CatwalkStairsBlock) || newState.get(HALF) != DoubleBlockHalf.LOWER) {
				return Blocks.AIR.getDefaultState();
			}
		}

		Direction left = state.get(FACING).rotateYClockwise();
//		if (direction == left) 
		{
			state = state.with(LEFT_RAIL, this.shouldHaveHandrail(state, world, pos, left, Side.LEFT));
		}
		Direction right = state.get(FACING).rotateYCounterclockwise();
//		if (direction == right) 
		{
			state = state.with(RIGHT_RAIL, this.shouldHaveHandrail(state, world, pos, right, Side.RIGHT));
		}
		return state;
	}

	protected boolean shouldHaveHandrail(BlockState state, WorldAccess world, BlockPos pos, Direction direction,
			Side side) {
		if (state.get(HALF) == DoubleBlockHalf.UPPER) {
			pos = pos.down();
			state = state.with(HALF, DoubleBlockHalf.LOWER);
		}

		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CatwalkStairsBlockEntity catwalk) {
			Optional<ElementMode> handrail = catwalk.getHandrailState(side);
			if (handrail.isPresent()) {
				return handrail.get() == ElementMode.ALWAYS ? true : false;
			}
		}

		BlockState neighbor = world.getBlockState(pos.offset(direction));
		BlockState above = world.getBlockState(pos.offset(direction).up());

		if (neighbor.isSideSolidFullSquare(world, pos.offset(direction), direction.getOpposite())
				&& neighbor.getMaterial() != Material.AGGREGATE
				&& above.isSideSolidFullSquare(world, pos.offset(direction).up(), direction.getOpposite())
				&& above.getMaterial() != Material.AGGREGATE) {
			return false;
		}
		if (neighbor.getBlock() instanceof CatwalkStairsBlock) {
			if (neighbor.get(FACING) != state.get(FACING) //
					|| neighbor.get(HALF) != state.get(HALF)) {
				// stairs not matching
				return true;
			}
			BlockEntity otherbe = world.getBlockEntity(pos.offset(direction));
			return otherbe instanceof CatwalkStairsBlockEntity catwalkbe
					&& catwalkbe.isHandrailForced(side.getOpposite());

		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState state2, Direction direction) {
		return direction.getAxis().isHorizontal() && state2.getBlock() == this && state.get(FACING) == direction
				&& state2.get(FACING) == direction.getOpposite() && state.get(HALF) == DoubleBlockHalf.LOWER
				|| super.isSideInvisible(state, state2, direction);
	}

	@Override
	public boolean needsCatwalkAccess(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (state.get(HALF) == DoubleBlockHalf.LOWER) {
			return side == state.get(FACING);
		} else {
			return side == state.get(FACING).getOpposite();
		}
	}

	@Override
	public boolean needsCatwalkConnectivity(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return this.needsCatwalkAccess(state, world, pos, side);
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		if (state.get(HALF) == DoubleBlockHalf.UPPER) {
			pos = pos.down();
			state = world.getBlockState(pos);
		}

		if (player != null && player.isSneaking()) {
			world.setBlockState(pos, state.cycle(FACING));
			world.setBlockState(pos.up(), state.cycle(FACING).with(HALF, DoubleBlockHalf.UPPER));
			return true;
		}

		CatwalkStairsBlockEntity be = (CatwalkStairsBlockEntity) world.getBlockEntity(pos);
		if (be == null) {
			be = new CatwalkStairsBlockEntity(pos, state);
			world.addBlockEntity(be);
		}

		Side stairsSide = WrenchHelper.getBlockHalf(pos, hitPos, state.get(FACING));

		BlockState newState = be.useWrench(stairsSide, state, player);

		world.setBlockState(pos, newState);
		world.setBlockState(pos.up(), newState.with(HALF, DoubleBlockHalf.UPPER));

		if (be.canBeRemoved()) {
			world.removeBlockEntity(pos);
		}

		return true;
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
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(CIItems.CATWALK);
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
			BlockState colored = block.getDefaultState() //
					.with(FACING, state.get(FACING)) //
					.with(RIGHT_RAIL, state.get(RIGHT_RAIL)) //
					.with(LEFT_RAIL, state.get(LEFT_RAIL)) //
					.with(WATERLOGGED, state.get(WATERLOGGED));

			BlockPos lower = getLowerHalfPos(state, pos);
			world.setBlockState(lower, colored.with(HALF, DoubleBlockHalf.LOWER), 3);
			world.setBlockState(lower.up(), colored.with(HALF, DoubleBlockHalf.UPPER), 3);
		}
	}

	protected static BlockPos getLowerHalfPos(BlockState state, BlockPos pos) {
		if (state.get(HALF) == DoubleBlockHalf.UPPER) {
			return pos.down();
		}
		return pos;
	}

	public static BooleanProperty getHandrailProperty(Side side) {
		return side == Side.LEFT ? LEFT_RAIL : RIGHT_RAIL;
	}

	public static Direction getSideDirection(Direction facing, Side side) {
		return side == Side.LEFT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
	}

	public static class PaintedCatwalkStairsBlock extends CatwalkStairsBlock implements PaintScrapableBlock {
		protected static final Map<DyeColor, Block> INSTANCES = new EnumMap<>(DyeColor.class);

		protected final DyeColor color;

		public PaintedCatwalkStairsBlock(DyeColor color, Block.Settings settings) {
			super(settings);
			this.color = color;
			INSTANCES.put(color, this);
		}

		public static Block ofColor(DyeColor color) {
			return INSTANCES.get(color);
		}

		@Override
		public String getTranslationKey() {
			return CIBlocks.CATWALK_STAIRS.getTranslationKey();
		}

		@Override
		public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
			super.appendTooltip(stack, world, tooltip, options);
			tooltip.add(new TranslatableText("misc.catwalksinc." + this.color.asString()).formatted(Formatting.GRAY));
		}

		@Override
		public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
			return new ItemStack(PaintedCatwalkBlock.ofColor(this.color));
		}

		@Override
		public boolean canPaintBlock(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
			return false;
		}

		@Override
		public void scrapPaint(BlockState state, WorldAccess world, BlockPos pos) {
			BlockState uncolored = CIBlocks.CATWALK_STAIRS.getDefaultState() //
					.with(FACING, state.get(FACING)) //
					.with(RIGHT_RAIL, state.get(RIGHT_RAIL)) //
					.with(LEFT_RAIL, state.get(LEFT_RAIL)) //
					.with(WATERLOGGED, state.get(WATERLOGGED));

			BlockPos lower = getLowerHalfPos(state, pos);
			world.setBlockState(lower, uncolored.with(HALF, DoubleBlockHalf.LOWER), 3);
			world.setBlockState(lower.up(), uncolored.with(HALF, DoubleBlockHalf.UPPER), 3);
		}
	}
}
