package soys.mods.slaythespire.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.combat.CombatService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 中文：当前迁移的红色牌定义表。开发者新增卡牌时优先在这里用 attack/skill/power 工厂注册，保持 Java API 统一入口。
 * English: Definition table for the migrated red cards. Developers should add cards through the attack/skill/power factories here to keep one Java API entrypoint.
 */
public final class CardDefinitions {
    // 中文：LinkedHashMap 保留注册顺序，创造模式物品栏、渲染规格生成和调试输出都会沿用这个顺序。
    // English: LinkedHashMap preserves registration order for creative-tab listing, render spec generation, and debugging.
    private static final Map<ResourceLocation, CardDefinition> DEFINITIONS = new LinkedHashMap<>();
    // 中文：基础牌默认永远可用；复杂卡牌可传入 CardPredicate 实现额外限制。
    // English: Basic cards are always usable by default; complex cards can provide CardPredicate for additional restrictions.
    private static final CardPredicate ALWAYS = context -> true;

    public static final CardDefinition STRIKE = attack("strike_card", CardRarity.BASIC, 1,
            context -> CombatService.dealAttackDamage(context.player(), context.target(), 6.0F));
    public static final CardDefinition DEFEND = skill("defend_card", CardRarity.BASIC, 1,
            context -> CombatService.gainBlock(context.player(), 5));
    public static final CardDefinition BARRICADE = power("barricade_card", CardRarity.RARE, 3,
            context -> context.state().setBarricade(true));
    public static final CardDefinition BERSERK = power("berserk_card", CardRarity.RARE, 0,
            context -> {
                CombatService.applyVulnerable(context.player(), 2);
                context.state().addBerserkEnergy(1);
            });
    public static final CardDefinition BRUTALITY = power("brutality_card", CardRarity.RARE, 0,
            context -> context.state().setBrutality(true));
    public static final CardDefinition COMBUST = power("combust_card", CardRarity.UNCOMMON, 1,
            context -> context.state().addCombustDamage(5));
    public static final CardDefinition CORRUPTION = power("corruption_card", CardRarity.RARE, 3,
            context -> context.state().setCorruption(true));
    public static final CardDefinition DARK_EMBRACE = power("dark_embrace_card", CardRarity.UNCOMMON, 2,
            context -> context.state().addDarkEmbraceDraw(1));
    public static final CardDefinition DEMON_FORM = power("demon_form_card", CardRarity.RARE, 3,
            context -> context.state().addDemonFormStrength(2));
    public static final CardDefinition EVOLVE = power("evolve_card", CardRarity.UNCOMMON, 1,
            context -> context.state().addEvolveDraw(1));
    public static final CardDefinition FEEL_NO_PAIN = power("feel_no_pain_card", CardRarity.UNCOMMON, 1,
            context -> context.state().addFeelNoPainBlock(3));
    public static final CardDefinition FIRE_BREATHING = power("fire_breathing_card", CardRarity.UNCOMMON, 1,
            context -> context.state().addFireBreathingDamage(6));
    public static final CardDefinition INFLAME = power("inflame_card", CardRarity.UNCOMMON, 1,
            context -> context.state().addStrength(2));
    public static final CardDefinition JUGGERNAUT = power("juggernaut_card", CardRarity.RARE, 2,
            context -> context.state().addJuggernautDamage(5));
    public static final CardDefinition METALLICIZE = power("metallicize_card", CardRarity.UNCOMMON, 1,
            context -> context.state().addMetallicize(3));
    public static final CardDefinition RUPTURE = power("rupture_card", CardRarity.UNCOMMON, 1,
            context -> context.state().addRuptureStrength(1));

