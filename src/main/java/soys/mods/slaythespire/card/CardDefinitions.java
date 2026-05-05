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

    public static final CardDefinition DEFEND = skill("defend_card", 1,
            context -> CombatService.gainBlock(context.player(), 5));
    public static final CardDefinition STRIKE = attack("strike_card", 1,
            context -> CombatService.dealAttackDamage(context.player(), context.target(), 6.0F));

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

    private static CardDefinition attack(String path, int cost, CardEffect effect) {
        return register(new CardDefinition(id(path), CardRarity.BASIC, CardType.ATTACK, CardTarget.ENEMY, cost, false, true, false, false, ALWAYS, effect));
    }

    private static CardDefinition skill(String path, int cost, CardEffect effect) {
        return register(new CardDefinition(id(path), CardRarity.BASIC, CardType.SKILL, CardTarget.SELF, cost, false, true, false, false, ALWAYS, effect));
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
