package com.github.reoseah.catwalksinc.block;

import com.github.reoseah.catwalksinc.CatwalksInc;
import com.github.reoseah.catwalksinc.CIncBlocks;
import com.github.reoseah.catwalksinc.block.entity.CatwalkBlockEntity;
import com.github.reoseah.catwalksinc.block.state.ElementMode;
import com.github.reoseah.catwalksinc.block.util.WrenchHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CatwalkBlock extends WaterloggableBlock
		implements BlockEntityProvider, CatwalkAccess, Wrenchable, Paintable {
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

	public CatwalkBlock(Settings settings) {
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

		if (world.getBlockState(pos.up()).isAir() || world.getBlockState(pos.up()).isOf(Blocks.WATER)) {
			Optional<Direction> stairsFacing = findStairsUpDirection(world, pos);
			if (stairsFacing.isPresent()) {
				return this.getMatchingStairs() //
						.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER) //
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
			if (exitState.getBlock()instanceof CatwalkAccess catwalk
					&& catwalk.needsCatwalkConnectivity(exitState, world, pos, facing.getOpposite())
					|| CatwalkHelper.hasBuiltinCatwalksConnectivity(exitState, world, exitPos, facing.getOpposite())) {
				return Optional.of(facing);
			}
		}

		return Optional.empty();
	}

	protected BlockState getMatchingStairs() {
		return CIncBlocks.CATWALK_STAIRS.getDefaultState();
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

	public static BooleanProperty getHandrailProperty(Direction direction) {
		return switch (direction) {
			case SOUTH -> SOUTH_RAIL;
			case WEST -> WEST_RAIL;
			case NORTH -> NORTH_RAIL;
			case EAST -> EAST_RAIL;
			default -> throw new IncompatibleClassChangeError();
		};
	}

	public boolean shouldHaveHandrail(WorldAccess world, BlockPos pos, Direction side) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CatwalkBlockEntity catwalk) {
			Optional<ElementMode> handrail = catwalk.getHandrailState(side);
			if (handrail.isPresent()) {
				return handrail.get() == ElementMode.ALWAYS;
			}
		}

		BlockPos neighborPos = pos.offset(side);
		BlockState neighbor = world.getBlockState(neighborPos);

		if (neighbor.getBlock()instanceof CatwalkAccess accessible) {
			return !accessible.needsCatwalkAccess(neighbor, world, neighborPos, side.getOpposite());
		}

		return !CatwalkHelper.hasBuiltinCatwalksAccess(neighbor, world, neighborPos, side.getOpposite());
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		// we add block entity manually with world.addBlockEntity
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!(newState.getBlock() instanceof CatwalkBlock)) {
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean needsCatwalkAccess(BlockState state, BlockView world, BlockPos pos, Direction side) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CatwalkBlockEntity catwalk) {
			return !catwalk.isHandrailForced(side);
		}
		return true;
	}

	@Override
	public boolean needsCatwalkConnectivity(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return this.needsCatwalkAccess(state, world, pos, side);
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		Direction dir = WrenchHelper.getTargetedQuarter(pos, hitPos);

		CatwalkBlockEntity be = (CatwalkBlockEntity) world.getBlockEntity(pos);
		if (be == null) {
			be = new CatwalkBlockEntity(pos, state);
			world.addBlockEntity(be);
		}

		world.setBlockState(pos, be.useWrench(dir, state, player));

		if (be.canBeRemoved()) {
			world.removeBlockEntity(pos);
		}

		return true;
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

	public static class PaintedCatwalkBlock extends CatwalkBlock implements PaintScrapableBlock {
		protected static final Map<DyeColor, Block> INSTANCES = new EnumMap<>(DyeColor.class);

		protected final DyeColor color;

		public PaintedCatwalkBlock(DyeColor color, Settings settings) {
			super(settings);
			this.color = color;
			INSTANCES.put(color, this);
		}

		public static Block ofColor(DyeColor color) {
			return INSTANCES.get(color);
		}

		@Override
		public String getTranslationKey() {
			return Util.createTranslationKey("misc", CatwalksInc.id("painted_catwalk"));
		}

		@Override
		public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
			super.appendTooltip(stack, world, tooltip, options);
			tooltip.add(new TranslatableText("misc.catwalksinc." + this.color.asString()).formatted(Formatting.GRAY));
		}

		@Override
		protected BlockState getMatchingStairs() {
			return CatwalkStairsBlock.PaintedCatwalkStairsBlock.ofColor(this.color).getDefaultState();
		}

		@Override
		public boolean canPaintBlock(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
			return false;
		}

		@Override
		public void scrapPaint(BlockState state, WorldAccess world, BlockPos pos) {
			world.setBlockState(pos, CIncBlocks.CATWALK.getDefaultState() //
					.with(NORTH_RAIL, state.get(NORTH_RAIL)) //
					.with(SOUTH_RAIL, state.get(SOUTH_RAIL)) //
					.with(WEST_RAIL, state.get(WEST_RAIL)) //
					.with(EAST_RAIL, state.get(EAST_RAIL)) //
					.with(WATERLOGGED, state.get(WATERLOGGED)), //
					3);
		}
	}
}
