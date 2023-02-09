package com.github.reoseah.catwalksinc.block;

import com.github.reoseah.catwalksinc.CatwalksUtil;
import com.github.reoseah.catwalksinc.item.WrenchItem;
import com.github.reoseah.catwalksinc.part.ConnectionOverride;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
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
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class CatwalkStairsBlock extends CatwalksIncBlock implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public static final BooleanProperty RIGHT = BooleanProperty.of("right");
    public static final BooleanProperty LEFT = BooleanProperty.of("left");

    private static final VoxelShape[][] OUTLINE_SHAPES;
    private static final VoxelShape[][] COLLISION_SHAPES;

    static {
        OUTLINE_SHAPES = new VoxelShape[4][4];
        COLLISION_SHAPES = new VoxelShape[4][4];

        VoxelShape[] floorShapes = { //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 8, 16, 9, 16), Block.createCuboidShape(0, 16, 0, 16, 17, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 8, 9, 16), Block.createCuboidShape(8, 16, 0, 16, 17, 16)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 16, 9, 8), Block.createCuboidShape(0, 16, 8, 16, 17, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 0, 16, 9, 16), Block.createCuboidShape(0, 16, 0, 8, 17, 16))};

        VoxelShape[] leftOutlines = { //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 8, 2, 24, 16), Block.createCuboidShape(0, 16, 0, 2, 32, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 8, 24, 2), Block.createCuboidShape(8, 16, 0, 16, 32, 2)), //
                VoxelShapes.union(Block.createCuboidShape(14, 8, 0, 16, 24, 8), Block.createCuboidShape(14, 16, 8, 16, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 14, 16, 24, 16), Block.createCuboidShape(0, 16, 14, 8, 32, 16))};
        VoxelShape[] rightOutlines = { //
                VoxelShapes.union(Block.createCuboidShape(14, 8, 8, 16, 24, 16), Block.createCuboidShape(14, 16, 0, 16, 32, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 14, 8, 24, 16), Block.createCuboidShape(8, 16, 14, 16, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 2, 24, 8), Block.createCuboidShape(0, 16, 8, 2, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 0, 16, 24, 2), Block.createCuboidShape(0, 16, 0, 8, 32, 2))};

        VoxelShape[] leftCollisions = { //
                VoxelShapes.union(Block.createCuboidShape(0.5, 8, 8, 1, 24, 16), Block.createCuboidShape(0.5, 16, 0, 1, 32, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0.5, 8, 24, 1), Block.createCuboidShape(8, 16, 0.5, 16, 32, 1)), //
                VoxelShapes.union(Block.createCuboidShape(15, 8, 0, 15.5, 24, 8), Block.createCuboidShape(15, 16, 8, 15.5, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 15, 16, 24, 15.5), Block.createCuboidShape(0, 16, 15, 8, 32, 15.5))};
        VoxelShape[] rightCollisions = { //
                VoxelShapes.union(Block.createCuboidShape(15, 8, 8, 15.5, 24, 16), Block.createCuboidShape(15, 16, 0, 15.5, 32, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 15, 8, 24, 15.5), Block.createCuboidShape(8, 16, 15, 16, 32, 15.5)), //
                VoxelShapes.union(Block.createCuboidShape(0.5, 8, 0, 1, 24, 8), Block.createCuboidShape(0.5, 16, 8, 1, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 0.5, 16, 24, 1), Block.createCuboidShape(0, 16, 0.5, 8, 32, 1))};

        for (int i = 0; i < 4; i++) {
            COLLISION_SHAPES[i][0] = OUTLINE_SHAPES[i][0] = floorShapes[i];
            OUTLINE_SHAPES[i][1] = VoxelShapes.union(floorShapes[i], leftOutlines[i]);
            OUTLINE_SHAPES[i][2] = VoxelShapes.union(floorShapes[i], rightOutlines[i]);
            OUTLINE_SHAPES[i][3] = VoxelShapes.union(floorShapes[i], leftOutlines[i], rightOutlines[i]);

            COLLISION_SHAPES[i][1] = VoxelShapes.union(floorShapes[i], leftCollisions[i]);
            COLLISION_SHAPES[i][2] = VoxelShapes.union(floorShapes[i], rightCollisions[i]);
            COLLISION_SHAPES[i][3] = VoxelShapes.union(floorShapes[i], leftCollisions[i], rightCollisions[i]);
        }
    }

    public static final Block INSTANCE = new CatwalkStairsBlock(FabricBlockSettings.of(Material.METAL, MapColor.GRAY).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque());

    public CatwalkStairsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.LOWER).with(RIGHT, true).with(LEFT, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, HALF, RIGHT, LEFT);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int idx = (state.get(LEFT) ? 1 : 0) | (state.get(RIGHT) ? 2 : 0);
        VoxelShape voxelShape = OUTLINE_SHAPES[state.get(FACING).getHorizontal()][idx];

        return state.get(HALF) == DoubleBlockHalf.UPPER ? voxelShape.offset(0, -1, 0) : voxelShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int i = (state.get(LEFT) ? 1 : 0) | (state.get(RIGHT) ? 2 : 0);
        VoxelShape voxelShape = COLLISION_SHAPES[state.get(FACING).getHorizontal()][i];

        return state.get(HALF) == DoubleBlockHalf.UPPER ? voxelShape.offset(0, -1, 0) : voxelShape;
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getOutlineShape(state, world, pos, ShapeContext.absent());
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(CatwalkBlock.ITEM);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, world.getFluidState(pos.up()).getFluid() == Fluids.WATER), 3);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                dropStacks(state, world, pos, null, player, player.getMainHandStack());
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
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos);

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

        Direction facing = state.get(FACING);
        Direction left = facing.rotateYClockwise();
        Direction right = facing.rotateYCounterclockwise();
        return state.with(LEFT, this.shouldHaveHandrail(state, world, pos, left, HorizontalHalf.LEFT)).with(RIGHT, this.shouldHaveHandrail(state, world, pos, right, HorizontalHalf.RIGHT));
    }

    public boolean shouldHaveHandrail(BlockState state, WorldAccess world, BlockPos pos, Direction direction, HorizontalHalf half) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.down();
            state = state.with(HALF, DoubleBlockHalf.LOWER);
        }
        if (world.getBlockEntity(pos) instanceof CatwalkStairsBlockEntity catwalk) {
            Optional<ConnectionOverride> handrail = catwalk.getHandrailState(half);
            if (handrail.isPresent()) {
                return handrail.get() == ConnectionOverride.FORCED;
            }
        }
        BlockState neighbor = world.getBlockState(pos.offset(direction));
        BlockState above = world.getBlockState(pos.offset(direction).up());

        if (neighbor.isSideSolidFullSquare(world, pos.offset(direction), direction.getOpposite()) && neighbor.getMaterial() != Material.AGGREGATE && above.isSideSolidFullSquare(world, pos.offset(direction).up(), direction.getOpposite()) && above.getMaterial() != Material.AGGREGATE) {
            return false;
        }
        if (neighbor.getBlock() instanceof CatwalkStairsBlock) {
            if (neighbor.get(FACING) != state.get(FACING) || neighbor.get(HALF) != state.get(HALF)) {
                // stairs not matching
                return true;
            }
            return world.getBlockEntity(pos.offset(direction)) instanceof CatwalkStairsBlockEntity otherCatwalk && otherCatwalk.isHandrailForced(half.getOpposite());

        }
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean isSideInvisible(BlockState state, BlockState state2, Direction direction) {
        return direction.getAxis().isHorizontal() && state2.getBlock() == this && state.get(FACING) == direction && state2.get(FACING) == direction.getOpposite() && state.get(HALF) == DoubleBlockHalf.LOWER //
                || super.isSideInvisible(state, state2, direction);
    }

    public static Direction getSideDirection(Direction facing, HorizontalHalf half) {
        return half == HorizontalHalf.LEFT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
    }

    protected static BlockPos getLowerHalfPos(BlockState state, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            return pos.down();
        }
        return pos;
    }

    public static BooleanProperty getHandrailProperty(HorizontalHalf half) {
        return half == HorizontalHalf.LEFT ? LEFT : RIGHT;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isIn(WrenchItem.COMPATIBILITY_TAG)) {
            if (!world.isClient && this.useWrench(state, world, pos, hit.getSide(), player, hand, hit.getPos())) {
                if (stack.isDamageable()) {
                    stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public boolean useWrench(BlockState state, World world, BlockPos pos, Direction direction, PlayerEntity player, Hand hand, Vec3d hitPos) {
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

        HorizontalHalf half = CatwalksUtil.getTargettedSide(pos, hitPos, state.get(FACING));

        be.useWrench(half, state, player);

        BlockState newState = state.with(getHandrailProperty(half), be.getHandrailState(half).map(override -> override == ConnectionOverride.FORCED).orElse(this.shouldHaveHandrail(state, world, pos, direction, half)));
        world.setBlockState(pos, newState);
        world.setBlockState(pos.up(), newState.with(HALF, DoubleBlockHalf.UPPER));

        if (be.canBeRemoved()) {
            world.removeBlockEntity(pos);
        }

        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
