package com.github.reoseah.catwalksinc.items;

import com.github.reoseah.catwalksinc.blocks.Wrenchable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WrenchItem extends ToolItem {
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	public WrenchItem(Item.Settings settings) {
		super(ToolMaterials.IRON, settings);

		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> attributeBuilder = ImmutableMultimap
				.builder();
		attributeBuilder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
				ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 2, EntityAttributeModifier.Operation.ADDITION));
		attributeBuilder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(
				ATTACK_SPEED_MODIFIER_ID, "Tool modifier", 0, EntityAttributeModifier.Operation.ADDITION));
		this.attributeModifiers = attributeBuilder.build();
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
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		if (slot == EquipmentSlot.MAINHAND) {
			return this.attributeModifiers;
		}
		return super.getAttributeModifiers(slot);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(2, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}
}
