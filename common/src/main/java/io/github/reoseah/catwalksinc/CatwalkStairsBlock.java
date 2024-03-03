package io.github.reoseah.catwalksinc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class CatwalkStairsBlock extends Block {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public static final BooleanProperty RIGHT = BooleanProperty.of("right");
    public static final BooleanProperty LEFT = BooleanProperty.of("left");

    private static final VoxelShape[] OUTLINE_SHAPES = createShapes(2);
    private static final VoxelShape[] COLLISION_SHAPES = createShapes(0.5);

    public static VoxelShape[] createShapes(double handrailThickness) {
        VoxelShape[] floors = { //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 8, 16, 9, 16), Block.createCuboidShape(0, 16, 0, 16, 17, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 8, 9, 16), Block.createCuboidShape(8, 16, 0, 16, 17, 16)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 16, 9, 8), Block.createCuboidShape(0, 16, 8, 16, 17, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 0, 16, 9, 16), Block.createCuboidShape(0, 16, 0, 8, 17, 16))};

        VoxelShape[] leftHandrails = { //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 8, handrailThickness, 24, 16), Block.createCuboidShape(0, 16, 0, handrailThickness, 32, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 8, 24, handrailThickness), Block.createCuboidShape(8, 16, 0, 16, 32, handrailThickness)), //
                VoxelShapes.union(Block.createCuboidShape(16 - handrailThickness, 8, 0, 16, 24, 8), Block.createCuboidShape(16 - handrailThickness, 16, 8, 16, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 16 - handrailThickness, 16, 24, 16), Block.createCuboidShape(0, 16, 16 - handrailThickness, 8, 32, 16))};
        VoxelShape[] rightHandrails = { //
                VoxelShapes.union(Block.createCuboidShape(16 - handrailThickness, 8, 8, 16, 24, 16), Block.createCuboidShape(16 - handrailThickness, 16, 0, 16, 32, 8)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 16 - handrailThickness, 8, 24, 16), Block.createCuboidShape(8, 16, 16 - handrailThickness, 16, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(0, 8, 0, handrailThickness, 24, 8), Block.createCuboidShape(0, 16, 8, handrailThickness, 32, 16)), //
                VoxelShapes.union(Block.createCuboidShape(8, 8, 0, 16, 24, handrailThickness), Block.createCuboidShape(0, 16, 0, 8, 32, handrailThickness))};

        VoxelShape[] shapes = new VoxelShape[16];
        for (int facing = 0; facing < 4; facing++) {
            shapes[facing] = floors[facing];
            shapes[0b0100 | facing] = VoxelShapes.union(floors[facing], leftHandrails[facing]);
            shapes[0b1000 | facing] = VoxelShapes.union(floors[facing], rightHandrails[facing]);
            shapes[0b1100 | facing] = VoxelShapes.union(floors[facing], leftHandrails[facing], rightHandrails[facing]);
        }
        return shapes;
    }

    public static int getShapeIndex(Direction facing, boolean left, boolean right) {
        return (facing.getHorizontal()) | (left ? 0b0100 : 0) | (right ? 0b1000 : 0);
    }

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
        VoxelShape shape = OUTLINE_SHAPES[getShapeIndex(state.get(FACING), state.get(LEFT), state.get(RIGHT))];
        return state.get(HALF) == DoubleBlockHalf.UPPER ? shape.offset(0, -1, 0) : shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = COLLISION_SHAPES[getShapeIndex(state.get(FACING), state.get(LEFT), state.get(RIGHT))];
        return state.get(HALF) == DoubleBlockHalf.UPPER ? shape.offset(0, -1, 0) : shape;
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getOutlineShape(state, world, pos, ShapeContext.absent());
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(CatwalksInc.CATWALK);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        // place upper part
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER)
//                .with(WATERLOGGED, world.getFluidState(pos.up()).getFluid() == Fluids.WATER)
                , 3);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            if (player.isCreative()) {
                DoubleBlockHalf half = state.get(HALF);
                if (half == DoubleBlockHalf.UPPER) {
                    BlockPos below = pos.down();
                    BlockState stateBelow = world.getBlockState(below);
                    if (stateBelow.getBlock() == state.getBlock() && stateBelow.get(HALF) == DoubleBlockHalf.LOWER) {
                        world.setBlockState(below, Blocks.AIR.getDefaultState(), 35);
                        world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, below, Block.getRawIdFromState(stateBelow));
                    }
                }
            } else {
                dropStacks(state, world, pos, null, player, player.getMainHandStack());
            }
        }

        super.onBreak(world, pos, state, player);
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
        return state.with(LEFT, this.shouldHaveHandrail(state, world, pos, left, HorizontalHalf.LEFT)) //
                .with(RIGHT, this.shouldHaveHandrail(state, world, pos, right, HorizontalHalf.RIGHT));
    }

    public boolean shouldHaveHandrail(BlockState state, WorldAccess world, BlockPos pos, Direction direction, HorizontalHalf half) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.down();
            state = state.with(HALF, DoubleBlockHalf.LOWER);
        }
//        if (world.getBlockEntity(pos) instanceof CatwalkStairsBlockEntity catwalk) {
//            Optional<ConnectionOverride> handrail = catwalk.getHandrailState(half);
//            if (handrail.isPresent()) {
//                return handrail.get() == ConnectionOverride.FORCED;
//            }
//        }
        BlockState neighbor = world.getBlockState(pos.offset(direction));
        BlockState above = world.getBlockState(pos.offset(direction).up());

        if (neighbor.isSideSolidFullSquare(world, pos.offset(direction), direction.getOpposite()) //
                && above.isSideSolidFullSquare(world, pos.offset(direction).up(), direction.getOpposite())) {
            return false;
        }
        if (neighbor.getBlock() instanceof CatwalkStairsBlock) {
            if (neighbor.get(FACING) != state.get(FACING) || neighbor.get(HALF) != state.get(HALF)) {
                // stairs not matching
                return true;
            }
//            return world.getBlockEntity(pos.offset(direction)) instanceof CatwalkStairsBlockEntity otherCatwalk && otherCatwalk.isHandrailForced(half.getOpposite());
            return false;
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

    public enum HorizontalHalf {
        LEFT, RIGHT;

        public HorizontalHalf getOpposite() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }
}
