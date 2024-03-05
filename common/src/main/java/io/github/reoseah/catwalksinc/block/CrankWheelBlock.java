package io.github.reoseah.catwalksinc.block;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

@SuppressWarnings("deprecation")
public class CrankWheelBlock extends WallDecorationBlock {
    public static final IntProperty ROTATION = Properties.ROTATION;

    public static final VoxelShape[] SHAPES = { //
            Block.createCuboidShape(3, 11, 3, 13, 16, 13), //
            Block.createCuboidShape(3, 0, 3, 13, 5, 13), //
            Block.createCuboidShape(3, 3, 11, 13, 13, 16), //
            Block.createCuboidShape(3, 3, 0, 13, 13, 5), //
            Block.createCuboidShape(11, 3, 3, 16, 13, 13), //
            Block.createCuboidShape(0, 3, 3, 5, 13, 13), //
    };

    public CrankWheelBlock(Settings settings) {
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
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(FACING) == direction) {
            return state.get(ROTATION);
        }
        return 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(ROTATION);
    }

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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction facing = state.get(FACING);
        if (facing.getAxis() == Direction.Axis.Y) {
            facing = player.getHorizontalFacing().getOpposite();
        }
        CatwalkStairsBlock.StairSide half = CatwalkStairsBlock.getTargetedSide(pos, hit.getPos(), facing);

        int rotation = state.get(ROTATION);
        int newRotation = half == CatwalkStairsBlock.StairSide.RIGHT ? Math.min(15, rotation + 1) : Math.max(0, rotation - 1);

        if (world.isClient) {
            if (newRotation != 0 && newRotation != rotation) {
                spawnParticles(state.get(FACING).getOpposite(), world, pos);
            }
        } else {
            BlockState newState = state.with(ROTATION, newRotation);

            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
            this.updateNeighbors(newState, world, pos);
            float pitch = world.random.nextFloat() * 0.1F + (newRotation > 0 ? 0.6f : 0.5f);
            if (newState != state) {
                // world.playSound(null, pos, CIncSoundEvents.CRANK_WHEEL_USE, SoundCategory.BLOCKS, 0.3f, pitch);
                world.emitGameEvent(player, newState.get(ROTATION) > 0 ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
            }
        }
        return ActionResult.SUCCESS;
    }

    public static void spawnParticles(Direction direction, WorldAccess world, BlockPos pos) {
        double x = pos.getX() + 0.5 + 0.3 * direction.getOffsetX();
        double y = pos.getY() + 0.5 + 0.3 * direction.getOffsetY();
        double z = pos.getZ() + 0.5 + 0.3 * direction.getOffsetZ();
        world.addParticle(new DustParticleEffect(DustParticleEffect.RED, 1.0F), x, y, z, 0.0, 0.0, 0.0);
    }
}