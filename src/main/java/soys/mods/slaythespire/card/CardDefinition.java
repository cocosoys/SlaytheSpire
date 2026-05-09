package soys.mods.slaythespire.card;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;

/**
 * 中文：Java API 卡牌定义。每张卡牌的费用、类型、目标、可用条件和效果都集中在这里描述，渲染、物品和战斗系统只读取这个不可变定义。
 * English: Java API card definition. Cost, type, target, usage predicate, and effect are described here, while rendering, item behavior, and combat systems only read this immutable definition.
 */
public final class CardDefinition {
    // 中文：id 同时用于物品注册、语言键、渲染规格和卡图命名约定，是卡牌的主键。
    // English: id is the primary key for item registration, language keys, render specs, and portrait naming conventions.
    private final ResourceLocation id;
    // 中文：effectKey 允许效果逻辑和物品 id 解耦，兼容去掉 _card 后缀或复用内置效果的场景。
    // English: effectKey decouples effect logic from item id, supporting suffix normalization or reuse of built-in effects.
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

    // 中文：构造完整卡牌定义，允许传入自定义效果键和字面文本覆盖。
    // English: Constructs a full card definition, allowing custom effect keys and literal text overrides.
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

    // 中文：构造常规 Java API 卡牌定义，默认使用 id 路径作为效果键。
    // English: Constructs a regular Java API card definition, using the id path as the default effect key.
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

    // 中文：返回卡牌唯一资源 id。
    // English: Returns the unique resource id of the card.
    public ResourceLocation id() {
        return id;
    }

    // 中文：返回用于匹配内置效果逻辑的效果键。
    // English: Returns the effect key used to match built-in effect logic.
    public String effectKey() {
        return effectKey;
    }

    // 中文：返回卡牌稀有度。
    // English: Returns the card rarity.
    public CardRarity rarity() {
        return rarity;
    }

    // 中文：返回卡牌类型。
    // English: Returns the card type.
    public CardType type() {
        return type;
    }

    // 中文：返回卡牌目标规则。
    // English: Returns the card targeting rule.
    public CardTarget target() {
        return target;
    }

    // 中文：返回卡牌基础费用。
    // English: Returns the base card cost.
    public int cost() {
        return cost;
    }

    // 中文：返回卡牌是否为 X 费。
    // English: Returns whether the card uses X cost.
    public boolean isXCost() {
        return xCost;
    }

    // 中文：返回卡牌是否可主动打出。
    // English: Returns whether the card can be actively played.
    public boolean isPlayable() {
        return playable;
    }

    // 中文：返回卡牌打出后是否消耗。
    // English: Returns whether the card exhausts after being played.
    public boolean exhausts() {
        return exhausts;
    }

    // 中文：返回卡牌是否为虚无牌。
    // English: Returns whether the card is ethereal.
    public boolean ethereal() {
        return ethereal;
    }

    // 中文：返回卡牌额外可用性判定。
    // English: Returns the card's additional usability predicate.
    public CardPredicate canUse() {
        return canUse;
    }

    // 中文：返回卡牌打出时执行的效果。
    // English: Returns the effect executed when the card is played.
    public CardEffect effect() {
        return effect;
    }

    // 中文：判断卡牌是否为攻击牌。
    // English: Checks whether the card is an attack card.
    public boolean isAttack() {
        return type == CardType.ATTACK;
    }

    // 中文：判断卡牌是否为技能牌。
    // English: Checks whether the card is a skill card.
    public boolean isSkill() {
        return type == CardType.SKILL;
    }

    // 中文：判断卡牌是否为能力牌。
    // English: Checks whether the card is a power card.
    public boolean isPower() {
        return type == CardType.POWER;
    }

    // 中文：判断卡牌是否为状态牌。
    // English: Checks whether the card is a status card.
    public boolean isStatus() {
        return type == CardType.STATUS;
    }

    // 中文：返回用于物品名和牌面标题的本地化显示名。
    // English: Returns the localized display name used by the item name and card title.
    public Component displayName() {
        if (literalDisplayName != null) {
            return Component.literal(literalDisplayName);
        }
        // 中文：默认名称使用 item 命名空间，和 Forge 物品本地化保持一致。
        // English: The default name uses the item namespace so it stays aligned with Forge item localization.
        return Component.translatable("item." + Slaythespire.MODID + "." + id.getPath());
    }

    // 中文：返回用于 tooltip 和牌面描述的本地化文本。
    // English: Returns the localized text used by tooltips and card descriptions.
    public Component description() {
        if (literalDescription != null) {
            return Component.literal(literalDescription);
        }
        // 中文：描述单独使用 card 命名空间，避免和物品显示名混在一起，也方便牌面渲染直接读取。
        // English: Descriptions use a separate card namespace, keeping them apart from item names and easy for the card renderer to read.
        return Component.translatable("card." + Slaythespire.MODID + "." + id.getPath() + ".desc");
    }
}
