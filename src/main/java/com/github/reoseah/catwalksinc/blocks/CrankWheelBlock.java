package com.github.reoseah.catwalksinc.blocks;

import com.github.reoseah.catwalksinc.CISounds;
import com.github.reoseah.catwalksinc.util.Side;
import com.github.reoseah.catwalksinc.util.WrenchHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class CrankWheelBlock extends RotatableDecorationBlock {
	public static final IntProperty ROTATION = Properties.ROTATION;

	public static final VoxelShape[] SHAPES = { //
			Block.createCuboidShape(3, 11, 3, 13, 16, 13), //
			Block.createCuboidShape(3, 0, 3, 13, 5, 13), //
			Block.createCuboidShape(3, 3, 11, 13, 13, 16), //
			Block.createCuboidShape(3, 3, 0, 13, 13, 5), //
			Block.createCuboidShape(11, 3, 3, 16, 13, 13), //
			Block.createCuboidShape(0, 3, 3, 5, 13, 13), //
	};

	public CrankWheelBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(ROTATION, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(ROTATION);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES[state.get(FACING).getId()];
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		if (world.isClient) {
			BlockState blockState = state.cycle(ROTATION);
			if (blockState.get(ROTATION) > 0) {
				spawnParticles(blockState, world, pos, 1.0f);
			}
			return ActionResult.SUCCESS;
		}
		BlockState blockState = this.togglePower(state, world, pos, hit, player);
		float f = world.random.nextFloat() * 0.1F + blockState.get(ROTATION) > 0 ? 0.6f : 0.5f;
		if (blockState != state) {
			world.playSound(null, pos, CISounds.CRANK_WHEEL_USE, SoundCategory.BLOCKS, 0.3f, f);
			world.emitGameEvent(player,
					blockState.get(ROTATION) > 0 ? GameEvent.BLOCK_SWITCH : GameEvent.BLOCK_UNSWITCH, pos);
		}
		return ActionResult.CONSUME;
	}

	private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
		Direction direction = state.get(FACING).getOpposite();
		Direction direction2 = state.get(FACING).getOpposite();
		double d = pos.getX() + 0.5 + 0.1 * direction.getOffsetX() + 0.2 * direction2.getOffsetX();
		double e = pos.getY() + 0.5 + 0.1 * direction.getOffsetY() + 0.2 * direction2.getOffsetY();
		double f = pos.getZ() + 0.5 + 0.1 * direction.getOffsetZ() + 0.2 * direction2.getOffsetZ();
		world.addParticle(new DustParticleEffect(DustParticleEffect.RED, alpha), d, e, f, 0.0, 0.0, 0.0);
	}

	public BlockState togglePower(BlockState state, World world, BlockPos pos, BlockHitResult hitResult,
			PlayerEntity player) {
		Direction facing = state.get(FACING);
		if (facing.getAxis() == Axis.Y) {
			facing = player.getHorizontalFacing().getOpposite();
		}
		Side side = WrenchHelper.getBlockHalf(pos, hitResult.getPos(), facing);
		if (side == Side.RIGHT) {
			state = state.with(ROTATION, Math.min(15, state.get(ROTATION) + 1));
		} else {
			state = state.with(ROTATION, Math.max(0, state.get(ROTATION) - 1));
		}

		world.setBlockState(pos, state, Block.NOTIFY_ALL);
		this.updateNeighbors(state, world, pos);
		return state;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (moved || state.isOf(newState.getBlock())) {
			return;
		}
		if (state.get(ROTATION) > 0) {
			this.updateNeighbors(state, world, pos);
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	private void updateNeighbors(BlockState state, World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(state.get(FACING).getOpposite()), this);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(ROTATION);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (state.get(FACING) == direction) {
			return state.get(ROTATION);
		}
		return 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}
}
