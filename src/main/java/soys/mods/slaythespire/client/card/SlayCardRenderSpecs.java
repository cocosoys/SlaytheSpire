package soys.mods.slaythespire.client.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.card.CardDefinitions;

import java.util.Map;

public final class SlayCardRenderSpecs {
    private static final ResourceLocation ORB = texture("slay/512/card_purple_orb.png");
    private static final Map<ResourceLocation, SlayCardRenderSpec> SPECS = Map.of(
            CardDefinitions.STRIKE.id(), new SlayCardRenderSpec(
                    CardDefinitions.STRIKE.id(),
                    texture("slay/512/bg_attack_purple.png"),
                    texture("slay/cards/strike_purple.png"),
                    ORB
            ),
            CardDefinitions.DEFEND.id(), new SlayCardRenderSpec(
                    CardDefinitions.DEFEND.id(),
                    texture("slay/512/bg_skill_purple.png"),
                    texture("slay/cards/defend_purple.png"),
                    ORB
            )
    );

    private SlayCardRenderSpecs() {
    }

    public static SlayCardRenderSpec require(ResourceLocation id) {
        SlayCardRenderSpec spec = SPECS.get(id);
        if (spec == null) {
            throw new IllegalArgumentException("Missing card render spec: " + id);
        }
        return spec;
    }

    private static ResourceLocation texture(String path) {
        return ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "textures/item/" + path);
    }
}
