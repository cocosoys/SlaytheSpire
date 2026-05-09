package soys.mods.slaythespire.combat;

/**
 * 中文：战斗参与者身上的临时状态。玩家和敌人都会挂载它，用于易伤、虚弱和目标自身力量修正。
 * English: Temporary status values on a combatant. Both players and enemies receive it for vulnerable, weak, and entity-local strength modifiers.
 */
public final class CombatantStatus {
    private int vulnerable;
    private int weak;
    private int strength;

    // 中文：返回当前易伤层数。
    // English: Returns the current vulnerable amount.
    public int vulnerable() {
        return vulnerable;
    }

    // 中文：返回当前虚弱层数。
    // English: Returns the current weak amount.
    public int weak() {
        return weak;
    }

    // 中文：返回该战斗实体的力量修正。
    // English: Returns this combatant's strength modifier.
    public int strength() {
        return strength;
    }

    // 中文：增加易伤层数。
    // English: Adds vulnerable turns.
    public void addVulnerable(int turns) {
        vulnerable += Math.max(0, turns);
    }

    // 中文：增加虚弱层数。
    // English: Adds weak turns.
    public void addWeak(int turns) {
        weak += Math.max(0, turns);
    }

    // 中文：修改该战斗实体的力量值。
    // English: Modifies this combatant's strength value.
    public void addStrength(int amount) {
        strength += amount;
    }

    // 中文：清空该战斗实体的临时状态。
    // English: Clears this combatant's temporary status values.
    public void clear() {
        vulnerable = 0;
        weak = 0;
        strength = 0;
    }
}
