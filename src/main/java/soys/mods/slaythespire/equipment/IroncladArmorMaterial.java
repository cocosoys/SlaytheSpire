package soys.mods.slaythespire.equipment;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import soys.mods.slaythespire.Slaythespire;

/**
 * 中文：铁甲战士外观套装的零数值护甲材质。它只占用装备槽触发客户端外观，不提供护甲或战斗收益。
 * English: Zero-stat armor material for the Ironclad appearance set. It only occupies armor slots to trigger client appearance.
 */
public enum IroncladArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return 203;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return 0;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return Slaythespire.MODID + ":ironclad";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
