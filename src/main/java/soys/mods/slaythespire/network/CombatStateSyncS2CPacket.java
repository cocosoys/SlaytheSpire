package soys.mods.slaythespire.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import soys.mods.slaythespire.combat.CombatStateSnapshot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.function.Supplier;

/**
 * 中文：服务端到客户端的战斗状态同步包。它只传 HUD 必需的快照字段，客户端收到后反射调用 ClientCombatState。
 * English: Server-to-client combat-state sync packet. It sends only HUD-required snapshot fields and reflectively updates ClientCombatState on receipt.
 */
public final class CombatStateSyncS2CPacket {
    private final CombatStateSnapshot snapshot;

    // 中文：用服务端生成的战斗快照创建同步包。
    // English: Creates a sync packet from a combat snapshot generated on the server.
    public CombatStateSyncS2CPacket(CombatStateSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    // 中文：从网络缓冲区反序列化战斗同步包。
    // English: Deserializes a combat sync packet from the network buffer.
    public CombatStateSyncS2CPacket(FriendlyByteBuf buffer) {
        // 中文：读写顺序必须和 encode 完全一致；新增字段时两边需要同步更新。
        // English: Read order must exactly match encode; any new field must be added on both sides.
        this(new CombatStateSnapshot(
                buffer.readBoolean(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readFloat(),
                buffer.readFloat()
        ));
    }

    // 中文：把战斗快照写入网络缓冲区。
    // English: Writes the combat snapshot into the network buffer.
    public void encode(FriendlyByteBuf buffer) {
        // 中文：只编码 HUD 快照，不发送完整 CombatState，减少网络耦合。
        // English: Encode only the HUD snapshot rather than full CombatState to reduce network coupling.
        buffer.writeBoolean(snapshot.inCombat());
        buffer.writeInt(snapshot.energy());
        buffer.writeInt(snapshot.maxEnergy());
        buffer.writeInt(snapshot.block());
        buffer.writeInt(snapshot.strength());
        buffer.writeFloat(snapshot.currentHp());
        buffer.writeFloat(snapshot.maxHp());
    }

    // 中文：在客户端网络线程收到同步包后排队应用快照。
    // English: Queues snapshot application after the sync packet is received on the client network thread.
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> applyClientSnapshot(snapshot)));
        context.setPacketHandled(true);
    }

    // 中文：通过反射把快照交给客户端状态缓存。
    // English: Reflectively passes the snapshot to the client state cache.
    private static void applyClientSnapshot(CombatStateSnapshot snapshot) {
        try {
            // 中文：反射避免网络公共类在专用服务端类加载阶段直接引用 client 包。
            // English: Reflection avoids direct references to the client package while this common network class is loaded on dedicated servers.
            Class<?> clientState = Class.forName("soys.mods.slaythespire.client.ClientCombatState");
            Method apply = clientState.getMethod("apply", CombatStateSnapshot.class);
            apply.invoke(null, snapshot);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to apply client combat state", exception);
        }
    }
}
