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

public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean registered;
    private static int nextId;

    private ModNetworking() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        CHANNEL.registerMessage(
                nextId++,
                CombatStateSyncS2CPacket.class,
                CombatStateSyncS2CPacket::encode,
                CombatStateSyncS2CPacket::new,
                CombatStateSyncS2CPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                RequestEndTurnC2SPacket.class,
                RequestEndTurnC2SPacket::encode,
                RequestEndTurnC2SPacket::new,
                RequestEndTurnC2SPacket::handle
        );
        registered = true;
    }

    public static void sync(ServerPlayer player) {
        CombatService.refreshCardPreviews(player);
        CombatState state = CombatStateAccess.get(player);
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CombatStateSyncS2CPacket(CombatStateSnapshot.from(player, state)));
    }

    public static void requestEndTurn() {
        CHANNEL.sendToServer(new RequestEndTurnC2SPacket());
    }

}
