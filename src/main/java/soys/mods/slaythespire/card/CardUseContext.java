package soys.mods.slaythespire.card;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import soys.mods.slaythespire.combat.CombatState;

/**
 * 中文：一次卡牌使用的上下文快照。effect 和 predicate 都通过它读取玩家、状态、卡牌、物品栈和可能为空的目标。
 * English: Context snapshot for one card use. Effects and predicates read player, state, card, item stack, and nullable target through it.
 */
public record CardUseContext(ServerPlayer player, CombatState state, CardDefinition definition, ItemStack stack, @Nullable LivingEntity target) {
}
