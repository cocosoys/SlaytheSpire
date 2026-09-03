package soys.mods.slaythespire.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.combat.CombatService;
import soys.mods.slaythespire.combat.CombatState;
import soys.mods.slaythespire.combat.CombatStateAccess;
import soys.mods.slaythespire.combat.CombatStateSnapshot;

/**
 * 中文：网络通道注册与同步入口。包含战斗状态 S2C 同步和回合结束 C2S 请求。
 * English: Network channel registration and sync entrypoint. Includes combat-state S2C sync and end-turn C2S request.
 */
public final class ModNetworking {
    // 中文：协议版本变更会拒绝旧客户端/服务端连接，用于保护包结构兼容性。
    // English: Protocol version mismatch rejects old clients or servers, protecting packet-structure compatibility.
    private static final String PROTOCOL_VERSION = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean registered;
    private static int nextId;

    // 中文：禁止实例化网络注册工具类。
    // English: Prevents instantiation of this networking registration utility.
    private ModNetworking() {
    }

    // 中文：注册本模组网络消息。
    // English: Registers this mod's network messages.
    public static void register() {
        if (registered) {
            return;
        }

        // 中文：只注册一次，防止开发环境重复 common setup 时重复占用 packet id。
        // English: Register only once so repeated common setup in dev does not reuse packet ids.
        CHANNEL.registerMessage(
                nextId++,
                CombatStateSyncS2CPacket.class,
                CombatStateSyncS2CPacket::encode,
                CombatStateSyncS2CPacket::new,
                CombatStateSyncS2CPacket::handle
        );
        // 中文：回合结束请求包，客户端按键后发送到服务端执行回合结算。
        // English: End-turn request packet, sent from client to server on key press to perform turn settlement.
        CHANNEL.registerMessage(
                nextId++,
                EndTurnC2SPacket.class,
                EndTurnC2SPacket::encode,
                EndTurnC2SPacket::new,
                EndTurnC2SPacket::handle
        );
        registered = true;
    }

    // 中文：客户端发送回合结束请求到服务端。
    // English: Client sends an end-turn request to the server.
    public static void sendEndTurn() {
        CHANNEL.sendToServer(new EndTurnC2SPacket());
    }

    // 中文：向指定玩家同步战斗 HUD 快照和卡牌预览数据。
    // English: Syncs the combat HUD snapshot and card preview data to one player.
    public static void sync(ServerPlayer player) {
        // 中文：同步前先刷新卡牌 tooltip 预览 NBT，再发送 HUD 快照，保证客户端看到同一帧的数值。
        // English: Refresh card tooltip preview NBT before sending the HUD snapshot so the client sees values from the same frame.
        CombatService.refreshCardPreviews(player);
        CombatState state = CombatStateAccess.get(player);
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CombatStateSyncS2CPacket(CombatStateSnapshot.from(player, state)));
    }
}
