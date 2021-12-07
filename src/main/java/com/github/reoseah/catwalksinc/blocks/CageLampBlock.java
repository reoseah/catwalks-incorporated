package com.github.reoseah.catwalksinc.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CageLampBlock extends WaterloggableBlock implements Wrenchable {
	public static final EnumProperty<Direction> FACING = Properties.FACING;

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
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.DOWN));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES[state.get(FACING).getId()];
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getSide());
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		world.setBlockState(pos, state.cycle(FACING), 3);
		return true;
	}
}
