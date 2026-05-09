package soys.mods.slaythespire.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.client.hud.CombatHudOverlay;

/**
 * 中文：客户端 mod 事件注册。这里只注册 HUD overlay，避免把客户端事件混入服务端公共入口。
 * English: Client mod event registration. This class registers only HUD overlays and keeps client events out of common server entrypoints.
 */
@Mod.EventBusSubscriber(modid = Slaythespire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        // 中文：战斗 HUD 放在最上层，确保能量、格挡和生命显示不被普通 GUI 覆盖。
        // English: The combat HUD is registered above all overlays so energy, block, and health remain visible over normal GUI layers.
        event.registerAboveAll("combat_state", CombatHudOverlay.INSTANCE);
    }
}
