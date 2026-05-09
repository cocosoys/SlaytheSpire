package soys.mods.slaythespire.client.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardRarity;
import soys.mods.slaythespire.card.CardDefinitions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 中文：卡牌渲染规格注册表。开发者新增 Java 卡牌定义后，只要卡牌 id 和卡图命名遵守约定，就会自动得到对应渲染规格。
 * English: Registry for card render specifications. After a developer adds a Java card definition, matching id and portrait naming conventions automatically produce a render spec.
 */
public final class SlayCardRenderSpecs {
    // 中文：当前红色牌统一使用红色费用球；后续接入其他角色时，可以按角色模板替换这里的 orb。
    // English: Red cards currently share the red energy orb; future character templates can replace the orb per character.
    private static final CardUiAtlasRegion RED_ORB = CardUiAtlasRegion.REGION_1024_CARD_RED_ORB;

    // 中文：以下模板对应红色攻击、技能、能力牌。模板负责选择底板、卡图边框、标题横幅和稀有度装饰。
    // English: The following templates cover red attack, skill, and power cards. A template selects background, portrait frame, title banner, and rarity decorations.
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

    // 中文：禁止实例化静态渲染规格注册表。
    // English: Prevents instantiation of the static render-spec registry.
    private SlayCardRenderSpecs() {
    }

    // 中文：按卡牌 id 获取渲染规格，缺失时直接报错。
    // English: Gets the render spec by card id and throws immediately when missing.
    public static SlayCardRenderSpec require(ResourceLocation id) {
        // 中文：渲染规格缺失代表卡牌定义和素材约定不一致，直接抛错比渲染一张空卡更容易定位问题。
        // English: A missing render spec means the card definition and asset convention are out of sync; throwing is easier to diagnose than drawing a blank card.
        SlayCardRenderSpec spec = SPECS.get(id);
        if (spec == null) {
            throw new IllegalArgumentException("Missing card render spec: " + id);
        }
        return spec;
    }

    // 中文：把单张卡牌定义和卡图组合成最终渲染规格。
    // English: Combines one card definition and portrait into the final render spec.
    private static SlayCardRenderSpec spec(CardDefinition definition, SlayCardRenderSpec.CardArt art) {
        // 中文：单张卡牌只在这里完成“定义 -> 模板 -> 渲染规格”的转换。
        // English: This is the only conversion point from card definition to template and then to render spec.
        return template(definition).create(definition.id(), art);
    }

    // 中文：根据已注册卡牌定义批量创建渲染规格映射。
    // English: Creates the render-spec map from registered card definitions.
    private static Map<ResourceLocation, SlayCardRenderSpec> createSpecs() {
        // 中文：保留 LinkedHashMap 的插入顺序，便于调试时和 CardDefinitions.all() 的注册顺序对应。
        // English: LinkedHashMap keeps insertion order so debugging matches the order from CardDefinitions.all().
        Map<ResourceLocation, SlayCardRenderSpec> specs = new LinkedHashMap<>();
        for (CardDefinition definition : CardDefinitions.all()) {
            specs.put(definition.id(), spec(definition, art(definition)));
        }
        return Map.copyOf(specs);
    }

    // 中文：根据卡牌类型和 id 推导 1024 卡图资源。
    // English: Derives the 1024 portrait resource from card type and id.
    private static SlayCardRenderSpec.CardArt art(CardDefinition definition) {
        // 中文：卡图路径由卡牌类型决定目录，由卡牌 id 决定文件名，减少每张卡重复填写资源路径。
        // English: The portrait path uses card type for the folder and card id for the filename, avoiding repeated asset paths per card.
        String folder = switch (definition.type()) {
            case ATTACK -> "attack";
            case SKILL -> "skill";
            case POWER -> "power";
            case STATUS -> "status";
        };
        return SlayCardRenderSpec.CardArt.cardArt("red/" + folder + "/" + artName(definition) + ".png");
    }

    // 中文：把卡牌物品 id 转换为卡图文件名。
    // English: Converts the card item id into a portrait filename.
    private static String artName(CardDefinition definition) {
        // 中文：物品 id 保留 _card 后缀，卡图文件名去掉该后缀，以贴近杀戮尖塔原始卡名资源。
        // English: Item ids keep the _card suffix, while portrait filenames remove it to stay closer to original Slay the Spire card asset names.
        String path = definition.id().getPath();
        return path.endsWith("_card") ? path.substring(0, path.length() - "_card".length()) : path;
    }

    // 中文：选择适合该卡牌类型和稀有度的视觉模板。
    // English: Selects the visual template for the card type and rarity.
    private static Template template(CardDefinition definition) {
        // 中文：当前实现只迁移红色牌，攻击和技能按类型选模板，能力牌再按稀有度切换装饰。
        // English: The current implementation migrates red cards only. Attacks and skills select by type, while powers also select decorations by rarity.
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
        // 中文：用模板参数实例化单张卡的渲染规格。
        // English: Instantiates the render spec for one card from template parameters.
        private SlayCardRenderSpec create(ResourceLocation cardId, SlayCardRenderSpec.CardArt art) {
            // 中文：模板创建规格时只补入卡牌 id、卡图和费用球，其余视觉层来自模板本身。
            // English: Template creation only injects card id, portrait, and cost orb; the other visual layers come from the template itself.
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
