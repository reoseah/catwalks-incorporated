package io.github.reoseah.catwalksinc.block;

import io.github.reoseah.catwalksinc.CatwalksInc;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class CatwalkBlock extends WaterloggableBlock implements BlockEntityProvider {
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;

    public static BooleanProperty getHandrailProperty(Direction direction) {
        return switch (direction) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case NORTH -> NORTH;
            case EAST -> EAST;
            default -> throw new IncompatibleClassChangeError();
        };
    }

    public static final VoxelShape[] OUTLINE_SHAPES = createShapes(2);
    // thinner to not bump into the edges of the handrails as much
    public static final VoxelShape[] COLLISION_SHAPES = createShapes(0.5);

    public static VoxelShape[] createShapes(double handrailThickness) {
        VoxelShape centerCutout = VoxelShapes.union( //
                Block.createCuboidShape(0, 2, 2, 16, 13, 14), //
                Block.createCuboidShape(2, 2, 0, 14, 13, 16));

        VoxelShape floor = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

        VoxelShape southHandrail = Block.createCuboidShape(0, 0, 16 - handrailThickness, 16, 16, 16);
        VoxelShape westHandrail = Block.createCuboidShape(0, 0, 0, handrailThickness, 16, 16);
        VoxelShape northHandrail = Block.createCuboidShape(0, 0, 0, 16, 16, handrailThickness);
        VoxelShape eastHandrail = Block.createCuboidShape(16 - handrailThickness, 0, 0, 16, 16, 16);

        VoxelShape[] shapes = new VoxelShape[16];
        for (int i = 0; i < 16; i++) {
            VoxelShape shape = floor;
            if ((i & 1) != 0) {
                shape = VoxelShapes.union(shape, southHandrail);
            }
            if ((i & 2) != 0) {
                shape = VoxelShapes.union(shape, westHandrail);
            }
            if ((i & 4) != 0) {
                shape = VoxelShapes.union(shape, northHandrail);
            }
            if ((i & 8) != 0) {
                shape = VoxelShapes.union(shape, eastHandrail);
            }
            shapes[i] = VoxelShapes.combineAndSimplify(shape, centerCutout, BooleanBiFunction.ONLY_FIRST);
        }
        return shapes;
    }

    public static int getShapeIndex(boolean south, boolean west, boolean north, boolean east) {
        return (south ? 1 : 0) | (west ? 2 : 0) | (north ? 4 : 0) | (east ? 8 : 0);
    }

    public CatwalkBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(SOUTH, true).with(WEST, true).with(NORTH, true).with(EAST, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SOUTH, EAST, NORTH, WEST);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context.isHolding(this.asItem()) && !context.isDescending()) {
            // it helps to place catwalk blocks against already placed catwalk
            return VoxelShapes.fullCube();
        }
        return OUTLINE_SHAPES[getShapeIndex(state.get(SOUTH), state.get(WEST), state.get(NORTH), state.get(EAST))];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES[getShapeIndex(state.get(SOUTH), state.get(WEST), state.get(NORTH), state.get(EAST))];
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return OUTLINE_SHAPES[getShapeIndex(state.get(SOUTH), state.get(WEST), state.get(NORTH), state.get(EAST))];
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        if (world.getBlockState(pos.up()).canReplace(ItemPlacementContext.offset(ctx, pos.up(), Direction.DOWN))) {
            Optional<Direction> stairsUpFacing = findNeighborCatwalk(world, pos.up(), ctx.getPlacementDirections());

            if (stairsUpFacing.isPresent()) {
                return CatwalksInc.CATWALK_STAIRS.getDefaultState() //
                        .with(CatwalkStairsBlock.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER) //
                        .with(CatwalkStairsBlock.FACING, stairsUpFacing.get().getOpposite());
            }
        }
        if (world.getBlockState(pos.down()).canReplace(ItemPlacementContext.offset(ctx, pos.down(), Direction.UP))) {
            Optional<Direction> stairsDownFacing = findNeighborCatwalk(world, pos.down(), ctx.getPlacementDirections());

            if (stairsDownFacing.isPresent()) {
                return CatwalksInc.CATWALK_STAIRS.getDefaultState() //
                        .with(CatwalkStairsBlock.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER) //
                        .with(CatwalkStairsBlock.HALF, DoubleBlockHalf.UPPER) //
                        .with(CatwalkStairsBlock.FACING, stairsDownFacing.get());
            }
        }

        return super.getPlacementState(ctx) //
                .with(SOUTH, shouldHaveHandrail(world, pos, Direction.SOUTH)) //
                .with(WEST, shouldHaveHandrail(world, pos, Direction.WEST)) //
                .with(NORTH, shouldHaveHandrail(world, pos, Direction.NORTH)) //
                .with(EAST, shouldHaveHandrail(world, pos, Direction.EAST));
    }

    protected static Optional<Direction> findNeighborCatwalk(WorldAccess world, BlockPos pos, Direction[] placementDirections) {
        for (Direction facing : placementDirections) {
            if (!facing.getAxis().isHorizontal()) {
                continue;
            }
            BlockPos neighborPos = pos.offset(facing);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (getConnectivity(neighborState, world, neighborPos, facing.getOpposite()) == Connectivity.ADAPT_SHAPE) {
                return Optional.of(facing);
            }
        }

        return Optional.empty();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos);

        if (direction.getAxis().isHorizontal()) {
            return state.with(getHandrailProperty(direction), shouldHaveHandrail(world, pos, direction));
        }
        return state;
    }

    public static boolean shouldHaveHandrail(WorldAccess world, BlockPos pos, Direction side) {
        if (world.getBlockEntity(pos) instanceof CatwalkBlockEntity be) {
            CatwalkSideState state = be.getSideState(side);
            if (state != CatwalkSideState.DEFAULT) {
                return state == CatwalkSideState.FORCE_HANDRAIL;
            }
        }

        BlockPos neighborPos = pos.offset(side);
        BlockState neighbor = world.getBlockState(neighborPos);

        return getConnectivity(neighbor, world, neighborPos, side.getOpposite()) == Connectivity.NONE;
    }

    public static Connectivity getConnectivity(BlockState state, WorldAccess world, BlockPos pos, Direction side) {
        Block block = state.getBlock();

        if (block instanceof CatwalkBlock) {
            if (world.getBlockEntity(pos) instanceof CatwalkBlockEntity be) {
                return be.canBeConnected(side) ? Connectivity.ADAPT_SHAPE : Connectivity.NONE;
            }

            return Connectivity.ADAPT_SHAPE;
        }
        if (block instanceof CatwalkStairsBlock) {
            boolean isExit = state.get(CatwalkStairsBlock.HALF) == DoubleBlockHalf.LOWER //
                    ? side == state.get(CatwalkStairsBlock.FACING) //
                    : side.getOpposite() == state.get(CatwalkStairsBlock.FACING);

            return isExit ? Connectivity.ADAPT_SHAPE : Connectivity.NONE;
        }

        if (block instanceof FenceGateBlock) {
            return Connectivity.NO_HANDRAIL;
        }
        if (block instanceof DoorBlock) {
            return state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? Connectivity.NO_HANDRAIL : Connectivity.NONE;
        }
        if (block instanceof LadderBlock) {
            return state.get(LadderBlock.FACING) == side ? Connectivity.NO_HANDRAIL : Connectivity.NONE;
        }

        return !state.isSideSolidFullSquare(world, pos, side) || Block.cannotConnect(state) ? Connectivity.NONE : Connectivity.NO_HANDRAIL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isIn(CatwalksInc.WRENCHES)) {
            if (!world.isClient && this.tryWrench(state, world, pos, player, hit.getPos())) {
                stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public boolean tryWrench(BlockState state, World world, BlockPos pos, PlayerEntity player, Vec3d hitPos) {
        Direction side = getTargetedDirection(pos, hitPos);

        CatwalkBlockEntity be = (CatwalkBlockEntity) world.getBlockEntity(pos);
        if (be == null) {
            be = new CatwalkBlockEntity(pos, state);
            world.addBlockEntity(be);
        }
        CatwalkSideState connectivity = be.getSideState(side);

        CatwalkSideState newConnectivity = connectivity.cycle();
        be.setSideState(side, newConnectivity);
        if (!be.isBlockEntityNecessary()) {
            world.removeBlockEntity(pos);
        }

        boolean handrail = newConnectivity == CatwalkSideState.FORCE_HANDRAIL || newConnectivity == CatwalkSideState.DEFAULT && shouldHaveHandrail(world, pos, side);

        BlockState newState = state.with(getHandrailProperty(side), handrail);
        world.setBlockState(pos, newState);

        if (player != null) {
            player.sendMessage(Text.translatable(newConnectivity.translationKey), true);
        }

        return true;
    }

    public static Direction getTargetedDirection(BlockPos pos, Vec3d point) {
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

    public enum Connectivity {
        ADAPT_SHAPE,
        NO_HANDRAIL,
        NONE;
    }
}
