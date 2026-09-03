package soys.mods.slaythespire.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.client.hud.CombatHudOverlay;
import soys.mods.slaythespire.client.ironclad.IroncladPlayerLayer;
import soys.mods.slaythespire.client.ironclad.IroncladPlayerLayers;

/**
 * 中文：客户端 mod 事件注册。这里只注册 HUD overlay 和客户端渲染层，避免把客户端事件混入服务端公共入口。
 * English: Client mod event registration. This class registers HUD overlays and client render layers while keeping them out of common server entrypoints.
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

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // 中文：铁甲战士模型层只在客户端模型注册阶段创建，公共入口不会加载这些类。
        // English: The Ironclad model layer is created only during client model registration, keeping common entrypoints free of client classes.
        event.registerLayerDefinition(IroncladPlayerLayers.IRONCLAD, IroncladPlayerLayers::createBodyLayer);
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            EntityRenderer<? extends Player> renderer = event.getPlayerSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new IroncladPlayerLayer(playerRenderer, event.getEntityModels().bakeLayer(IroncladPlayerLayers.IRONCLAD)));
            }
        }
    }
}
