package com.github.reoseah.catwalksinc.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * An item that provides custom durability value.
 * <p>
 * It's main use is to prevent Unbreaking/Mending enchantments where it doesn't
 * make sense (e.g. a battery).
 * <p>
 * Taken from Cloth API under Unlicense license by Shedaniel
 */
public interface CustomDurabilityItem {
    @Environment(EnvType.CLIENT)
    double getDurabilityBarProgress(ItemStack stack);

    @Environment(EnvType.CLIENT)
    boolean hasDurabilityBar(ItemStack stack);

    @Environment(EnvType.CLIENT)
    default int getDurabilityBarColor(ItemStack stack) {
        return MathHelper.hsvToRgb(Math.max(0.0F, 1 - (float) getDurabilityBarProgress(stack)) / 3.0F, 1.0F, 1.0F);
    }
}
