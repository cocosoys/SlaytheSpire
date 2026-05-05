package soys.mods.slaythespire.card;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;

public final class CardDefinition {
    private final ResourceLocation id;
    private final String effectKey;
    private final CardRarity rarity;
    private final CardType type;
    private final CardTarget target;
    private final int cost;
    private final boolean xCost;
    private final boolean playable;
    private final boolean exhausts;
    private final boolean ethereal;
    private final CardPredicate canUse;
    private final CardEffect effect;
    private final String literalDisplayName;
    private final String literalDescription;

    public CardDefinition(
            ResourceLocation id,
            String effectKey,
            CardRarity rarity,
            CardType type,
            CardTarget target,
            int cost,
            boolean xCost,
            boolean playable,
            boolean exhausts,
            boolean ethereal,
            CardPredicate canUse,
            CardEffect effect,
            String literalDisplayName,
            String literalDescription
    ) {
        this.id = id;
        this.effectKey = effectKey;
        this.rarity = rarity;
        this.type = type;
        this.target = target;
        this.cost = cost;
        this.xCost = xCost;
        this.playable = playable;
        this.exhausts = exhausts;
        this.ethereal = ethereal;
        this.canUse = canUse;
        this.effect = effect;
        this.literalDisplayName = literalDisplayName;
        this.literalDescription = literalDescription;
    }

    public CardDefinition(
            ResourceLocation id,
            CardRarity rarity,
            CardType type,
            CardTarget target,
            int cost,
            boolean xCost,
            boolean playable,
            boolean exhausts,
            boolean ethereal,
            CardPredicate canUse,
            CardEffect effect
    ) {
        this(id, id.getPath(), rarity, type, target, cost, xCost, playable, exhausts, ethereal, canUse, effect, null, null);
    }

    public ResourceLocation id() {
        return id;
    }

    public String effectKey() {
        return effectKey;
    }

    public CardRarity rarity() {
        return rarity;
    }

    public CardType type() {
        return type;
    }

    public CardTarget target() {
        return target;
    }

    public int cost() {
        return cost;
    }

    public boolean isXCost() {
        return xCost;
    }

    public boolean isPlayable() {
        return playable;
    }

    public boolean exhausts() {
        return exhausts;
    }

    public boolean ethereal() {
        return ethereal;
    }

    public CardPredicate canUse() {
        return canUse;
    }

    public CardEffect effect() {
        return effect;
    }

    public boolean isAttack() {
        return type == CardType.ATTACK;
    }

    public boolean isSkill() {
        return type == CardType.SKILL;
    }

    public boolean isPower() {
        return type == CardType.POWER;
    }

    public boolean isStatus() {
        return type == CardType.STATUS;
    }

    public Component displayName() {
        if (literalDisplayName != null) {
            return Component.literal(literalDisplayName);
        }
        return Component.translatable("item." + Slaythespire.MODID + "." + id.getPath());
    }

    public Component description() {
        if (literalDescription != null) {
            return Component.literal(literalDescription);
        }
        return Component.translatable("card." + Slaythespire.MODID + "." + id.getPath() + ".desc");
    }
}
