package soys.mods.slaythespire.client;

import soys.mods.slaythespire.combat.CombatStateSnapshot;

/**
 * 中文：客户端 HUD 使用的最近一次战斗快照。它只由服务端同步包更新，不参与任何战斗结算。
 * English: Latest combat snapshot for the client HUD. It is updated only by server sync packets and never participates in combat settlement.
 */
public final class ClientCombatState {
    // 中文：默认值让 HUD 在尚未收到同步包时也能安全读取。
    // English: The default value lets the HUD read safely before the first sync packet arrives.
    private static CombatStateSnapshot snapshot = new CombatStateSnapshot(false, 0, 3, 0, 0, 0.0F, 20.0F);

    // 中文：禁止实例化客户端战斗快照持有类。
    // English: Prevents instantiation of this client combat snapshot holder.
    private ClientCombatState() {
    }

    // 中文：应用服务端同步来的最新战斗快照。
    // English: Applies the latest combat snapshot synced from the server.
    public static void apply(CombatStateSnapshot nextSnapshot) {
        snapshot = nextSnapshot;
    }

    // 中文：返回客户端当前缓存的战斗快照。
    // English: Returns the combat snapshot currently cached on the client.
    public static CombatStateSnapshot snapshot() {
        return snapshot;
    }
}
