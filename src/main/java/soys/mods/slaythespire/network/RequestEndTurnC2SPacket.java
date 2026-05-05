package soys.mods.slaythespire.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import soys.mods.slaythespire.combat.CombatService;

import java.util.function.Supplier;

public final class RequestEndTurnC2SPacket {
    public RequestEndTurnC2SPacket() {
    }

    public RequestEndTurnC2SPacket(FriendlyByteBuf ignored) {
        this();
    }

    public static void encode(RequestEndTurnC2SPacket packet, FriendlyByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                CombatService.endTurnNow(sender);
            }
        });
        context.setPacketHandled(true);
    }
}
