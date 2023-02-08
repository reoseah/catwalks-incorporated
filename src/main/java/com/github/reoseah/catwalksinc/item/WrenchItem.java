package com.github.reoseah.catwalksinc.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class WrenchItem extends ToolItem {
    public static final Item INSTANCE = new WrenchItem(new FabricItemSettings().maxDamage(255));
    public static final TagKey<Item> COMPATIBILITY_TAG = TagKey.of(RegistryKeys.ITEM, new Identifier("c:wrenches"));

    public static final SoundEvent USE_SOUND = SoundEvent.of(new Identifier("catwalksinc:wrench_use"));

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public WrenchItem(Item.Settings settings) {
        super(ToolMaterials.IRON, settings);

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 2, EntityAttributeModifier.Operation.ADDITION));
        attributeBuilder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", 0, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = attributeBuilder.build();
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
