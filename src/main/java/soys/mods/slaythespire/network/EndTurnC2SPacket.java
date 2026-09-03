package soys.mods.slaythespire.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import soys.mods.slaythespire.combat.CombatService;

import java.util.function.Supplier;

/**
 * 中文：客户端到服务端的回合结束请求包。玩家按下回合结束键时发送，服务端收到后执行回合结束结算。
 * English: Client-to-server end-turn request packet. Sent when the player presses the end-turn key; the server performs end-turn settlement on receipt.
 */
public final class EndTurnC2SPacket {

    // 中文：创建回合结束请求包。包体为空，仅作为信号。
    // English: Creates an end-turn request packet. The body is empty; it acts only as a signal.
    public EndTurnC2SPacket() {
    }

    // 中文：从网络缓冲区反序列化回合结束请求包。
    // English: Deserializes an end-turn request packet from the network buffer.
    public EndTurnC2SPacket(FriendlyByteBuf buffer) {
        // 中文：无字段需要读取。
        // English: No fields to read.
    }

    // 中文：把回合结束请求写入网络缓冲区。
    // English: Writes the end-turn request into the network buffer.
    public void encode(FriendlyByteBuf buffer) {
        // 中文：无字段需要写入。
        // English: No fields to write.
    }

    // 中文：在服务端网络线程收到回合结束请求后排队执行结算。
    // English: Queues end-turn settlement after the request is received on the server network thread.
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CombatService.endTurn(player);
            }
        });
        context.setPacketHandled(true);
    }
}