    // ==================== 新增攻击牌 ====================
    public static final CardDefinition CLEAVE = attack("cleave_card", CardRarity.COMMON, 1,
            context -> CombatService.dealAllEnemies(context.player(), 8.0F));
    public static final CardDefinition HEAVY_BLADE = attack("heavy_blade_card", CardRarity.UNCOMMON, 2,
            context -> CombatService.dealAttackDamage(context.player(), context.target(), 14.0F, 3, true));
    public static final CardDefinition TWIN_STRIKE = attack("twin_strike_card", CardRarity.COMMON, 1,
            context -> {
                CombatService.dealAttackDamage(context.player(), context.target(), 5.0F);
                CombatService.dealAttackDamage(context.player(), context.target(), 5.0F);
            });
    public static final CardDefinition BLUDGEON = attack("bludgeon_card", CardRarity.RARE, 3,
            context -> CombatService.dealAttackDamage(context.player(), context.target(), 32.0F));
    public static final CardDefinition POMMEL_STRIKE = attack("pommel_strike_card", CardRarity.COMMON, 1,
            context -> {
                CombatService.dealAttackDamage(context.player(), context.target(), 9.0F);
                CombatService.drawTemporaryCards(context.player(), 1);
            });
    public static final CardDefinition ANGER = attack("anger_card", CardRarity.COMMON, 0,
            context -> {
                CombatService.dealAttackDamage(context.player(), context.target(), 6.0F);
                CombatService.createTemporaryGeneratedCopy(context.player(), context.definition().id(), null);
            });
    public static final CardDefinition WILD_STRIKE = attack("wild_strike_card", CardRarity.COMMON, 1,
            context -> {
                CombatService.dealAttackDamage(context.player(), context.target(), 12.0F);
                CombatService.addStatusCard(context.player(), id("wound_card"));
            });
    public static final CardDefinition CARNAGE = card("carnage_card", CardRarity.UNCOMMON, CardType.ATTACK, CardTarget.ENEMY, 2, true,
            context -> CombatService.dealAttackDamage(context.player(), context.target(), 20.0F));
    public static final CardDefinition UPPERCUT = attack("uppercut_card", CardRarity.UNCOMMON, 2,
            context -> {
                CombatService.dealAttackDamage(context.player(), context.target(), 13.0F);
                if (context.target() != null) {
                    CombatService.applyWeakFromPlayer(context.player(), context.target(), 1);
                    CombatService.applyVulnerableFromPlayer(context.player(), context.target(), 1);
                }
            });
    public static final CardDefinition THUNDERCLAP = attack("thunderclap_card", CardRarity.COMMON, 1,
            context -> {
                CombatService.dealAllEnemies(context.player(), 4.0F);
                CombatService.applyVulnerableToAllEnemies(context.player(), 1);
            });
    public static final CardDefinition RAMPAGE = attack("rampage_card", CardRarity.UNCOMMON, 1,
            context -> {
                int bonus = context.state().getRampageBonus(context.definition().effectKey());
                CombatService.dealAttackDamage(context.player(), context.target(), 8.0F + bonus);
                context.state().addRampageBonus(context.definition().effectKey(), 5);
            });

    // ==================== 新增技能牌 ====================
    public static final CardDefinition SHRUG_IT_OFF = skill("shrug_it_off_card", CardRarity.COMMON, 1,
            context -> {
                CombatService.gainBlock(context.player(), 8);
                CombatService.drawTemporaryCards(context.player(), 1);
            });
    public static final CardDefinition TRUE_GRIT = skill("true_grit_card", CardRarity.COMMON, 1,
            context -> {
                CombatService.gainBlock(context.player(), 7);
                CombatService.exhaustRandomCards(context.player(), card -> !card.isStatus(), 1, context.stack());
            });
    public static final CardDefinition BATTLE_TRANCE = skill("battle_trance_card", CardRarity.UNCOMMON, 0,
            context -> {
                CombatService.drawTemporaryCards(context.player(), 3);
                context.state().setDrawLocked(true);
            });
    public static final CardDefinition BLOODLETTING = skill("bloodletting_card", CardRarity.UNCOMMON, 0,
            context -> {
                CombatService.loseHpFromCard(context.player(), 3);
                CombatService.gainEnergy(context.player(), 2);
            });
    public static final CardDefinition BURNING_PACT = skill("burning_pact_card", CardRarity.UNCOMMON, 1,
            context -> {
                CombatService.exhaustRandomCards(context.player(), card -> !card.isStatus(), 2, context.stack());
                CombatService.drawTemporaryCards(context.player(), 2);
            });
    public static final CardDefinition DISARM = skill("disarm_card", CardRarity.RARE, 1,
            context -> {
                if (context.target() != null) {
                    CombatService.modifyTargetStrengthFromPlayer(context.player(), context.target(), -2);
                }
            });
    public static final CardDefinition INTIMIDATE = skill("intimidate_card", CardRarity.UNCOMMON, 0,
            context -> CombatService.applyWeakToAllEnemies(context.player(), 1));
    public static final CardDefinition POWER_THROUGH = skill("power_through_card", CardRarity.UNCOMMON, 1,
            context -> {
                CombatService.gainBlock(context.player(), 10);
                CombatService.addStatusCard(context.player(), id("wound_card"));
                CombatService.addStatusCard(context.player(), id("wound_card"));
            });
    public static final CardDefinition RAGE_SKILL = skill("rage_card", CardRarity.UNCOMMON, 0,
            context -> context.state().setRageBlockPerAttack(3));
    public static final CardDefinition RESTLESS = card("restless_card", CardRarity.COMMON, CardType.SKILL, CardTarget.SELF, 0, true,
            context -> CombatService.gainBlock(context.player(), 4));

    // ==================== 状态牌 ====================
    // 中文：灼伤状态牌，无法打出，占用手牌位。
    // English: Wound status card, unplayable, occupies a hand slot.
    public static final CardDefinition WOUND = card("wound_card", CardRarity.STATUS, CardType.STATUS, CardTarget.NONE, -1, false,
            context -> { });

