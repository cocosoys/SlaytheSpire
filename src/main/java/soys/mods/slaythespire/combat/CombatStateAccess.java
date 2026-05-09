package soys.mods.slaythespire.combat;

import net.minecraft.world.entity.player.Player;

/**
 * 中文：玩家 CombatState 的强制访问工具。缺失 Capability 直接抛错，便于尽早发现注册或挂载问题。
 * English: Strict accessor for player CombatState. Missing Capability throws immediately to expose registration or attachment problems early.
 */
public final class CombatStateAccess {
    private CombatStateAccess() {
    }

    public static CombatState get(Player player) {
        return player.getCapability(CombatStateProvider.CAPABILITY)
                .orElseThrow(() -> new IllegalStateException("Missing combat state for player " + player.getScoreboardName()));
    }
}
