package soys.mods.slaythespire.card;

@FunctionalInterface
public interface CardPredicate {
    boolean test(CardUseContext context);
}
