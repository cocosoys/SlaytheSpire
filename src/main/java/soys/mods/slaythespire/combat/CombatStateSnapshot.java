package soys.mods.slaythespire.combat;

import net.minecraft.server.level.ServerPlayer;

/**
 * 中文：同步给客户端 HUD 的轻量战斗快照。只包含显示所需字段，不暴露完整服务端 CombatState。
 * English: Lightweight combat snapshot for the client HUD. It includes only display fields and does not expose full server CombatState.
 */
public record CombatStateSnapshot(
        boolean inCombat,
        int energy,
        int maxEnergy,
        int block,
        int strength,
        float currentHp,
        float maxHp
) {
    // 中文：从服务端玩家实体和战斗状态生成客户端 HUD 快照。
    // English: Creates a client HUD snapshot from the server player entity and combat state.
    public static CombatStateSnapshot from(ServerPlayer player, CombatState state) {
        // 中文：生命值直接取玩家实体，能量/格挡/力量取战斗 Capability。
        // English: Health comes from the player entity, while energy, block, and strength come from the combat Capability.
        return new CombatStateSnapshot(
                state.isInCombat(),
                state.getEnergy(),
                state.getMaxEnergy(),
                state.getBlock(),
                state.getStrength(),
                player.getHealth(),
                player.getMaxHealth()
        );
    }
}
