package soys.mods.slaythespire.client.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardRarity;
import soys.mods.slaythespire.card.CardDefinitions;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SlayCardRenderSpecs {
    private static final CardUiAtlasRegion RED_ORB = CardUiAtlasRegion.REGION_1024_CARD_RED_ORB;

    private static final Template RED_ATTACK = new Template(
            CardUiAtlasRegion.REGION_1024_BG_ATTACK_RED,
            CardUiAtlasRegion.REGION_1024_FRAME_ATTACK_COMMON,
            CardUiAtlasRegion.REGION_1024_BANNER_COMMON,
            CardUiAtlasRegion.REGION_1024_COMMON_LEFT,
            CardUiAtlasRegion.REGION_1024_COMMON_CENTER,
            CardUiAtlasRegion.REGION_1024_COMMON_RIGHT,
            SlayCardRenderSpec.Layout.attack()
    );
    private static final Template RED_SKILL = new Template(
            CardUiAtlasRegion.REGION_1024_BG_SKILL_RED,
            CardUiAtlasRegion.REGION_1024_FRAME_SKILL_COMMON,
            CardUiAtlasRegion.REGION_1024_BANNER_COMMON,
            CardUiAtlasRegion.REGION_1024_COMMON_LEFT,
            CardUiAtlasRegion.REGION_1024_COMMON_CENTER,
            CardUiAtlasRegion.REGION_1024_COMMON_RIGHT,
            SlayCardRenderSpec.Layout.skill()
    );
    private static final Template RED_POWER_COMMON = new Template(
            CardUiAtlasRegion.REGION_1024_BG_POWER_RED,
            CardUiAtlasRegion.REGION_1024_FRAME_POWER_COMMON,
            CardUiAtlasRegion.REGION_1024_BANNER_COMMON,
            CardUiAtlasRegion.REGION_1024_COMMON_LEFT,
            CardUiAtlasRegion.REGION_1024_COMMON_CENTER,
            CardUiAtlasRegion.REGION_1024_COMMON_RIGHT,
            SlayCardRenderSpec.Layout.power()
    );
    private static final Template RED_POWER_UNCOMMON = new Template(
            CardUiAtlasRegion.REGION_1024_BG_POWER_RED,
            CardUiAtlasRegion.REGION_1024_FRAME_POWER_UNCOMMON,
            CardUiAtlasRegion.REGION_1024_BANNER_UNCOMMON,
            CardUiAtlasRegion.REGION_1024_UNCOMMON_LEFT,
            CardUiAtlasRegion.REGION_1024_UNCOMMON_CENTER,
            CardUiAtlasRegion.REGION_1024_UNCOMMON_RIGHT,
            SlayCardRenderSpec.Layout.power()
    );
    private static final Template RED_POWER_RARE = new Template(
            CardUiAtlasRegion.REGION_1024_BG_POWER_RED,
            CardUiAtlasRegion.REGION_1024_FRAME_POWER_RARE,
            CardUiAtlasRegion.REGION_1024_BANNER_RARE,
            CardUiAtlasRegion.REGION_1024_RARE_LEFT,
            CardUiAtlasRegion.REGION_1024_RARE_CENTER,
            CardUiAtlasRegion.REGION_1024_RARE_RIGHT,
            SlayCardRenderSpec.Layout.power()
    );

    private static final Map<ResourceLocation, SlayCardRenderSpec> SPECS = createSpecs();

    private SlayCardRenderSpecs() {
    }

    public static SlayCardRenderSpec require(ResourceLocation id) {
        SlayCardRenderSpec spec = SPECS.get(id);
        if (spec == null) {
            throw new IllegalArgumentException("Missing card render spec: " + id);
        }
        return spec;
    }

    private static SlayCardRenderSpec spec(CardDefinition definition, SlayCardRenderSpec.CardArt art) {
        return template(definition).create(definition.id(), art);
    }

    private static Map<ResourceLocation, SlayCardRenderSpec> createSpecs() {
        Map<ResourceLocation, SlayCardRenderSpec> specs = new LinkedHashMap<>();
        for (CardDefinition definition : CardDefinitions.all()) {
            specs.put(definition.id(), spec(definition, art(definition)));
        }
        return Map.copyOf(specs);
    }

    private static SlayCardRenderSpec.CardArt art(CardDefinition definition) {
        String folder = switch (definition.type()) {
            case ATTACK -> "attack";
            case SKILL -> "skill";
            case POWER -> "power";
            case STATUS -> "status";
        };
        return SlayCardRenderSpec.CardArt.cardArt("red/" + folder + "/" + artName(definition) + ".png");
    }

    private static String artName(CardDefinition definition) {
        String path = definition.id().getPath();
        return path.endsWith("_card") ? path.substring(0, path.length() - "_card".length()) : path;
    }

    private static Template template(CardDefinition definition) {
        return switch (definition.type()) {
            case ATTACK -> RED_ATTACK;
            case SKILL -> RED_SKILL;
            case POWER -> switch (definition.rarity()) {
                case RARE -> RED_POWER_RARE;
                case UNCOMMON -> RED_POWER_UNCOMMON;
                default -> RED_POWER_COMMON;
            };
            case STATUS -> RED_SKILL;
        };
    }

    private record Template(
            CardUiAtlasRegion cardBackground,
            CardUiAtlasRegion artFrame,
            CardUiAtlasRegion titleBanner,
            CardUiAtlasRegion titleDecorationLeft,
            CardUiAtlasRegion titleDecorationCenter,
            CardUiAtlasRegion titleDecorationRight,
            SlayCardRenderSpec.Layout layout
    ) {
        private SlayCardRenderSpec create(ResourceLocation cardId, SlayCardRenderSpec.CardArt art) {
            return new SlayCardRenderSpec(
                    cardId,
                    cardBackground,
                    art,
                    artFrame,
                    titleBanner,
                    titleDecorationLeft,
                    titleDecorationCenter,
                    titleDecorationRight,
                    RED_ORB,
                    layout
            );
        }
    }
}
