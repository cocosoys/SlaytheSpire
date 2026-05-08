package soys.mods.slaythespire.client.card;

import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;

public enum CardUiAtlasRegion {
    /** 1024 尺寸普通卡牌标题横幅，来自 cardui.png。 */
    REGION_1024_BANNER_COMMON("cardui.png", "1024/banner_common", 2048, 2048, false, 540, 200, 648, 153, 1024, 1024, 191, 741, -1),
    /** 1024 尺寸稀有卡牌标题横幅，来自 cardui.png。 */
    REGION_1024_BANNER_RARE("cardui.png", "1024/banner_rare", 2048, 2048, false, 538, 45, 648, 153, 1024, 1024, 188, 741, -1),
    /** 1024 尺寸无色攻击牌底板，来自 cardui.png。 */
    REGION_1024_BG_ATTACK_COLORLESS("cardui.png", "1024/bg_attack_colorless", 2048, 2048, false, 2, 1196, 618, 839, 1024, 1024, 210, 90, -1),
    /** 1024 尺寸红色攻击牌底板，来自 cardui.png。 */
    REGION_1024_BG_ATTACK_RED("cardui.png", "1024/bg_attack_red", 2048, 2048, false, 1224, 1197, 608, 838, 1024, 1024, 211, 91, -1),
    /** 1024 尺寸无色能力牌底板，来自 cardui.png。 */
    REGION_1024_BG_POWER_COLORLESS("cardui.png", "1024/bg_power_colorless", 2048, 2048, false, 540, 355, 600, 839, 1024, 1024, 210, 92, -1),
    /** 1024 尺寸黑色技能牌底板，来自 cardui.png。 */
    REGION_1024_BG_SKILL_BLACK("cardui.png", "1024/bg_skill_black", 2048, 2048, false, 622, 1196, 600, 839, 1024, 1024, 210, 91, -1),
    /** 1024 尺寸无色技能牌底板，来自 cardui.png。 */
    REGION_1024_BG_SKILL_COLORLESS("cardui.png", "1024/bg_skill_colorless", 2048, 2048, false, 1142, 355, 600, 839, 1024, 1024, 210, 91, -1),
    /** 1024 尺寸蓝色费用球，来自 cardui.png。 */
    REGION_1024_CARD_BLUE_ORB("cardui.png", "1024/card_blue_orb", 2048, 2048, false, 1834, 1318, 132, 132, 164, 164, 19, 13, -1),
    /** 1024 尺寸无色费用球，来自 cardui.png。 */
    REGION_1024_CARD_COLORLESS_ORB("cardui.png", "1024/card_colorless_orb", 2048, 2048, false, 1834, 1748, 143, 143, 164, 164, 14, 8, -1),
    /** 1024 尺寸绿色费用球，来自 cardui.png。 */
    REGION_1024_CARD_GREEN_ORB("cardui.png", "1024/card_green_orb", 2048, 2048, false, 1834, 1893, 145, 142, 164, 164, 12, 9, -1),
    /** 1024 尺寸紫色费用球，来自 cardui.png。 */
    REGION_1024_CARD_PURPLE_ORB("cardui.png", "1024/card_purple_orb", 2048, 2048, false, 1834, 1452, 142, 152, 164, 164, 14, 5, -1),
    /** 1024 尺寸红色费用球，来自 cardui.png。 */
    REGION_1024_CARD_RED_ORB("cardui.png", "1024/card_red_orb", 2048, 2048, false, 1834, 1606, 143, 140, 164, 164, 12, 8, -1),
    /** 1024 尺寸普通卡牌标题装饰中段，来自 cardui.png。 */
    REGION_1024_COMMON_CENTER("cardui.png", "1024/common_center", 2048, 2048, false, 1905, 1198, 62, 45, 1024, 1024, 483, 449, -1),
    /** 1024 尺寸普通卡牌标题装饰左段，来自 cardui.png。 */
    REGION_1024_COMMON_LEFT("cardui.png", "1024/common_left", 2048, 2048, false, 2012, 1489, 29, 45, 1024, 1024, 454, 449, -1),
    /** 1024 尺寸普通卡牌标题装饰右段，来自 cardui.png。 */
    REGION_1024_COMMON_RIGHT("cardui.png", "1024/common_right", 2048, 2048, false, 1981, 1990, 28, 45, 1024, 1024, 545, 449, -1),
    /** 1024 尺寸普通能力牌卡图边框，来自 cardui.png。 */
    REGION_1024_FRAME_POWER_COMMON("cardui.png", "1024/frame_power_common", 2048, 2048, false, 2, 719, 536, 475, 1024, 1024, 246, 448, -1),
    /** 1024 尺寸稀有能力牌卡图边框，来自 cardui.png。 */
    REGION_1024_FRAME_POWER_RARE("cardui.png", "1024/frame_power_rare", 2048, 2048, false, 2, 242, 536, 475, 1024, 1024, 246, 448, -1),
    /** 1024 尺寸稀有卡牌标题装饰中段，来自 cardui.png。 */
    REGION_1024_RARE_CENTER("cardui.png", "1024/rare_center", 2048, 2048, false, 1969, 1198, 62, 45, 1024, 1024, 483, 449, -1),
    /** 1024 尺寸稀有卡牌标题装饰左段，来自 cardui.png。 */
    REGION_1024_RARE_LEFT("cardui.png", "1024/rare_left", 2048, 2048, false, 1978, 1464, 29, 45, 1024, 1024, 454, 449, -1),
    /** 1024 尺寸稀有卡牌标题装饰右段，来自 cardui.png。 */
    REGION_1024_RARE_RIGHT("cardui.png", "1024/rare_right", 2048, 2048, false, 1981, 1943, 28, 45, 1024, 1024, 545, 449, -1),
    /** 1024 尺寸罕见卡牌标题装饰中段，来自 cardui.png。 */
    REGION_1024_UNCOMMON_CENTER("cardui.png", "1024/uncommon_center", 2048, 2048, false, 1979, 1263, 62, 45, 1024, 1024, 483, 449, -1),
    /** 1024 尺寸罕见卡牌标题装饰左段，来自 cardui.png。 */
    REGION_1024_UNCOMMON_LEFT("cardui.png", "1024/uncommon_left", 2048, 2048, false, 1979, 1846, 29, 45, 1024, 1024, 454, 449, -1),
    /** 1024 尺寸罕见卡牌标题装饰右段，来自 cardui.png。 */
    REGION_1024_UNCOMMON_RIGHT("cardui.png", "1024/uncommon_right", 2048, 2048, false, 1981, 1896, 28, 45, 1024, 1024, 545, 449, -1),
    /** 512 尺寸普通卡牌标题横幅，来自 cardui.png。 */
    REGION_512_BANNER_COMMON("cardui.png", "512/banner_common", 2048, 2048, false, 1188, 36, 324, 77, 512, 512, 94, 378, -1),
    /** 512 尺寸稀有卡牌标题横幅，来自 cardui.png。 */
    REGION_512_BANNER_RARE("cardui.png", "512/banner_rare", 2048, 2048, false, 1514, 36, 324, 77, 512, 512, 94, 378, -1),
    /** 512 尺寸蓝色攻击牌底板，来自 cardui.png。 */
    REGION_512_BG_ATTACK_BLUE("cardui.png", "512/bg_attack_blue", 2048, 2048, false, 1744, 776, 302, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸绿色攻击牌底板，来自 cardui.png。 */
    REGION_512_BG_ATTACK_GREEN("cardui.png", "512/bg_attack_green", 2048, 2048, false, 1744, 355, 302, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸蓝色费用球，来自 cardui.png。 */
    REGION_512_CARD_BLUE_ORB("cardui.png", "512/card_blue_orb", 2048, 2048, false, 1979, 1310, 67, 67, 512, 512, 90, 415, -1),
    /** 512 尺寸无色费用球，来自 cardui.png。 */
    REGION_512_CARD_COLORLESS_ORB("cardui.png", "512/card_colorless_orb", 2048, 2048, false, 1978, 1536, 68, 68, 512, 512, 91, 413, -1),
    /** 512 尺寸绿色费用球，来自 cardui.png。 */
    REGION_512_CARD_GREEN_ORB("cardui.png", "512/card_green_orb", 2048, 2048, false, 1905, 1245, 72, 71, 512, 512, 88, 413, -1),
    /** 512 尺寸紫色费用球，来自 cardui.png。 */
    REGION_512_CARD_PURPLE_ORB("cardui.png", "512/card_purple_orb", 2048, 2048, false, 1834, 1239, 69, 77, 512, 512, 90, 411, -1),
    /** 512 尺寸红色费用球，来自 cardui.png。 */
    REGION_512_CARD_RED_ORB("cardui.png", "512/card_red_orb", 2048, 2048, false, 1968, 1379, 72, 71, 512, 512, 87, 412, -1),
    /** 512 尺寸普通卡牌标题装饰中段，来自 cardui.png。 */
    REGION_512_COMMON_CENTER("cardui.png", "512/common_center", 2048, 2048, false, 1834, 1214, 32, 23, 512, 512, 241, 224, -1),
    /** 512 尺寸普通卡牌标题装饰左段，来自 cardui.png。 */
    REGION_512_COMMON_LEFT("cardui.png", "512/common_left", 2048, 2048, false, 1979, 1796, 14, 23, 512, 512, 228, 224, -1),
    /** 512 尺寸普通卡牌标题装饰右段，来自 cardui.png。 */
    REGION_512_COMMON_RIGHT("cardui.png", "512/common_right", 2048, 2048, false, 2009, 1464, 15, 23, 512, 512, 272, 224, -1),
    /** 512 尺寸罕见攻击牌卡图边框，来自 cardui.png。 */
    REGION_512_FRAME_ATTACK_UNCOMMON("cardui.png", "512/frame_attack_uncommon", 2048, 2048, false, 273, 55, 263, 185, 512, 512, 124, 219, -1),
    /** 512 尺寸普通能力牌卡图边框，来自 cardui.png。 */
    REGION_512_FRAME_POWER_COMMON("cardui.png", "512/frame_power_common", 2048, 2048, false, 2, 2, 269, 238, 512, 512, 121, 222, -1),
    /** 512 尺寸稀有能力牌卡图边框，来自 cardui.png。 */
    REGION_512_FRAME_POWER_RARE("cardui.png", "512/frame_power_rare", 2048, 2048, false, 1190, 115, 269, 238, 512, 512, 121, 222, -1),
    /** 512 尺寸罕见能力牌卡图边框，来自 cardui.png。 */
    REGION_512_FRAME_POWER_UNCOMMON("cardui.png", "512/frame_power_uncommon", 2048, 2048, false, 1461, 115, 269, 238, 512, 512, 121, 222, -1),
    /** 512 尺寸普通技能牌卡图边框，来自 cardui.png。 */
    REGION_512_FRAME_SKILL_COMMON("cardui.png", "512/frame_skill_common", 2048, 2048, false, 1732, 170, 263, 183, 512, 512, 124, 222, -1),
    /** 512 尺寸稀有卡牌标题装饰中段，来自 cardui.png。 */
    REGION_512_RARE_CENTER("cardui.png", "512/rare_center", 2048, 2048, false, 1868, 1214, 32, 23, 512, 512, 241, 224, -1),
    /** 512 尺寸稀有卡牌标题装饰左段，来自 cardui.png。 */
    REGION_512_RARE_LEFT("cardui.png", "512/rare_left", 2048, 2048, false, 1979, 1771, 14, 23, 512, 512, 228, 224, -1),
    /** 512 尺寸稀有卡牌标题装饰右段，来自 cardui.png。 */
    REGION_512_RARE_RIGHT("cardui.png", "512/rare_right", 2048, 2048, false, 2026, 1464, 15, 23, 512, 512, 272, 224, -1),
    /** 512 尺寸罕见卡牌标题装饰中段，来自 cardui.png。 */
    REGION_512_UNCOMMON_CENTER("cardui.png", "512/uncommon_center", 2048, 2048, false, 1978, 1511, 32, 23, 512, 512, 241, 224, -1),
    /** 512 尺寸罕见卡牌标题装饰左段，来自 cardui.png。 */
    REGION_512_UNCOMMON_LEFT("cardui.png", "512/uncommon_left", 2048, 2048, false, 1979, 1746, 14, 23, 512, 512, 228, 224, -1),
    /** 512 尺寸罕见卡牌标题装饰右段，来自 cardui.png。 */
    REGION_512_UNCOMMON_RIGHT("cardui.png", "512/uncommon_right", 2048, 2048, false, 1979, 1821, 15, 23, 512, 512, 272, 224, -1),
    /** 1024 尺寸罕见卡牌标题横幅，来自 cardui2.png。 */
    REGION_1024_BANNER_UNCOMMON("cardui2.png", "1024/banner_uncommon", 2048, 2048, false, 529, 212, 648, 153, 1024, 1024, 191, 741, -1),
    /** 1024 尺寸绿色攻击牌底板，来自 cardui2.png。 */
    REGION_1024_BG_ATTACK_GREEN("cardui2.png", "1024/bg_attack_green", 2048, 2048, false, 2, 1207, 607, 838, 1024, 1024, 212, 91, -1),
    /** 1024 尺寸紫色攻击牌底板，来自 cardui2.png。 */
    REGION_1024_BG_ATTACK_PURPLE("cardui2.png", "1024/bg_attack_purple", 2048, 2048, false, 540, 367, 606, 838, 1024, 1024, 212, 91, -1),
    /** 1024 尺寸蓝色能力牌底板，来自 cardui2.png。 */
    REGION_1024_BG_POWER_BLUE("cardui2.png", "1024/bg_power_blue", 2048, 2048, false, 611, 1207, 597, 838, 1024, 1024, 212, 93, -1),
    /** 1024 尺寸灰色能力牌底板，来自 cardui2.png。 */
    REGION_1024_BG_POWER_GRAY("cardui2.png", "1024/bg_power_gray", 2048, 2048, false, 1747, 784, 299, 421, 512, 512, 106, 46, -1),
    /** 512 尺寸灰色能力牌底板，来自 cardui2.png。 */
    REGION_512_BG_POWER_GRAY("cardui2.png", "512/bg_power_gray", 2048, 2048, false, 1747, 784, 299, 421, 512, 512, 106, 46, -1),
    /** 1024 尺寸绿色能力牌底板，来自 cardui2.png。 */
    REGION_1024_BG_POWER_GREEN("cardui2.png", "1024/bg_power_green", 2048, 2048, false, 1148, 367, 597, 838, 1024, 1024, 212, 93, -1),
    /** 1024 尺寸绿色技能牌底板，来自 cardui2.png。 */
    REGION_1024_BG_SKILL_GREEN("cardui2.png", "1024/bg_skill_green", 2048, 2048, false, 1210, 1207, 597, 838, 1024, 1024, 212, 92, -1),
    /** 1024 尺寸罕见能力牌卡图边框，来自 cardui2.png。 */
    REGION_1024_FRAME_POWER_UNCOMMON("cardui2.png", "1024/frame_power_uncommon", 2048, 2048, false, 2, 730, 536, 475, 1024, 1024, 246, 448, -1),
    /** 1024 尺寸普通技能牌卡图边框，来自 cardui2.png。 */
    REGION_1024_FRAME_SKILL_COMMON("cardui2.png", "1024/frame_skill_common", 2048, 2048, false, 1179, 2, 524, 363, 1024, 1024, 251, 449, -1),
    /** 1024 尺寸罕见技能牌卡图边框，来自 cardui2.png。 */
    REGION_1024_FRAME_SKILL_UNCOMMON("cardui2.png", "1024/frame_skill_uncommon", 2048, 2048, false, 2, 365, 525, 363, 1024, 1024, 251, 449, -1),
    /** 512 尺寸罕见卡牌标题横幅，来自 cardui2.png。 */
    REGION_512_BANNER_UNCOMMON("cardui2.png", "512/banner_uncommon", 2048, 2048, false, 1705, 284, 324, 77, 512, 512, 94, 378, -1),
    /** 512 尺寸蓝色能力牌底板，来自 cardui2.png。 */
    REGION_512_BG_POWER_BLUE("cardui2.png", "512/bg_power_blue", 2048, 2048, false, 1747, 363, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸普通攻击牌卡图边框，来自 cardui2.png。 */
    REGION_512_FRAME_ATTACK_COMMON("cardui2.png", "512/frame_attack_common", 2048, 2048, false, 2, 178, 262, 185, 512, 512, 125, 219, -1),
    /** 512 尺寸稀有攻击牌卡图边框，来自 cardui2.png。 */
    REGION_512_FRAME_ATTACK_RARE("cardui2.png", "512/frame_attack_rare", 2048, 2048, false, 266, 25, 262, 185, 512, 512, 125, 219, -1),
    /** 512 尺寸稀有技能牌卡图边框，来自 cardui2.png。 */
    REGION_512_FRAME_SKILL_RARE("cardui2.png", "512/frame_skill_rare", 2048, 2048, false, 1705, 99, 263, 183, 512, 512, 124, 222, -1),
    /** 512 尺寸罕见技能牌卡图边框，来自 cardui2.png。 */
    REGION_512_FRAME_SKILL_UNCOMMON("cardui2.png", "512/frame_skill_uncommon", 2048, 2048, false, 530, 27, 263, 183, 512, 512, 124, 222, -1),
    /** 1024 尺寸蓝色攻击牌底板，来自 cardui3.png。 */
    REGION_1024_BG_ATTACK_BLUE("cardui3.png", "1024/bg_attack_blue", 2048, 2048, false, 527, 366, 608, 837, 1024, 1024, 211, 91, -1),
    /** 1024 尺寸紫色能力牌底板，来自 cardui3.png。 */
    REGION_1024_BG_POWER_PURPLE("cardui3.png", "1024/bg_power_purple", 2048, 2048, false, 601, 1206, 596, 837, 1024, 1024, 212, 93, -1),
    /** 1024 尺寸红色能力牌底板，来自 cardui3.png。 */
    REGION_1024_BG_POWER_RED("cardui3.png", "1024/bg_power_red", 2048, 2048, false, 1137, 367, 596, 837, 1024, 1024, 212, 93, -1),
    /** 1024 尺寸蓝色技能牌底板，来自 cardui3.png。 */
    REGION_1024_BG_SKILL_BLUE("cardui3.png", "1024/bg_skill_blue", 2048, 2048, false, 1199, 1206, 596, 837, 1024, 1024, 212, 92, -1),
    /** 1024 尺寸红色技能牌底板，来自 cardui3.png。 */
    REGION_1024_BG_SKILL_RED("cardui3.png", "1024/bg_skill_red", 2048, 2048, false, 2, 1205, 597, 838, 1024, 1024, 212, 92, -1),
    /** 1024 尺寸普通攻击牌卡图边框，来自 cardui3.png。 */
    REGION_1024_FRAME_ATTACK_COMMON("cardui3.png", "1024/frame_attack_common", 2048, 2048, false, 2, 834, 523, 369, 1024, 1024, 253, 442, -1),
    /** 1024 尺寸稀有攻击牌卡图边框，来自 cardui3.png。 */
    REGION_1024_FRAME_ATTACK_RARE("cardui3.png", "1024/frame_attack_rare", 2048, 2048, false, 2, 463, 523, 369, 1024, 1024, 253, 442, -1),
    /** 1024 尺寸罕见攻击牌卡图边框，来自 cardui3.png。 */
    REGION_1024_FRAME_ATTACK_UNCOMMON("cardui3.png", "1024/frame_attack_uncommon", 2048, 2048, false, 2, 92, 523, 369, 1024, 1024, 253, 442, -1),
    /** 1024 尺寸稀有技能牌卡图边框，来自 cardui3.png。 */
    REGION_1024_FRAME_SKILL_RARE("cardui3.png", "1024/frame_skill_rare", 2048, 2048, false, 1137, 2, 524, 363, 1024, 1024, 251, 449, -1),
    /** 512 尺寸灰色攻击牌底板，来自 cardui3.png。 */
    REGION_512_BG_ATTACK_GRAY("cardui3.png", "512/bg_attack_gray", 2048, 2048, false, 1735, 362, 308, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸剪影攻击牌底板，来自 cardui3.png。 */
    REGION_512_BG_ATTACK_SILHOUETTE("cardui3.png", "512/bg_attack_silhouette", 2048, 2048, false, 1735, 783, 308, 421, 512, 512, 106, 46, -1),
    /** 1024 尺寸紫色技能牌底板，来自 cardui4.png。 */
    REGION_1024_BG_SKILL_PURPLE("cardui4.png", "1024/bg_skill_purple", 2048, 2048, false, 2, 1024, 596, 837, 1024, 1024, 212, 92, -1),
    /** 512 尺寸紫色攻击牌底板，来自 cardui4.png。 */
    REGION_512_BG_ATTACK_PURPLE("cardui4.png", "512/bg_attack_purple", 2048, 2048, false, 968, 1442, 302, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸红色攻击牌底板，来自 cardui4.png。 */
    REGION_512_BG_ATTACK_RED("cardui4.png", "512/bg_attack_red", 2048, 2048, false, 1272, 1442, 302, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸绿色能力牌底板，来自 cardui4.png。 */
    REGION_512_BG_POWER_GREEN("cardui4.png", "512/bg_power_green", 2048, 2048, false, 343, 603, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸紫色能力牌底板，来自 cardui4.png。 */
    REGION_512_BG_POWER_PURPLE("cardui4.png", "512/bg_power_purple", 2048, 2048, false, 343, 182, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸红色能力牌底板，来自 cardui4.png。 */
    REGION_512_BG_POWER_RED("cardui4.png", "512/bg_power_red", 2048, 2048, false, 644, 951, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸剪影能力牌底板，来自 cardui4.png。 */
    REGION_512_BG_POWER_SILHOUETTE("cardui4.png", "512/bg_power_silhouette", 2048, 2048, false, 945, 951, 298, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸黑色技能牌底板，来自 cardui4.png。 */
    REGION_512_BG_SKILL_BLACK("cardui4.png", "512/bg_skill_black", 2048, 2048, false, 644, 530, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸蓝色技能牌底板，来自 cardui4.png。 */
    REGION_512_BG_SKILL_BLUE("cardui4.png", "512/bg_skill_blue", 2048, 2048, false, 644, 109, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸灰色技能牌底板，来自 cardui4.png。 */
    REGION_512_BG_SKILL_GRAY("cardui4.png", "512/bg_skill_gray", 2048, 2048, false, 945, 530, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸绿色技能牌底板，来自 cardui4.png。 */
    REGION_512_BG_SKILL_GREEN("cardui4.png", "512/bg_skill_green", 2048, 2048, false, 945, 109, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸紫色技能牌底板，来自 cardui4.png。 */
    REGION_512_BG_SKILL_PURPLE("cardui4.png", "512/bg_skill_purple", 2048, 2048, false, 1245, 1021, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸红色技能牌底板，来自 cardui4.png。 */
    REGION_512_BG_SKILL_RED("cardui4.png", "512/bg_skill_red", 2048, 2048, false, 1546, 1020, 299, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸剪影技能牌底板，来自 cardui4.png。 */
    REGION_512_BG_SKILL_SILHOUETTE("cardui4.png", "512/bg_skill_silhouette", 2048, 2048, false, 1246, 600, 298, 419, 512, 512, 106, 46, -1),
    /** 512 尺寸卡背，来自 cardui4.png。 */
    REGION_512_CARD_BACK("cardui4.png", "512/card_back", 2048, 2048, false, 2, 2, 317, 433, 512, 512, 106, 32, -1),
    /** 512 尺寸卡牌通用背景，来自 cardui4.png。 */
    REGION_512_CARD_BG("cardui4.png", "512/card_bg", 2048, 2048, false, 1246, 186, 288, 412, 512, 512, 112, 49, -1),
    /** 512 尺寸卡牌闪光特效，来自 cardui4.png。 */
    REGION_512_CARD_FLASH_VFX("cardui4.png", "512/card_flash_vfx", 2048, 2048, false, 2, 437, 339, 585, 512, 800, 85, 86, -1),
    /** 512 尺寸卡牌阴影，来自 cardui4.png。 */
    REGION_512_CARD_SHADOW("cardui4.png", "512/card_shadow", 2048, 2048, false, 1576, 1441, 300, 420, 512, 512, 106, 46, -1),
    /** 512 尺寸卡牌大型阴影，来自 cardui4.png。 */
    REGION_512_CARD_SUPER_SHADOW("cardui4.png", "512/card_super_shadow", 2048, 2048, false, 600, 1372, 366, 489, 512, 512, 73, 10, -1);

    private final ResourceLocation texture;
    private final String atlasName;
    private final int textureWidth;
    private final int textureHeight;
    private final boolean rotated;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int originalWidth;
    private final int originalHeight;
    private final int offsetX;
    private final int offsetY;
    private final int index;

    CardUiAtlasRegion(
            String page,
            String atlasName,
            int textureWidth,
            int textureHeight,
            boolean rotated,
            int x,
            int y,
            int width,
            int height,
            int originalWidth,
            int originalHeight,
            int offsetX,
            int offsetY,
            int index
    ) {
        this.texture = ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "textures/cardui/" + page);
        this.atlasName = atlasName;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.rotated = rotated;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.index = index;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public ResourceLocation resourceLocation() {
        return texture;
    }

    public String atlasName() {
        return atlasName;
    }

    public int textureWidth() {
        return textureWidth;
    }

    public int textureHeight() {
        return textureHeight;
    }

    public boolean rotated() {
        return rotated;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int originalWidth() {
        return originalWidth;
    }

    public int originalHeight() {
        return originalHeight;
    }

    public int offsetX() {
        return offsetX;
    }

    public int offsetY() {
        return offsetY;
    }

    public int index() {
        return index;
    }
}
