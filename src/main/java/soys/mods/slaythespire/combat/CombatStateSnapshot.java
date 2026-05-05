package soys.mods.slaythespire.combat;

import net.minecraft.server.level.ServerPlayer;

public record CombatStateSnapshot(
        boolean inCombat,
        int energy,
        int maxEnergy,
        int block,
        int strength,
        int turn,
        float currentHp,
        float maxHp
) {
    public static CombatStateSnapshot from(ServerPlayer player, CombatState state) {
        return new CombatStateSnapshot(
                state.isInCombat(),
                state.getEnergy(),
                state.getMaxEnergy(),
                state.getBlock(),
                state.getStrength(),
                state.getTurn(),
                player.getHealth(),
                player.getMaxHealth()
        );
    }
}
