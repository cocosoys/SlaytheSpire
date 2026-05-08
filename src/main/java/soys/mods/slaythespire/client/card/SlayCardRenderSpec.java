package soys.mods.slaythespire.client.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;

public record SlayCardRenderSpec(
        ResourceLocation cardId,
        CardUiAtlasRegion cardBackground,
        CardArt art,
        CardUiAtlasRegion artFrame,
        CardUiAtlasRegion titleBanner,
        CardUiAtlasRegion titleDecorationLeft,
        CardUiAtlasRegion titleDecorationCenter,
        CardUiAtlasRegion titleDecorationRight,
        CardUiAtlasRegion costOrb,
        Layout layout
) {
    public record CardArt(ResourceLocation texture) {
        public static CardArt cardArt(String path) {
            return new CardArt(
                    ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "textures/cards/1024portraits/" + path)
            );
        }
    }

    public record Layout(
            Bounds title,
            Bounds art,
            Bounds type,
            Bounds body,
            Bounds cost,
            int titleDecorationOffsetY
    ) {
        public static Layout attack() {
            return new Layout(
                    new Bounds(236, 124, 556, 96),
                    new Bounds(260, 202, 512, 360),
                    new Bounds(414, 526, 208, 56),
                    new Bounds(148, 636, 728, 248),
                    new Bounds(174, 89, 112, 112),
                    -4
            );
        }

        public static Layout skill() {
            return new Layout(
                    new Bounds(236, 124, 556, 96),
                    new Bounds(260, 202, 512, 360),
                    new Bounds(414, 526, 208, 56),
                    new Bounds(148, 636, 728, 248),
                    new Bounds(174, 89, 112, 112),
                    -4
            );
        }

        public static Layout power() {
            return new Layout(
                    new Bounds(236, 124, 556, 96),
                    new Bounds(260, 202, 512, 360),
                    new Bounds(400, 526, 232, 56),
                    new Bounds(148, 652, 728, 232),
                    new Bounds(174, 89, 112, 112),
                    0
            );
        }
    }

    public record Bounds(int x, int y, int width, int height) {
    }
}
