package com.github.reoseah.catwalksinc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CageLampBlock extends CIncDecorationBlock implements Wrenchable {
    public static final VoxelShape[] SHAPES = { //
            Block.createCuboidShape(4, 6, 4, 12, 16, 12), //
            Block.createCuboidShape(4, 0, 4, 12, 10, 12), //
            Block.createCuboidShape(4, 4, 6, 12, 12, 16), //
            Block.createCuboidShape(4, 4, 0, 12, 12, 10), //
            Block.createCuboidShape(6, 4, 4, 16, 12, 12), //
            Block.createCuboidShape(0, 4, 4, 10, 12, 12), //
    };

    public CageLampBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).getId()];
    }
}
