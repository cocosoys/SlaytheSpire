package soys.mods.slaythespire.card;

/**
 * 中文：卡牌目标类型。CardItem 根据它决定空放、点实体或无目标卡牌如何进入 CombatService。
 * English: Card target type. CardItem uses it to decide whether a card is used in air, on an entity, or without a target before entering CombatService.
 */
public enum CardTarget {
    SELF,
    ENEMY,
    ALL_ENEMIES,
    NONE
}
