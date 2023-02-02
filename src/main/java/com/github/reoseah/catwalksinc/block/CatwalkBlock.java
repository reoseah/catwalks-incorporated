package com.github.reoseah.catwalksinc.block;

import com.github.reoseah.catwalksinc.CatwalksInc;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class CatwalkBlock extends WaterloggableBlock {
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;

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

    public static final Block INSTANCE = new CatwalkBlock(FabricBlockSettings.of(Material.METAL, MapColor.GRAY).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque());
    public static final Item ITEM = new BlockItem(INSTANCE, new FabricItemSettings().group(CatwalksInc.ITEM_GROUP));

    public CatwalkBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState() //
                .with(SOUTH, true).with(WEST, true) //
                .with(NORTH, true).with(EAST, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SOUTH, EAST, NORTH, WEST);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return context.isHolding(this.asItem()) && !context.isDescending() ? VoxelShapes.fullCube() : OUTLINE_SHAPES[getShapeIndex(state)];
    }

    protected static int getShapeIndex(BlockState state) {
        return (state.get(SOUTH) ? 1 : 0) //
                | (state.get(WEST) ? 2 : 0) //
                | (state.get(NORTH) ? 4 : 0) //
                | (state.get(EAST) ? 8 : 0);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES[getShapeIndex(state)];
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return OUTLINE_SHAPES[getShapeIndex(state)];
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        return super.getPlacementState(ctx) //
                .with(SOUTH, this.shouldHaveHandrail(world, pos, Direction.SOUTH)) //
                .with(WEST, this.shouldHaveHandrail(world, pos, Direction.WEST)) //
                .with(NORTH, this.shouldHaveHandrail(world, pos, Direction.NORTH)) //
                .with(EAST, this.shouldHaveHandrail(world, pos, Direction.EAST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos);

        if (direction.getAxis().isHorizontal()) {
            return state.with(getHandrailProperty(direction), this.shouldHaveHandrail(world, pos, direction));
        }
        return state;
    }

    public boolean shouldHaveHandrail(WorldAccess world, BlockPos pos, Direction side) {
        BlockPos neighborPos = pos.offset(side);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (neighbor.isOf(this)) {
            return false;
        }
        return !needsCatwalkAccess(neighbor, world, neighborPos, side.getOpposite());
    }

    public static boolean needsCatwalkAccess(BlockState state, WorldAccess world, BlockPos pos, Direction side) {
        Block block = state.getBlock();
        if (block instanceof DoorBlock) {
            return state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
        }
        if (block instanceof LadderBlock) {
            return state.get(LadderBlock.FACING) == side;
        }
        if (block instanceof InventoryProvider provider) {
            return provider.getInventory(state, world, pos) != null;
        }
        if (block instanceof AbstractCauldronBlock //
                || block instanceof HopperBlock //
                || block instanceof AbstractChestBlock<?>) {
            return true;
        }
        return state.isSideSolidFullSquare(world, pos, side) && !Block.cannotConnect(state);
    }

    public static BooleanProperty getHandrailProperty(Direction direction) {
        return switch (direction) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case NORTH -> NORTH;
            case EAST -> EAST;
            default -> throw new IncompatibleClassChangeError();
        };
    }
}
