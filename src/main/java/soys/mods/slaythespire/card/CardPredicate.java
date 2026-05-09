package soys.mods.slaythespire.card;

/**
 * 中文：卡牌可用性判断函数。它在扣费和执行效果前运行，用于表达“只在某种状态下可打出”的限制。
 * English: Card usability predicate. It runs before cost payment and effect execution to express restrictions such as "playable only in a specific state".
 */
@FunctionalInterface
public interface CardPredicate {
    boolean test(CardUseContext context);
}
