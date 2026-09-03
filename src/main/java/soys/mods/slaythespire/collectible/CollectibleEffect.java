package soys.mods.slaythespire.collectible;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.combat.CombatState;

/**
 * 中文：收藏品效果函数式接口。遗物效果通过不同钩子触发，药水效果通过使用触发。
 * English: Functional interface for collectible effects. Relic effects trigger through various hooks, while potion effects trigger on use.
 */
@FunctionalInterface
public interface CollectibleEffect {

    /**
     * 中文：效果触发上下文，包含玩家、可选目标和可选卡牌栈。
     * English: Effect trigger context, containing the player, optional target, and optional card stack.
     */
    record Context(ServerPlayer player, CombatState state, LivingEntity target, ItemStack stack) {
    }

    // 中文：执行效果。
    // English: Applies the effect.
    void apply(Context context);
}
