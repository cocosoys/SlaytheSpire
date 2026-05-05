package soys.mods.slaythespire.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import soys.mods.slaythespire.combat.CombatStateSnapshot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.function.Supplier;

public final class CombatStateSyncS2CPacket {
    private final CombatStateSnapshot snapshot;

    public CombatStateSyncS2CPacket(CombatStateSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public CombatStateSyncS2CPacket(FriendlyByteBuf buffer) {
        this(new CombatStateSnapshot(
                buffer.readBoolean(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readFloat(),
                buffer.readFloat()
        ));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(snapshot.inCombat());
        buffer.writeInt(snapshot.energy());
        buffer.writeInt(snapshot.maxEnergy());
        buffer.writeInt(snapshot.block());
        buffer.writeInt(snapshot.strength());
        buffer.writeInt(snapshot.turn());
        buffer.writeFloat(snapshot.currentHp());
        buffer.writeFloat(snapshot.maxHp());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> applyClientSnapshot(snapshot)));
        context.setPacketHandled(true);
    }

    private static void applyClientSnapshot(CombatStateSnapshot snapshot) {
        try {
            Class<?> clientState = Class.forName("soys.mods.slaythespire.client.ClientCombatState");
            Method apply = clientState.getMethod("apply", CombatStateSnapshot.class);
            apply.invoke(null, snapshot);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to apply client combat state", exception);
        }
    }
}
