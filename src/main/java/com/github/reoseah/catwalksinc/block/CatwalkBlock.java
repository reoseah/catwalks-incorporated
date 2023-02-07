package com.github.reoseah.catwalksinc.block;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.lib.multipart.api.NativeMultipart;
import com.github.reoseah.catwalksinc.CatwalksInc;
import com.github.reoseah.catwalksinc.item.WrenchItem;
import com.github.reoseah.catwalksinc.part.CatwalkPart;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class CatwalkBlock extends CatwalksIncBlock implements NativeMultipart {
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;

    public static final VoxelShape[] OUTLINE_SHAPES;
    public static final VoxelShape[] COLLISION_SHAPES;

    public static final VoxelShape FLOOR_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public static final VoxelShape CUTOUT_SHAPE = VoxelShapes.union( //
            Block.createCuboidShape(0, 2, 2, 16, 13, 14), //
            Block.createCuboidShape(2, 2, 0, 14, 13, 16));
    public static final VoxelShape SOUTH_HANDRAIL_SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 0, 14, 16, 16, 16), CUTOUT_SHAPE, BooleanBiFunction.ONLY_FIRST);
    public static final VoxelShape WEST_HANDRAIL_SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 0, 0, 2, 16, 16), CUTOUT_SHAPE, BooleanBiFunction.ONLY_FIRST);
    public static final VoxelShape NORTH_HANDRAIL_SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 0, 0, 16, 16, 2), CUTOUT_SHAPE, BooleanBiFunction.ONLY_FIRST);
    public static final VoxelShape EAST_HANDRAIL_SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(14, 0, 0, 16, 16, 16), CUTOUT_SHAPE, BooleanBiFunction.ONLY_FIRST);

    static {
        OUTLINE_SHAPES = new VoxelShape[16];
        COLLISION_SHAPES = new VoxelShape[16];

        // collision shapes are only half-pixel thick
        // otherwise you bump into edges of handrails too much
        VoxelShape floorCollision = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
        VoxelShape southCollision = Block.createCuboidShape(0, 0, 15.5, 16, 16, 16);
        VoxelShape westCollision = Block.createCuboidShape(0, 0, 0, 0.5, 16, 16);
        VoxelShape northColl = Block.createCuboidShape(0, 0, 0, 16, 16, 0.5);
        VoxelShape eastColl = Block.createCuboidShape(15.5, 0, 0, 16, 16, 16);

        for (int i = 0; i < 16; i++) {
            VoxelShape outline = FLOOR_SHAPE;
            VoxelShape collision = floorCollision;
            if ((i & 1) != 0) {
                outline = VoxelShapes.union(outline, SOUTH_HANDRAIL_SHAPE);
                collision = VoxelShapes.union(collision, southCollision);
            }
            if ((i & 2) != 0) {
                outline = VoxelShapes.union(outline, WEST_HANDRAIL_SHAPE);
                collision = VoxelShapes.union(collision, westCollision);
            }
            if ((i & 4) != 0) {
                outline = VoxelShapes.union(outline, NORTH_HANDRAIL_SHAPE);
                collision = VoxelShapes.union(collision, northColl);
            }
            if ((i & 8) != 0) {
                outline = VoxelShapes.union(outline, EAST_HANDRAIL_SHAPE);
                collision = VoxelShapes.union(collision, eastColl);
            }
            OUTLINE_SHAPES[i] = VoxelShapes.combineAndSimplify(outline, CUTOUT_SHAPE, BooleanBiFunction.ONLY_FIRST);
            COLLISION_SHAPES[i] = VoxelShapes.combineAndSimplify(collision, CUTOUT_SHAPE, BooleanBiFunction.ONLY_FIRST);
        }
    }

    public static final Block INSTANCE = new CatwalkBlock(FabricBlockSettings.of(Material.METAL, MapColor.GRAY).sounds(BlockSoundGroup.LANTERN).strength(2F, 10F).nonOpaque());
    public static final Item ITEM = new BlockItem(INSTANCE, new FabricItemSettings().group(CatwalksInc.ITEM_GROUP));

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
        return context.isHolding(this.asItem()) && !context.isDescending() ? VoxelShapes.fullCube() : OUTLINE_SHAPES[getShapeIndex(state.get(SOUTH), state.get(WEST), state.get(NORTH), state.get(EAST))];
    }

    public static int getShapeIndex(boolean south, boolean west, boolean north, boolean east) {
        int result = 0;
        if (south) {
            result |= 1;
        }
        if (west) {
            result |= 0b10;
        }
        if (north) {
            result |= 0b100;
        }
        if (east) {
            result |= 0b1000;
        }
        return result;
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

        if (world.getBlockState(pos).canReplace(ctx) //
                && world.getBlockState(pos.up()).canReplace(ItemPlacementContext.offset(ctx, pos.up(), Direction.DOWN))) {
            Optional<Direction> stairsUpFacing = checkForStairsPlacementAbove(world, pos);
            if (stairsUpFacing.isPresent()) {
                return CatwalkStairsBlock.INSTANCE.getDefaultState() //
                        .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER) //
                        .with(CatwalkStairsBlock.FACING, stairsUpFacing.get().getOpposite());
            }
        }

        return super.getPlacementState(ctx) //
                .with(SOUTH, shouldHaveHandrail(world, pos, Direction.SOUTH)) //
                .with(WEST, shouldHaveHandrail(world, pos, Direction.WEST)) //
                .with(NORTH, shouldHaveHandrail(world, pos, Direction.NORTH)) //
                .with(EAST, shouldHaveHandrail(world, pos, Direction.EAST));
    }

    protected static Optional<Direction> checkForStairsPlacementAbove(WorldAccess world, BlockPos pos) {
        for (Direction facing : Direction.Type.HORIZONTAL) {
            BlockPos exitPos = pos.up().offset(facing);
            BlockState exitState = world.getBlockState(exitPos);
            if (needsCatwalkConnection(exitState, world, exitPos, facing.getOpposite())) {
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
        BlockPos neighborPos = pos.offset(side);
        BlockState neighbor = world.getBlockState(neighborPos);

        return !needsCatwalkConnection(neighbor, world, neighborPos, side.getOpposite()) && !needsCatwalkAccess(neighbor, world, neighborPos, side.getOpposite());
    }

    public static boolean needsCatwalkConnection(BlockState state, WorldAccess world, BlockPos pos, Direction side) {
        if (state.isOf(INSTANCE)) {
            return true;
        }
        if (state.isOf(CatwalkStairsBlock.INSTANCE)) {
            if (state.get(CatwalkStairsBlock.HALF) == DoubleBlockHalf.LOWER) {
                return side == state.get(CatwalkStairsBlock.FACING);
            } else {
                return side.getOpposite() == state.get(CatwalkStairsBlock.FACING);
            }
        }
        MultipartContainer container = MultipartUtil.get(world, pos);
        if (container != null) {
            for (AbstractPart part : container.getAllParts()) {
                if (part instanceof CatwalkPart catwalk) {
                    return !catwalk.isHandrailForced(side.getOpposite());
                }
            }
        }
        return false;
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

    @Nullable
    @Override
    public List<MultipartContainer.MultipartCreator> getMultipartConversion(World world, BlockPos pos, BlockState state) {
        return ImmutableList.of(holder -> new CatwalkPart(CatwalkPart.DEFINITION, holder, state.get(NORTH), state.get(WEST), state.get(SOUTH), state.get(EAST)));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isIn(WrenchItem.COMPATIBILITY_TAG)) {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            }
            MultipartContainer container = MultipartUtil.turnIntoMultipart(world, pos);
            if (container != null) {
                for (AbstractPart part : container.getAllParts()) {
                    if (part instanceof CatwalkPart catwalk) {
                        return catwalk.onUse(player, hand, hit);
                    }
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.getBlockState(pos.down()).isOf(CatwalkBlock.INSTANCE)) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos checkEmptyPos = pos.offset(direction);
                BlockPos checkCatwalkPos = checkEmptyPos.down();

                BlockState checkEmptyState = world.getBlockState(checkEmptyPos);
                if (!checkEmptyState.getMaterial().isReplaceable()) {
                    // there's a block that prevents catwalk from turning to stairs to connect to us
                    continue;
                }
                BlockState checkCatwalkState = world.getBlockState(checkCatwalkPos);
                if (!checkCatwalkState.isOf(CatwalkBlock.INSTANCE)) {
                    continue;
                }
                BlockState catwalkState = CatwalkStairsBlock.INSTANCE.getDefaultState() //
                        .with(CatwalkStairsBlock.FACING, direction).with(CatwalkStairsBlock.HALF, DoubleBlockHalf.LOWER);
                world.setBlockState(checkCatwalkPos, catwalkState);
                world.setBlockState(checkEmptyPos, catwalkState.with(CatwalkStairsBlock.HALF, DoubleBlockHalf.UPPER));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        tooltip.add(Text.translatable("block.catwalksinc.catwalk.desc.0"));
    }
}