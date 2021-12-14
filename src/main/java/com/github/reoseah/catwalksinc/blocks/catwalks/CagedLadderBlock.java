package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.github.reoseah.catwalksinc.CIBlocks;
import com.github.reoseah.catwalksinc.CIItems;
import com.github.reoseah.catwalksinc.blocks.CatwalkAccess;
import com.github.reoseah.catwalksinc.blocks.PaintScrapableBlock;
import com.github.reoseah.catwalksinc.blocks.Paintable;
import com.github.reoseah.catwalksinc.blocks.WaterloggableBlock;
import com.github.reoseah.catwalksinc.blocks.Wrenchable;
import com.github.reoseah.catwalksinc.blocks.catwalks.CatwalkBlock.PaintedCatwalkBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CagedLadderBlock extends WaterloggableBlock
		implements BlockEntityProvider, CatwalkAccess, Wrenchable, Paintable {
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty EXTENSION = BooleanProperty.of("extension");

	public static final VoxelShape[] OUTLINE_SHAPES = new VoxelShape[4];
	public static final VoxelShape[] EXTENSION_OUTLINE_SHAPES = new VoxelShape[4];
	static {
		VoxelShape south = Block.createCuboidShape(0, 0, 0, 16, 16, 2);
		VoxelShape west = Block.createCuboidShape(14, 0, 0, 16, 16, 16);
		VoxelShape north = Block.createCuboidShape(0, 0, 14, 16, 16, 16);
		VoxelShape east = Block.createCuboidShape(0, 0, 0, 2, 16, 16);

		OUTLINE_SHAPES[0] = VoxelShapes.union(MetalLadderBlock.OUTLINE_SHAPES[0], west, north, east);
		OUTLINE_SHAPES[1] = VoxelShapes.union(MetalLadderBlock.OUTLINE_SHAPES[1], south, north, east);
		OUTLINE_SHAPES[2] = VoxelShapes.union(MetalLadderBlock.OUTLINE_SHAPES[2], south, west, east);
		OUTLINE_SHAPES[3] = VoxelShapes.union(MetalLadderBlock.OUTLINE_SHAPES[3], south, west, north);

		EXTENSION_OUTLINE_SHAPES[0] = VoxelShapes.union(west, north, east);
		EXTENSION_OUTLINE_SHAPES[1] = VoxelShapes.union(south, north, east);
		EXTENSION_OUTLINE_SHAPES[2] = VoxelShapes.union(south, west, east);
		EXTENSION_OUTLINE_SHAPES[3] = VoxelShapes.union(south, west, north);
	}

	public static final VoxelShape[] COLLISION_SHAPES = new VoxelShape[4];
	public static final VoxelShape[] EXTENSION_COLLISION_SHAPES = new VoxelShape[4];
	static {
		VoxelShape south = Block.createCuboidShape(0, 0, 0, 16, 16, 0.5);
		VoxelShape west = Block.createCuboidShape(15.5, 0, 0, 16, 16, 16);
		VoxelShape north = Block.createCuboidShape(0, 0, 15.5, 16, 16, 16);
		VoxelShape east = Block.createCuboidShape(0, 0, 0, 0.5, 16, 16);

		COLLISION_SHAPES[0] = VoxelShapes.union(MetalLadderBlock.COLLISION_SHAPES[0], west, north, east);
		COLLISION_SHAPES[1] = VoxelShapes.union(MetalLadderBlock.COLLISION_SHAPES[1], south, north, east);
		COLLISION_SHAPES[2] = VoxelShapes.union(MetalLadderBlock.COLLISION_SHAPES[2], south, west, east);
		COLLISION_SHAPES[3] = VoxelShapes.union(MetalLadderBlock.COLLISION_SHAPES[3], south, west, north);

		EXTENSION_COLLISION_SHAPES[0] = VoxelShapes.union(west, north, east);
		EXTENSION_COLLISION_SHAPES[1] = VoxelShapes.union(south, north, east);
		EXTENSION_COLLISION_SHAPES[2] = VoxelShapes.union(south, west, east);
		EXTENSION_COLLISION_SHAPES[3] = VoxelShapes.union(south, west, north);
	}

	public CagedLadderBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(FACING, Direction.NORTH) //
				.with(EXTENSION, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING, EXTENSION);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int idx = state.get(FACING).getHorizontal();
		return state.get(EXTENSION) ? EXTENSION_COLLISION_SHAPES[idx] : COLLISION_SHAPES[idx];
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int idx = state.get(FACING).getHorizontal();
		return state.get(EXTENSION) ? EXTENSION_OUTLINE_SHAPES[idx] : OUTLINE_SHAPES[idx];
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state =  super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing().getOpposite());
		return state.with(EXTENSION, this.shouldChangeToExtension(state, ctx.getWorld(), ctx.getBlockPos()));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState,
			WorldAccess world, BlockPos pos, BlockPos posFrom) {
		super.getStateForNeighborUpdate(state, direction, newState, world, posFrom, pos);
		return state.with(EXTENSION, this.shouldChangeToExtension(state, world, pos));
	}

	public boolean shouldChangeToExtension(BlockState state, WorldAccess world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CagedLadderBlockEntity ladder) {
			Optional<ElementMode> mode = ladder.getLadderState();
			if (mode.isPresent()) {
				return mode.get() == ElementMode.ALWAYS;
			}
		}

		Direction supportDirection = state.get(FACING).getOpposite();
		BlockPos supportPos = pos.offset(supportDirection);
		BlockState support = world.getBlockState(supportPos);
		if (support.getBlock()instanceof CatwalkAccess catwalk
				&& catwalk.needsCatwalkConnectivity(support, world, supportPos, supportDirection.getOpposite())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		if (player.isSneaking()) {
			world.setBlockState(pos, state.cycle(FACING), 3);
			return true;
		}

		CagedLadderBlockEntity be = (CagedLadderBlockEntity) world.getBlockEntity(pos);
		if (be == null) {
			be = new CagedLadderBlockEntity(pos, state);
			world.addBlockEntity(be);
		}

		world.setBlockState(pos, be.useWrench(state, player));

		if (be.canBeRemoved()) {
			world.removeBlockEntity(pos);
		}

		return true;
	}

	@Override
	public boolean canPaintBlock(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
		Block block = PaintedCatwalkBlock.ofColor(color);
		return block != null;
	}

	@Override
	public void paintBlock(DyeColor color, BlockState state, WorldAccess world, BlockPos pos) {
		Block block = PaintedCatwalkBlock.ofColor(color);
		if (block != null) {
			world.setBlockState(pos, block.getDefaultState() //
					.with(FACING, state.get(FACING)) //
					.with(WATERLOGGED, state.get(WATERLOGGED)), //
					3);
		}
	}

	@Override
	public boolean needsCatwalkAccess(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return state.get(FACING) == side.getOpposite();
	}

	public static class PaintedCagedLadderBlock extends CagedLadderBlock implements PaintScrapableBlock {
		protected static final Map<DyeColor, Block> INSTANCES = new EnumMap<>(DyeColor.class);

		protected final DyeColor color;

		public PaintedCagedLadderBlock(DyeColor color, Block.Settings settings) {
			super(settings);
			this.color = color;
			INSTANCES.put(color, this);
		}

		public static Block ofColor(DyeColor color) {
			return INSTANCES.get(color);
		}

		@Override
		public String getTranslationKey() {
			return CIItems.CAGED_LADDER.getTranslationKey();
		}

		@Override
		public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
			super.appendTooltip(stack, world, tooltip, options);
			tooltip.add(new TranslatableText("misc.catwalksinc." + this.color.asString()).formatted(Formatting.GRAY));
		}

		@Override
		public boolean canPaintBlock(DyeColor color, BlockState state, BlockView world, BlockPos pos) {
			return false;
		}

		@Override
		public void scrapPaint(BlockState state, WorldAccess world, BlockPos pos) {
			world.setBlockState(pos, CIBlocks.CAGED_LADDER.getDefaultState() //
					.with(FACING, state.get(FACING)) //
					.with(WATERLOGGED, state.get(WATERLOGGED)), //
					3);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return null;
	}
}
