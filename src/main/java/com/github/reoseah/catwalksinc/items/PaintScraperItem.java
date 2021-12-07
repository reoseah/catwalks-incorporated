package com.github.reoseah.catwalksinc.items;

import com.github.reoseah.catwalksinc.blocks.PaintScrapableBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PaintScraperItem extends Item {
    public PaintScraperItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof PaintScrapableBlock) {
            PaintScrapableBlock scrapable = (PaintScrapableBlock) block;
            if (scrapable.canScrapPaint(state, world, pos)) {
                scrapable.scrapPaint(state, world, pos);

                context.getStack().damage(1, context.getPlayer(), player -> {
                    player.sendToolBreakStatus(context.getHand());
                });
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
        if (block == Blocks.WHITE_STAINED_GLASS
                || block == Blocks.ORANGE_STAINED_GLASS
                || block == Blocks.MAGENTA_STAINED_GLASS
                || block == Blocks.LIGHT_BLUE_STAINED_GLASS
                || block == Blocks.YELLOW_STAINED_GLASS
                || block == Blocks.LIME_STAINED_GLASS
                || block == Blocks.PINK_STAINED_GLASS
                || block == Blocks.GRAY_STAINED_GLASS
                || block == Blocks.LIGHT_GRAY_STAINED_GLASS
                || block == Blocks.CYAN_STAINED_GLASS
                || block == Blocks.PURPLE_STAINED_GLASS
                || block == Blocks.BLUE_STAINED_GLASS
                || block == Blocks.BROWN_STAINED_GLASS
                || block == Blocks.GREEN_STAINED_GLASS
                || block == Blocks.RED_STAINED_GLASS
                || block == Blocks.BLACK_STAINED_GLASS) {
            world.setBlockState(pos, Blocks.GLASS.getDefaultState());
            context.getStack().damage(1, context.getPlayer(), player -> {
                player.sendToolBreakStatus(context.getHand());
            });
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.getItem() == Items.IRON_INGOT;
    }
}
