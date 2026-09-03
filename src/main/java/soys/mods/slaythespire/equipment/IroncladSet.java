package soys.mods.slaythespire.equipment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import soys.mods.slaythespire.registry.ModItems;

/**
 * 中文：铁甲战士套装检测工具。它只读取装备槽，不修改玩家属性、战斗状态或卡牌结算。
 * English: Ironclad set detection helper. It only reads equipment slots and never mutates attributes, combat state, or card settlement.
 */
public final class IroncladSet {
    private IroncladSet() {
    }

    /**
     * 中文：确认玩家是否穿齐四件铁甲战士套装，用作客户端外观层的唯一触发条件。
     * English: Checks whether an entity wears all four Ironclad pieces, serving as the only trigger for the client appearance layer.
     */
    public static boolean isWearingFullSet(LivingEntity entity) {
        return isIroncladPiece(entity.getItemBySlot(EquipmentSlot.HEAD), ArmorItem.Type.HELMET)
                && isIroncladPiece(entity.getItemBySlot(EquipmentSlot.CHEST), ArmorItem.Type.CHESTPLATE)
                && isIroncladPiece(entity.getItemBySlot(EquipmentSlot.LEGS), ArmorItem.Type.LEGGINGS)
                && isIroncladPiece(entity.getItemBySlot(EquipmentSlot.FEET), ArmorItem.Type.BOOTS);
    }

    /**
     * 中文：按装备类型判断物品是否为指定铁甲战士部件。
     * English: Checks whether a stack is the expected Ironclad piece for the requested armor type.
     */
    public static boolean isIroncladPiece(ItemStack stack, ArmorItem.Type type) {
        if (stack.isEmpty()) {
            return false;
        }
        return switch (type) {
            case HELMET -> stack.is(ModItems.IRONCLAD_HELMET.get());
            case CHESTPLATE -> stack.is(ModItems.IRONCLAD_CHESTPLATE.get());
            case LEGGINGS -> stack.is(ModItems.IRONCLAD_LEGGINGS.get());
            case BOOTS -> stack.is(ModItems.IRONCLAD_BOOTS.get());
        };
    }
}
