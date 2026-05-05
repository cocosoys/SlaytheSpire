package soys.mods.slaythespire.client;

import soys.mods.slaythespire.combat.CombatStateSnapshot;

public final class ClientCombatState {
    private static CombatStateSnapshot snapshot = new CombatStateSnapshot(false, 0, 3, 0, 0, 0, 0.0F, 20.0F);

    private ClientCombatState() {
    }

    public static void apply(CombatStateSnapshot nextSnapshot) {
        snapshot = nextSnapshot;
    }

    public static CombatStateSnapshot snapshot() {
        return snapshot;
    }
}
