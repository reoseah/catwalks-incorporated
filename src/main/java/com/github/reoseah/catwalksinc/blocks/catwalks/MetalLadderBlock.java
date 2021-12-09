package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.github.reoseah.catwalksinc.CIBlocks;
import com.github.reoseah.catwalksinc.CIItems;
import com.github.reoseah.catwalksinc.blocks.PaintScrapableBlock;
import com.github.reoseah.catwalksinc.blocks.Paintable;
import com.github.reoseah.catwalksinc.blocks.WaterloggableBlock;
import com.github.reoseah.catwalksinc.blocks.Wrenchable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MetalLadderBlock extends WaterloggableBlock implements CatwalkAccessible, Wrenchable, Paintable {
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

	public static final VoxelShape[] OUTLINE_SHAPES = { //
			Block.createCuboidShape(0, 0, 0, 16, 16, 4), //
			Block.createCuboidShape(12, 0, 0, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 12, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 0, 4, 16, 16), //
	};

	public static final VoxelShape[] COLLISION_SHAPES = { //
			Block.createCuboidShape(0, 0, 0, 16, 16, 1), //
			Block.createCuboidShape(15, 0, 0, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 15, 16, 16, 16), //
			Block.createCuboidShape(0, 0, 0, 1, 16, 16), //
	};

	public MetalLadderBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState() //
				.with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPES[state.get(FACING).getHorizontal()];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION_SHAPES[state.get(FACING).getHorizontal()];
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx) //
				.with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player,
			Hand hand, Vec3d hitPos) {
		world.setBlockState(pos, state.cycle(FACING), 3);
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
	public boolean shouldCatwalksDisableHandrail(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return state.get(FACING) == side;
	}

	public static class PaintedLadderBlock extends MetalLadderBlock implements PaintScrapableBlock {
		protected static final Map<DyeColor, Block> INSTANCES = new EnumMap<>(DyeColor.class);

		protected final DyeColor color;

		public PaintedLadderBlock(DyeColor color, Block.Settings settings) {
			super(settings);
			this.color = color;
			INSTANCES.put(color, this);
		}

		public static Block ofColor(DyeColor color) {
			return INSTANCES.get(color);
		}

		@Override
		public String getTranslationKey() {
			return CIItems.INDUSTRIAL_LADDER.getTranslationKey();
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
			world.setBlockState(pos, CIBlocks.INDUSTRIAL_LADDER.getDefaultState() //
					.with(FACING, state.get(FACING)) //
					.with(WATERLOGGED, state.get(WATERLOGGED)), //
					3);
		}
	}
}
