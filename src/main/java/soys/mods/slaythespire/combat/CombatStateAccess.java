package soys.mods.slaythespire.combat;

import net.minecraft.world.entity.player.Player;

public final class CombatStateAccess {
    private CombatStateAccess() {
    }

    public static CombatState get(Player player) {
        return player.getCapability(CombatStateProvider.CAPABILITY)
                .orElseThrow(() -> new IllegalStateException("Missing combat state for player " + player.getScoreboardName()));
    }
}
