package soys.mods.slaythespire.card;

@FunctionalInterface
public interface CardEffect {
    void apply(CardUseContext context);
}
