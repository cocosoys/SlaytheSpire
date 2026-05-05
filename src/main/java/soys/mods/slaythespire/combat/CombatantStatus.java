package soys.mods.slaythespire.combat;

public final class CombatantStatus {
    private int vulnerable;
    private int weak;
    private int strength;

    public int vulnerable() {
        return vulnerable;
    }

    public int weak() {
        return weak;
    }

    public int strength() {
        return strength;
    }

    public void addVulnerable(int turns) {
        vulnerable += Math.max(0, turns);
    }

    public void addWeak(int turns) {
        weak += Math.max(0, turns);
    }

    public void addStrength(int amount) {
        strength += amount;
    }

    public void onTurnAdvance() {
        if (vulnerable > 0) {
            vulnerable--;
        }
        if (weak > 0) {
            weak--;
        }
    }

    public void clear() {
        vulnerable = 0;
        weak = 0;
        strength = 0;
    }
}
