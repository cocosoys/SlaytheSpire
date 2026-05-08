package soys.mods.slaythespire.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.combat.CombatService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CardDefinitions {
    private static final Map<ResourceLocation, CardDefinition> DEFINITIONS = new LinkedHashMap<>();
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

    private CardDefinitions() {
    }

    public static CardDefinition get(ResourceLocation id) {
        return DEFINITIONS.get(id);
    }

    public static CardDefinition require(ResourceLocation id) {
        CardDefinition definition = get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown card definition: " + id);
        }
        return definition;
    }

    public static CardDefinition requireBuiltinEffect(String builtinId) {
        String normalized = normalizeBuiltinId(builtinId);
        for (CardDefinition definition : DEFINITIONS.values()) {
            if (definition.effectKey().equals(normalized)) {
                return definition;
            }
        }
        throw new IllegalArgumentException("Unknown builtin card effect: " + builtinId);
    }

    public static List<CardDefinition> all() {
        return List.copyOf(DEFINITIONS.values());
    }

    public static List<CardDefinition> playableRedCards() {
        return all();
    }

    private static CardDefinition attack(String path, CardRarity rarity, int cost, CardEffect effect) {
        return card(path, rarity, CardType.ATTACK, CardTarget.ENEMY, cost, false, effect);
    }

    private static CardDefinition skill(String path, CardRarity rarity, int cost, CardEffect effect) {
        return card(path, rarity, CardType.SKILL, CardTarget.SELF, cost, false, effect);
    }

    private static CardDefinition power(String path, CardRarity rarity, int cost, CardEffect effect) {
        return card(path, rarity, CardType.POWER, CardTarget.SELF, cost, false, effect);
    }

    private static CardDefinition card(String path, CardRarity rarity, CardType type, CardTarget target, int cost, boolean exhausts, CardEffect effect) {
        return register(new CardDefinition(id(path), rarity, type, target, cost, false, true, exhausts, false, ALWAYS, effect));
    }

    private static CardDefinition register(CardDefinition definition) {
        DEFINITIONS.put(definition.id(), definition);
        return definition;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, path);
    }

    private static String normalizeBuiltinId(String builtinId) {
        if (builtinId.endsWith("_card")) {
            return builtinId;
        }
        return builtinId + "_card";
    }
}
