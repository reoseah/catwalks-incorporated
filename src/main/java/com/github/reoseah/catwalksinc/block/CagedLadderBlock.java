package com.github.reoseah.catwalksinc.block;

import com.github.reoseah.catwalksinc.CatwalksInc;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

@SuppressWarnings("deprecation")
public class CagedLadderBlock extends WaterloggableBlock {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<CageState> CAGE = EnumProperty.of("cage", CageState.class);
    public static final BooleanProperty LADDER = BooleanProperty.of("ladder");

    public static final VoxelShape[] LADDER_ONLY_OUTLINE_SHAPES = { //
            Block.createCuboidShape(0, 0, 0, 16, 16, 4), //
            Block.createCuboidShape(12, 0, 0, 16, 16, 16), //
            Block.createCuboidShape(0, 0, 12, 16, 16, 16), //
            Block.createCuboidShape(0, 0, 0, 4, 16, 16), //
    };
    public static final VoxelShape[] LADDER_ONLY_COLLISION_SHAPES = { //
            Block.createCuboidShape(0, 0, 0, 16, 16, 1), //
            Block.createCuboidShape(15, 0, 0, 16, 16, 16), //
            Block.createCuboidShape(0, 0, 15, 16, 16, 16), //
            Block.createCuboidShape(0, 0, 0, 1, 16, 16), //
    };

    private static final VoxelShape SOUTH_OUTLINE = Block.createCuboidShape(0, 0, 0, 16, 16, 2);
    private static final VoxelShape WEST_OUTLINE = Block.createCuboidShape(14, 0, 0, 16, 16, 16);
    private static final VoxelShape NORTH_OUTLINE = Block.createCuboidShape(0, 0, 14, 16, 16, 16);
    private static final VoxelShape EAST_OUTLINE = Block.createCuboidShape(0, 0, 0, 2, 16, 16);

    public static final VoxelShape[] CAGE_ONLY_OUTLINE_SHAPES = { //
            VoxelShapes.union(WEST_OUTLINE, NORTH_OUTLINE, EAST_OUTLINE), //
            VoxelShapes.union(SOUTH_OUTLINE, NORTH_OUTLINE, EAST_OUTLINE), //
            VoxelShapes.union(SOUTH_OUTLINE, WEST_OUTLINE, EAST_OUTLINE), //
            VoxelShapes.union(SOUTH_OUTLINE, WEST_OUTLINE, NORTH_OUTLINE) //
    };
    public static final VoxelShape[] OUTLINE_SHAPES = { //
            VoxelShapes.union(LADDER_ONLY_OUTLINE_SHAPES[0], CAGE_ONLY_OUTLINE_SHAPES[0]), //
            VoxelShapes.union(LADDER_ONLY_OUTLINE_SHAPES[1], CAGE_ONLY_OUTLINE_SHAPES[1]), //
            VoxelShapes.union(LADDER_ONLY_OUTLINE_SHAPES[2], CAGE_ONLY_OUTLINE_SHAPES[2]), //
            VoxelShapes.union(LADDER_ONLY_OUTLINE_SHAPES[3], CAGE_ONLY_OUTLINE_SHAPES[3]) //
    };

    private static final VoxelShape SOUTH_COLLISION = Block.createCuboidShape(0, 0, 0, 16, 16, 0.5);
    private static final VoxelShape WEST_COLLISION = Block.createCuboidShape(15.5, 0, 0, 16, 16, 16);
    private static final VoxelShape NORTH_COLLISION = Block.createCuboidShape(0, 0, 15.5, 16, 16, 16);
    private static final VoxelShape EAST_COLLISION = Block.createCuboidShape(0, 0, 0, 0.5, 16, 16);