    // 中文：禁止实例化静态卡牌定义表。
    // English: Prevents instantiation of the static card definition table.
    private CardDefinitions() {
    }

    // 中文：按资源 id 查询卡牌定义，找不到时返回 null。
    // English: Looks up a card definition by resource id, returning null when absent.
    public static CardDefinition get(ResourceLocation id) {
        return DEFINITIONS.get(id);
    }

    // 中文：按资源 id 获取卡牌定义，找不到时抛出错误以暴露注册问题。
    // English: Gets a card definition by resource id, throwing when missing to expose registration issues.
    public static CardDefinition require(ResourceLocation id) {
        CardDefinition definition = get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown card definition: " + id);
        }
        return definition;
    }

    // 中文：按内置效果键获取卡牌定义，兼容带或不带 _card 后缀的调用。
    // English: Gets a card definition by built-in effect key, accepting calls with or without the _card suffix.
    public static CardDefinition requireBuiltinEffect(String builtinId) {
        // 中文：旧代码或调试入口可能传入 strike 而不是 strike_card，这里统一归一化再查找。
        // English: Older code or debug hooks may pass strike instead of strike_card, so normalize before lookup.
        String normalized = normalizeBuiltinId(builtinId);
        for (CardDefinition definition : DEFINITIONS.values()) {
            if (definition.effectKey().equals(normalized)) {
                return definition;
            }
        }
        throw new IllegalArgumentException("Unknown builtin card effect: " + builtinId);
    }

    // 中文：返回全部已注册卡牌定义的不可变快照。
    // English: Returns an immutable snapshot of all registered card definitions.
    public static List<CardDefinition> all() {
        return List.copyOf(DEFINITIONS.values());
    }

    // 中文：返回当前可在创造栏和玩法中使用的红色牌集合。
    // English: Returns the red-card set currently exposed to creative tabs and gameplay.
    public static List<CardDefinition> playableRedCards() {
        // 中文：当前阶段只保留红色牌迁移结果，因此可玩红牌集合等同于全部已注册卡。
        // English: This phase only keeps migrated red cards, so playable red cards are the full registered set.
        return all();
    }

    // 中文：注册一张以敌人为目标的攻击牌。
    // English: Registers an attack card targeting an enemy.
    private static CardDefinition attack(String path, CardRarity rarity, int cost, CardEffect effect) {
        return card(path, rarity, CardType.ATTACK, CardTarget.ENEMY, cost, false, effect);
    }

    // 中文：注册一张以自身为目标的技能牌。
    // English: Registers a skill card targeting the player.
    private static CardDefinition skill(String path, CardRarity rarity, int cost, CardEffect effect) {
        return card(path, rarity, CardType.SKILL, CardTarget.SELF, cost, false, effect);
    }

    // 中文：注册一张以自身为目标的能力牌。
    // English: Registers a power card targeting the player.
    private static CardDefinition power(String path, CardRarity rarity, int cost, CardEffect effect) {
        return card(path, rarity, CardType.POWER, CardTarget.SELF, cost, false, effect);
    }

    // 中文：集中构建卡牌定义并写入注册表。
    // English: Centralizes card-definition construction and inserts it into the registry.
    private static CardDefinition card(String path, CardRarity rarity, CardType type, CardTarget target, int cost, boolean exhausts, CardEffect effect) {
        // 中文：这里集中填充通用默认值：非 X 费、可打出、非虚无、默认 ALWAYS 可用。
        // English: Shared defaults are centralized here: non-X cost, playable, non-ethereal, and ALWAYS usable.
        return register(new CardDefinition(id(path), rarity, type, target, cost, false, true, exhausts, false, ALWAYS, effect));
    }

    // 中文：把卡牌定义写入有序注册表并返回原定义，便于静态字段链式初始化。
    // English: Stores the definition in the ordered registry and returns it for static field initialization.
    private static CardDefinition register(CardDefinition definition) {
        // 中文：注册时不做覆盖检查；如果未来允许扩展包覆盖，应在这里加入明确策略。
        // English: Registration does not check overwrites; if extensions later allow replacement, that policy should be added here.
        DEFINITIONS.put(definition.id(), definition);
        return definition;
    }

    // 中文：为本模组卡牌路径创建资源 id。
    // English: Creates a resource id for a card path in this mod namespace.
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, path);
    }

    // 中文：归一化旧调用传入的内置效果 id。
    // English: Normalizes built-in effect ids supplied by older call sites.
    private static String normalizeBuiltinId(String builtinId) {
        if (builtinId.endsWith("_card")) {
            return builtinId;
        }
        return builtinId + "_card";
    }
}
