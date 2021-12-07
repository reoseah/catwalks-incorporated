package com.github.reoseah.catwalksinc.items;

import java.util.Random;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;

public abstract class SimpleCustomDurabilityItem extends Item implements CustomDurabilityItem {
	public SimpleCustomDurabilityItem(Item.Settings settings) {
		super(settings);
	}

	public abstract int getCustomMaxDamage();

	public boolean isDamageable(ItemStack stack) {
		if (!stack.isEmpty() && this.getCustomMaxDamage() > 0) {
			NbtCompound NbtCompound = stack.getNbt();
			return NbtCompound == null || !NbtCompound.getBoolean("Unbreakable");
		}
		return false;
	}

	public boolean isDamaged(ItemStack stack) {
		return this.isDamageable(stack) && this.getDamage(stack) > 0;
	}

	public int getDamage(ItemStack stack) {
		return stack.getNbt() == null ? 0 : stack.getNbt().getInt("CustomDamage");
	}

	public void setDamage(ItemStack stack, int damage) {
		stack.getOrCreateNbt().putInt("CustomDamage", Math.max(0, damage));
	}

	public boolean damage(ItemStack stack, int amount, Random random, @Nullable ServerPlayerEntity player) {
		if (!this.isDamageable(stack)) {
			return false;
		}
		int i;

		if (player != null && amount != 0) {
			Criteria.ITEM_DURABILITY_CHANGED.trigger(player, stack, this.getDamage(stack) + amount);
		}

		i = this.getDamage(stack) + amount;
		this.setDamage(stack, i);
		return i >= this.getCustomMaxDamage();
	}

	public <T extends LivingEntity> void damage(ItemStack stack, int amount, T entity, Consumer<T> breakCallback) {
		if (!entity.world.isClient
				&& (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).getAbilities().creativeMode)) {
			if (this.isDamageable(stack)) {
				if (this.damage(stack, amount, entity.getRandom(),
						entity instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity : null)) {
					breakCallback.accept(entity);
					Item item = stack.getItem();
					stack.decrement(1);
					if (entity instanceof PlayerEntity) {
						((PlayerEntity) entity).incrementStat(Stats.BROKEN.getOrCreateStat(item));
					}

					this.setDamage(stack, 0);
				}
			}
		}
	}

	@Override
	public double getDurabilityBarProgress(ItemStack stack) {
		return (double) this.getDamage(stack) / (double) this.getCustomMaxDamage();
	}

	@Override
	public boolean hasDurabilityBar(ItemStack stack) {
		return this.isDamaged(stack);
	}
}
