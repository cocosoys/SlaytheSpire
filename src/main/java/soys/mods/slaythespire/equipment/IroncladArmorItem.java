package soys.mods.slaythespire.equipment;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 中文：铁甲战士四件套物品。继承 ArmorItem 是为了复用原版装备槽，但显式移除所有属性修饰与附魔收益。
 * English: Ironclad set item. It extends ArmorItem for vanilla equipment slots while explicitly removing attribute and enchantment benefits.
 */
public class IroncladArmorItem extends ArmorItem {
    private static final Multimap<Attribute, AttributeModifier> NO_ATTRIBUTES = ImmutableMultimap.of();

    public IroncladArmorItem(Type type, Properties properties) {
        super(IroncladArmorMaterial.INSTANCE, type, properties.durability(IroncladArmorMaterial.INSTANCE.getDurabilityForType(type)));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return NO_ATTRIBUTES;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.slaythespire.ironclad_set").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.slaythespire.ironclad_non_combat").withStyle(ChatFormatting.DARK_GRAY));
    }
}
