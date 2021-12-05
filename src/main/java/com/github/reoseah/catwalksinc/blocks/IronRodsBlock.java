package com.github.reoseah.catwalksinc.blocks;

import java.util.ArrayList;
import java.util.List;

import com.github.reoseah.catwalksinc.CIItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class IronRodsBlock extends Block implements Waterloggable {
	public static final BooleanProperty SOUTH_WEST = BooleanProperty.of("south_west");
	public static final BooleanProperty NORTH_WEST = BooleanProperty.of("north_west");
	public static final BooleanProperty NORTH_EAST = BooleanProperty.of("north_east");
	public static final BooleanProperty SOUTH_EAST = BooleanProperty.of("south_east");

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	private static final VoxelShape[] OUTLINE_SHAPES;
	public static final VoxelShape[] COLLISION_SHAPES;
	static {
		OUTLINE_SHAPES = new VoxelShape[32];
		COLLISION_SHAPES = new VoxelShape[32];

		VoxelShape[] partsOutline = { Block.createCuboidShape(0, 0, 14, 2, 16, 16), // south west
				Block.createCuboidShape(0, 0, 0, 2, 16, 2), // north west
				Block.createCuboidShape(14, 0, 0, 16, 16, 2), // north east
				Block.createCuboidShape(14, 0, 14, 16, 16, 16), // south east
		};
		VoxelShape[] partsCollisions = { Block.createCuboidShape(0, 0, 14, 0.5, 16, 16), // south west
				Block.createCuboidShape(0, 0, 0, 0.5, 16, 0.5), // north west
				Block.createCuboidShape(15.5, 0, 0, 16, 16, 0.5), // north east
				Block.createCuboidShape(15.5, 0, 15.5, 16, 16, 16), // south east
		};

		for (int idx = 0; idx < 16; idx++) {
			VoxelShape outlineShape = VoxelShapes.empty();
			VoxelShape collisionShape = VoxelShapes.empty();
			for (int j = 0; j < 4; j++) {
				if ((idx & (1 << j)) != 0) {
					outlineShape = VoxelShapes.union(outlineShape, partsOutline[j]);
					collisionShape = VoxelShapes.union(collisionShape, partsCollisions[j]);
				}
			}
			OUTLINE_SHAPES[idx] = outlineShape;
			COLLISION_SHAPES[idx] = collisionShape;
		}
	}

	public IronRodsBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(SOUTH_WEST, false).with(NORTH_WEST, false) //
				.with(NORTH_EAST, false).with(SOUTH_EAST, false) //
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(SOUTH_WEST, NORTH_WEST, NORTH_EAST, SOUTH_EAST, WATERLOGGED);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int idx = (state.get(SOUTH_WEST) ? 1 : 0) | (state.get(NORTH_WEST) ? 2 : 0) | (state.get(NORTH_EAST) ? 4 : 0)
				| (state.get(SOUTH_EAST) ? 8 : 0);
		return COLLISION_SHAPES[idx];
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int idx = (state.get(SOUTH_WEST) ? 1 : 0) | (state.get(NORTH_WEST) ? 2 : 0) | (state.get(NORTH_EAST) ? 4 : 0)
				| (state.get(SOUTH_EAST) ? 8 : 0);
		return OUTLINE_SHAPES[idx];
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		int idx = (state.get(SOUTH_WEST) ? 1 : 0) | (state.get(NORTH_WEST) ? 2 : 0) | (state.get(NORTH_EAST) ? 4 : 0)
				| (state.get(SOUTH_EAST) ? 8 : 0);
		return OUTLINE_SHAPES[idx];
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		if (IronRodsBlock.isEmpty(state)) {
			return true;
		}
		if (context.getPlayer() != null && !context.getPlayer().isSneaking()) {
			if (context.getStack().getItem() == CIItems.IRON_ROD) {
				BlockPos pos = context.getBlockPos();
				Vec3d hitPos = context.getHitPos();

				double dx = hitPos.getX() - pos.getX();
				double dz = hitPos.getZ() - pos.getZ();

				BooleanProperty property = IronRodsBlock.getProperty(dx, dz);
				return !state.get(property);
			}
		}
		return super.canReplace(state, context);
	}

	@Override
	public boolean canBucketPlace(BlockState state, Fluid fluid) {
		if (IronRodsBlock.isEmpty(state)) {
			return true;
		}
		return super.canBucketPlace(state, fluid);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
			WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (IronRodsBlock.isEmpty(state)) {
			return state.get(WATERLOGGED) ? Fluids.WATER.getDefaultState().getBlockState()
					: Blocks.AIR.getDefaultState();
		}
		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	public static boolean isEmpty(BlockState state) {
		return !state.get(SOUTH_WEST) && !state.get(NORTH_WEST) && !state.get(NORTH_EAST) && !state.get(SOUTH_EAST);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos pos = ctx.getBlockPos();
		Vec3d hitPos = ctx.getHitPos();
		World world = ctx.getWorld();
		FluidState fluid = world.getFluidState(pos);
		BlockState state = world.getBlockState(pos);

		double dx = hitPos.getX() - pos.getX();
		double dz = hitPos.getZ() - pos.getZ();

		if (ctx.getStack().getItem() == CIItems.IRON_ROD) {
			return (state.isOf(this) ? state
					: this.getDefaultState().with(WATERLOGGED, fluid.getFluid() == Fluids.WATER))
							.with(IronRodsBlock.getProperty(dx, dz), true);
		}

		return state;
	}

	public static BooleanProperty getProperty(double dx, double dz) {
		if (dx > 0.5 && dz > 0.5) {
			return SOUTH_EAST;
		} else if (dx > 0.5 && dz < 0.5) {
			return NORTH_EAST;
		} else if (dx < 0.5 && dz > 0.5) {
			return SOUTH_WEST;
		} else {
			return NORTH_WEST;
		}
	}

	public static BooleanProperty offsetProperty(BooleanProperty property, Direction direction) {
		if (property == SOUTH_WEST) {
			return direction == Direction.NORTH ? NORTH_WEST : SOUTH_EAST;
		}
		if (property == NORTH_WEST) {
			return direction == Direction.SOUTH ? SOUTH_WEST : NORTH_EAST;
		}
		if (property == NORTH_EAST) {
			return direction == Direction.SOUTH ? SOUTH_EAST : NORTH_WEST;
		}
		if (property == SOUTH_EAST) {
			return direction == Direction.NORTH ? NORTH_EAST : SOUTH_WEST;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() == CIItems.IRON_ROD && hit.getSide().getAxis().isHorizontal()) {
			Vec3d hitPos = hit.getPos();
			double dx = hitPos.getX() - pos.getX();
			double dz = hitPos.getZ() - pos.getZ();
			BooleanProperty property = IronRodsBlock.offsetProperty(IronRodsBlock.getProperty(dx, dz), hit.getSide());
			if (!state.get(property)) {
				world.setBlockState(pos, state.with(property, true));
				if (!player.isCreative()) {
					stack.decrement(1);
				}
				return ActionResult.SUCCESS;
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		int rods = 0;
		if (state.get(SOUTH_WEST)) {
			rods++;
		}
		if (state.get(SOUTH_EAST)) {
			rods++;
		}
		if (state.get(NORTH_WEST)) {
			rods++;
		}
		if (state.get(NORTH_EAST)) {
			rods++;
		}
		List<ItemStack> list = new ArrayList<>();
		if (rods > 0) {
			list.add(new ItemStack(CIItems.IRON_ROD, rods));
		}
		return list;
	}
}