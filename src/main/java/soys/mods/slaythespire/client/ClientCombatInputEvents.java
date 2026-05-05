package soys.mods.slaythespire.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.client.hud.EndTurnButtonState;
import soys.mods.slaythespire.network.ModNetworking;

@Mod.EventBusSubscriber(modid = Slaythespire.MODID, value = Dist.CLIENT)
public final class ClientCombatInputEvents {
    private ClientCombatInputEvents() {
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT || event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null || !ClientCombatState.snapshot().inCombat()) {
            return;
        }

        double mouseX = minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
        double mouseY = minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();
        if (EndTurnButtonState.contains(mouseX, mouseY)) {
            ModNetworking.requestEndTurn();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen != null || !ClientCombatState.snapshot().inCombat()) {
            return;
        }

        KeyMapping mapping = ClientModKeys.END_TURN;
        if (mapping == null) {
            return;
        }

        while (mapping.consumeClick()) {
            ModNetworking.requestEndTurn();
        }
    }
}