    public static final VoxelShape[] CAGE_ONLY_COLLISION_SHAPES = { //
            VoxelShapes.union(WEST_COLLISION, NORTH_COLLISION, EAST_COLLISION), //
            VoxelShapes.union(SOUTH_COLLISION, NORTH_COLLISION, EAST_COLLISION), //
            VoxelShapes.union(SOUTH_COLLISION, WEST_COLLISION, EAST_COLLISION), //
            VoxelShapes.union(SOUTH_COLLISION, WEST_COLLISION, NORTH_COLLISION) //

    };
    public static final VoxelShape[] COLLISION_SHAPES = { //
            VoxelShapes.union(LADDER_ONLY_COLLISION_SHAPES[0], CAGE_ONLY_COLLISION_SHAPES[0]), //
            VoxelShapes.union(LADDER_ONLY_COLLISION_SHAPES[1], CAGE_ONLY_COLLISION_SHAPES[1]), //
            VoxelShapes.union(LADDER_ONLY_COLLISION_SHAPES[2], CAGE_ONLY_COLLISION_SHAPES[2]), //
            VoxelShapes.union(LADDER_ONLY_COLLISION_SHAPES[3], CAGE_ONLY_COLLISION_SHAPES[3]) //
    };

    public static final Block INSTANCE = new CagedLadderBlock(FabricBlockSettings.of().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque());

    protected CagedLadderBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(CAGE, CageState.NORMAL).with(LADDER, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, CAGE, LADDER);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(CatwalksInc.CATWALK);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        int idx = facing.getHorizontal();
        return switch (state.get(CAGE)) {
            case NORMAL, HANDRAILS -> state.get(LADDER) ? COLLISION_SHAPES[idx] : CAGE_ONLY_COLLISION_SHAPES[idx];
            case NONE -> LADDER_ONLY_COLLISION_SHAPES[facing.getHorizontal()];
        };
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context.isHolding(CatwalksInc.CATWALK.asItem()) && !context.isDescending()) {
            return VoxelShapes.fullCube();
        }
        Direction facing = state.get(FACING);
        int idx = facing.getHorizontal();
        return switch (state.get(CAGE)) {
            case NORMAL, HANDRAILS -> state.get(LADDER) ? OUTLINE_SHAPES[idx] : CAGE_ONLY_OUTLINE_SHAPES[idx];
            case NONE -> LADDER_ONLY_OUTLINE_SHAPES[facing.getHorizontal()];
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        CageState cage = getCageState(ctx.getWorld(), ctx.getBlockPos());
        return super.getPlacementState(ctx).with(FACING, facing).with(CAGE, cage).with(LADDER, getLadderState(facing, cage, ctx.getWorld(), ctx.getBlockPos()));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        Direction facing = state.get(FACING);
        CageState cage = getCageState(world, pos);
        return super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos).with(CAGE, cage).with(LADDER, getLadderState(facing, cage, world, pos));

    }

    public static CageState getCageState(WorldAccess world, BlockPos pos) {
        BlockPos belowLadder = pos.down();
        BlockState stateBelowLadder = world.getBlockState(belowLadder);
        if (stateBelowLadder.isOf(CatwalksInc.CATWALK)) {
            return CageState.NONE;
        }

        if (stateBelowLadder.isSideSolidFullSquare(world, belowLadder, Direction.UP)) {
            return CageState.NONE;
        }
        if (stateBelowLadder.isOf(INSTANCE) && stateBelowLadder.get(CAGE) == CageState.NONE) {
            BlockPos belowBelowLadder = belowLadder.down();
            BlockState stateBelowBelowLadder = world.getBlockState(belowLadder);
            if (stateBelowBelowLadder.isSideSolidFullSquare(world, belowBelowLadder, Direction.UP)) {
                return CageState.NONE;
            }
        }

        BlockPos aboveLadder = pos.up();
        BlockState stateAboveLadder = world.getBlockState(aboveLadder);
        if (!stateAboveLadder.isOf(INSTANCE) && !stateAboveLadder.isSideSolidFullSquare(world, aboveLadder, Direction.DOWN)) {
            return CageState.HANDRAILS;
        }

        return CageState.NORMAL;
    }

    public static boolean getLadderState(Direction facing, CageState cage, WorldAccess world, BlockPos pos) {
        if (cage == CageState.NONE) {
            // cannot not have ladder if there's no cage, that would result in empty block
            return true;
        }
        BlockPos behindLadder = pos.offset(facing.getOpposite());
        BlockState stateBehindLadder = world.getBlockState(behindLadder);
        return CatwalkBlock.getConnectivity(stateBehindLadder, world, behindLadder, facing) != CatwalkBlock.Connectivity.NO_HANDRAIL;
    }
}
