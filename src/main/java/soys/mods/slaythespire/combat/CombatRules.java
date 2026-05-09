package soys.mods.slaythespire.combat;

/**
 * 中文：战斗系统常量。把基础数值集中到这里，避免卡牌、HUD、测试各自硬编码同一套规则。
 * English: Combat-system constants. Shared values live here so cards, HUD, and tests do not hardcode separate copies of the same rules.
 */
public final class CombatRules {
    // 中文：当前实现沿用杀戮尖塔基础 3 点能量。
    // English: The current implementation follows Slay the Spire's base 3 energy.
    public static final int MAX_ENERGY = 3;
    // 中文：长时间没有继续打牌时自动退出战斗，避免玩家永久停留在战斗状态。
    // English: Combat exits automatically after inactivity so players are not stuck in combat state.
    public static final int COMBAT_TIMEOUT_TICKS = 200;
    // 中文：无固定目标时在玩家周围寻找敌人的半径。
    // English: Radius used to find nearby enemies when no fixed target is active.
    public static final double ENEMY_SEARCH_RADIUS = 16.0D;
    public static final int TEMP_DRAW_COUNT = 1;
    public static final int BURN_DAMAGE = 2;
    public static final int STRIKE_DAMAGE = 6;
    public static final int DEFEND_BLOCK = 5;
    public static final int RAGE_STRENGTH = 1;

    private CombatRules() {
    }
}
