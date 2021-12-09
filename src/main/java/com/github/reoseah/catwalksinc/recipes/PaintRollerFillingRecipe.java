package com.github.reoseah.catwalksinc.recipes;

import java.util.List;

import com.github.reoseah.catwalksinc.CIItems;
import com.github.reoseah.catwalksinc.CIRecipeSerializers;
import com.github.reoseah.catwalksinc.items.PaintRollerItem;
import com.google.common.collect.Lists;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PaintRollerFillingRecipe extends SpecialCraftingRecipe {
	public PaintRollerFillingRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		ItemStack paintroller = ItemStack.EMPTY;

		DyeColor color = null;
		List<ItemStack> dyes = Lists.newArrayList();
		for (int i = 0; i < inventory.size(); ++i) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() == CIItems.PAINT_ROLLER || stack.getItem() instanceof PaintRollerItem) {
				if (paintroller.isEmpty()) {
					paintroller = stack;
				} else {
					return false;
				}
			} else if (stack.getItem() instanceof DyeItem) {
				DyeColor color2 = ((DyeItem) stack.getItem()).getColor();
				if (color == null) {
					color = color2;
				} else if (color != color2) {
					return false;
				}
				dyes.add(stack);
			} else {
				return false;
			}
		}
		Item item = paintroller.getItem();
		if (item instanceof PaintRollerItem) {
			PaintRollerItem item2 = (PaintRollerItem) item;
			DyeColor paintrollerColor = ((PaintRollerItem) item).getColor();
			if (paintrollerColor != color) {
				return false;
			}
			int uses = item2.getMaxPaint() - item2.getDamage(paintroller);
			int maxAdded = 4 - (int) Math.ceil(uses / 8F);
			if (dyes.size() > maxAdded) {
				return false;
			}
		} else {
			if (PaintRollerItem.byColor(color) == null) {
				return false;
			}
		}

		return !paintroller.isEmpty() && !dyes.isEmpty() && dyes.size() <= 4;
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		ItemStack paintroller = ItemStack.EMPTY;
		DyeColor color = null;
		List<ItemStack> dyes = Lists.newArrayList();
		for (int i = 0; i < inventory.size(); ++i) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() == CIItems.PAINT_ROLLER || stack.getItem() instanceof PaintRollerItem) {
				if (paintroller.isEmpty()) {
					paintroller = stack;
				} else {
					return ItemStack.EMPTY;
				}
			} else if (stack.getItem() instanceof DyeItem) {
				DyeColor color2 = ((DyeItem) stack.getItem()).getColor();
				if (color == null) {
					color = color2;
				} else if (color != color2) {
					return ItemStack.EMPTY;
				}
				dyes.add(stack);
			} else {
				return ItemStack.EMPTY;
			}
		}
		Item item = paintroller.getItem();
		if (item instanceof PaintRollerItem) {
			PaintRollerItem item2 = (PaintRollerItem) item;
			DyeColor paintrollerColor = ((PaintRollerItem) item).getColor();
			if (paintrollerColor != color) {
				return ItemStack.EMPTY;
			}
			int uses = item2.getMaxPaint() - item2.getDamage(paintroller);
			int maxAdded = 4 - (int) Math.ceil(uses / 8F);
			if (dyes.size() > maxAdded) {
				return ItemStack.EMPTY;
			}
		}
		if (!paintroller.isEmpty() && !dyes.isEmpty() && dyes.size() <= 4) {
			ItemStack paintroller2 = new ItemStack(PaintRollerItem.byColor(color));
			PaintRollerItem resultItem = (PaintRollerItem) paintroller2.getItem();
			int left = dyes.size() * 8;
			if (item instanceof PaintRollerItem) {
				resultItem.setDamage(paintroller2, resultItem.getMaxPaint() - left
						- resultItem.getMaxPaint() + resultItem.getDamage(paintroller));
			} else {
				resultItem.setDamage(paintroller2, resultItem.getMaxPaint() - left);

			}
			return paintroller2;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CIRecipeSerializers.PAINTROLLER_FILLING;
	}
}
