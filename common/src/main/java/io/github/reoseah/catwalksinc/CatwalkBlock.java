package io.github.reoseah.catwalksinc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CatwalkBlock extends Block {
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;

    public static final VoxelShape[] OUTLINE_SHAPES = createShapes(2);
    // thinner to not bump into the edges of the handrails as much
    public static final VoxelShape[] COLLISION_SHAPES = createShapes(0.5);

    public static VoxelShape[] createShapes(double handrailThickness) {
        // a hole in the middle of each handrail for arrows and alike to pass through
        VoxelShape centerCutout = VoxelShapes.union( //
                Block.createCuboidShape(0, 2, 2, 16, 13, 14), //
                Block.createCuboidShape(2, 2, 0, 14, 13, 16));

        VoxelShape floorShape = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
        VoxelShape southHandrailShape = Block.createCuboidShape(0, 0, 16 - handrailThickness, 16, 16, 16);
        VoxelShape westHandrailShape = Block.createCuboidShape(0, 0, 0, handrailThickness, 16, 16);
        VoxelShape northHandrailShape = Block.createCuboidShape(0, 0, 0, 16, 16, handrailThickness);
        VoxelShape eastHandrailShape = Block.createCuboidShape(16 - handrailThickness, 0, 0, 16, 16, 16);

        VoxelShape[] shapes = new VoxelShape[16];
        for (int i = 0; i < 16; i++) {
            VoxelShape shape = floorShape;
            if ((i & 1) != 0) {
                shape = VoxelShapes.union(shape, southHandrailShape);
            }
            if ((i & 2) != 0) {
                shape = VoxelShapes.union(shape, westHandrailShape);
            }
            if ((i & 4) != 0) {
                shape = VoxelShapes.union(shape, northHandrailShape);
            }
            if ((i & 8) != 0) {
                shape = VoxelShapes.union(shape, eastHandrailShape);
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
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context.isHolding(this.asItem()) && !context.isDescending()) {
            // it helps to place catwalk blocks against already placed catwalk
            return VoxelShapes.fullCube();
        }
        return OUTLINE_SHAPES[getShapeIndex(state.get(SOUTH), state.get(WEST), state.get(NORTH), state.get(EAST))];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES[getShapeIndex(state.get(SOUTH), state.get(WEST), state.get(NORTH), state.get(EAST))];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return OUTLINE_SHAPES[getShapeIndex(state.get(SOUTH), state.get(WEST), state.get(NORTH), state.get(EAST))];
    }
}
