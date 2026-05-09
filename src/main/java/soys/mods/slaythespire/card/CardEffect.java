package soys.mods.slaythespire.card;

/**
 * 中文：卡牌效果函数。所有效果都在服务端通过 CombatService 调用，传入当前玩家、战斗状态、卡牌和目标。
 * English: Card effect function. All effects are invoked on the server by CombatService with the current player, combat state, card, and target.
 */
@FunctionalInterface
public interface CardEffect {
    void apply(CardUseContext context);
}
