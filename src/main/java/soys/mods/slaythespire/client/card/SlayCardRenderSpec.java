package soys.mods.slaythespire.client.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;

/**
 * 中文：单张卡牌的渲染规格。它把 Java API 注册出的 CardDefinition 映射到一组可绘制素材和 1024 画布布局。
 * English: Render specification for one card. It maps a Java-registered CardDefinition to drawable assets and a 1024-canvas layout.
 */
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
    /**
     * 中文：卡图资源引用。当前统一使用 1024Portraits 下的清晰卡图，不再使用旧 purple/cardart 素材。
     * English: Card portrait reference. The current renderer consistently uses sharp 1024Portraits art instead of the old purple/cardart assets.
     */
    public record CardArt(ResourceLocation texture) {
        // 中文：按 1024Portraits 相对路径创建卡图引用。
        // English: Creates a portrait reference from a path relative to 1024Portraits.
        public static CardArt cardArt(String path) {
            // 中文：path 只传 cards/1024Portraits 后的相对路径，调用处不需要重复写命名空间和 textures 前缀。
            // English: path is relative to cards/1024Portraits, so callers do not repeat namespace or the textures prefix.
            return new CardArt(
                    ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "textures/cards/1024portraits/" + path)
            );
        }
    }

    /**
     * 中文：卡牌布局矩形集合。所有 Bounds 坐标都使用 1024x1024 牌面空间，渲染器再换算到物品四边形。
     * English: Set of card layout rectangles. Every Bounds uses 1024x1024 card-space coordinates and the renderer converts them to item quads.
     */
    public record Layout(
            Bounds title,
            Bounds art,
            Bounds type,
            Bounds body,
            Bounds cost,
            int titleDecorationOffsetY
    ) {
        // 中文：返回攻击牌使用的 1024 画布布局。
        // English: Returns the 1024-canvas layout used by attack cards.
        public static Layout attack() {
            // 中文：攻击牌使用矩形卡图窗口，类型槽贴在攻击框下沿，整体参考原作 Strike 类卡牌。
            // English: Attack cards use a rectangular portrait window, with the type plate attached to the frame bottom, following original Strike-like cards.
            return new Layout(
                    new Bounds(236, 154, 556, 88),
                    new Bounds(260, 202, 512, 360),
                    new Bounds(410, 536, 208, 56),
                    new Bounds(148, 636, 728, 248),
                    new Bounds(174, 89, 112, 112),
                    -4
            );
        }

        // 中文：返回技能牌使用的 1024 画布布局。
        // English: Returns the 1024-canvas layout used by skill cards.
        public static Layout skill() {
            // 中文：技能牌当前和攻击牌共用矩形布局，只替换底板、卡图框和具体卡图。
            // English: Skill cards currently share the attack rectangular layout and only swap background, frame, and portrait assets.
            return new Layout(
                    new Bounds(236, 154, 556, 88),
                    new Bounds(260, 202, 512, 360),
                    new Bounds(410, 536, 208, 56),
                    new Bounds(148, 636, 728, 248),
                    new Bounds(174, 89, 112, 112),
                    -4
            );
        }

        // 中文：返回能力牌使用的 1024 画布布局。
        // English: Returns the 1024-canvas layout used by power cards.
        public static Layout power() {
            // 中文：能力牌使用椭圆卡图框，正文区域稍微下移，给圆形装饰和标题横幅留出重叠空间。
            // English: Power cards use an oval portrait frame and move the body region slightly downward to leave room for the circular decoration and title banner overlap.
            return new Layout(
                    new Bounds(236, 154, 556, 88),
                    new Bounds(260, 202, 512, 360),
                    new Bounds(396, 536, 232, 56),
                    new Bounds(148, 652, 728, 232),
                    new Bounds(174, 89, 112, 112),
                    0
            );
        }
    }

    /**
     * 中文：1024 画布中的矩形区域，x/y 是左上角，width/height 是区域尺寸。
     * English: Rectangle in the 1024 canvas. x/y are the top-left corner and width/height are the area size.
     */
    public record Bounds(int x, int y, int width, int height) {
    }
}
