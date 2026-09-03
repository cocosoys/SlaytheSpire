package soys.mods.slaythespire.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.network.ModNetworking;

/**
 * 中文：客户端 Forge 总线 tick 事件，处理按键检测等每帧逻辑。
 * English: Client-side Forge bus tick events, handling per-frame logic such as key press detection.
 */
@Mod.EventBusSubscriber(modid = Slaythespire.MODID, value = Dist.CLIENT)
public final class ClientTickEvents {

    private ClientTickEvents() {
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }
        // 中文：检测回合结束按键，按下一次发送一次请求，不连续触发。
        // English: Detect end-turn key press; send one request per press without continuous triggering.
        while (KeyBindings.END_TURN.consumeClick()) {
            ModNetworking.sendEndTurn();
        }
    }
}
