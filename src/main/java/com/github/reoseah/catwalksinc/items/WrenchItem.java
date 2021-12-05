package com.github.reoseah.catwalksinc.items;

import com.github.reoseah.catwalksinc.blocks.Wrenchable;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WrenchItem extends Item {
	public WrenchItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock()instanceof Wrenchable wrenchable //
				&& wrenchable.useWrench(state, world, pos, context.getSide(), context.getPlayer(), context.getHand(),
						context.getHitPos())) {
			context.getStack().damage(1, context.getPlayer(), player -> {
				player.sendToolBreakStatus(context.getHand());
			});

			return ActionResult.SUCCESS;

		}
		return super.useOnBlock(context);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.IRON_INGOT;
	}
}
