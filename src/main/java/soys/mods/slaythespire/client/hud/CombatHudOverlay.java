package soys.mods.slaythespire.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import soys.mods.slaythespire.client.ClientCombatState;
import soys.mods.slaythespire.combat.CombatStateSnapshot;

public enum CombatHudOverlay implements IGuiOverlay {
    INSTANCE;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        CombatStateSnapshot state = ClientCombatState.snapshot();
        if (!state.inCombat()) {
            EndTurnButtonState.hide();
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int x = 10;
        int y = 10;
        int buttonWidth = 82;
        int buttonHeight = 18;
        int buttonX = screenWidth - buttonWidth - 10;
        int buttonY = screenHeight - buttonHeight - 28;

        drawBar(graphics, font, x, y, 110, 0xAA2D0B0B, 0xAAE74C3C, "HP", state.currentHp(), state.maxHp());
        drawBar(graphics, font, x, y + 16, 110, 0xAA0A3045, 0xAA3BB8FF, "EN", state.energy(), state.maxEnergy());

        graphics.fill(x, y + 34, x + 110, y + 46, 0xAA2A2A2A);
        graphics.drawString(font, "BLK " + state.block() + "  STR " + state.strength() + "  T" + state.turn(), x + 4, y + 37, 0xFFFFFF, false);

        graphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 0xCC4B1818);
        // Show the configured key mapping in the HUD label if available
        if (soys.mods.slaythespire.client.ClientModKeys.END_TURN != null) {
            graphics.drawCenteredString(font, Component.translatable("hud.slaythespire.end_turn", soys.mods.slaythespire.client.ClientModKeys.END_TURN.getTranslatedKeyMessage()), buttonX + buttonWidth / 2, buttonY + 5, 0xFFFFFF);
        } else {
            graphics.drawCenteredString(font, Component.translatable("hud.slaythespire.end_turn", "N"), buttonX + buttonWidth / 2, buttonY + 5, 0xFFFFFF);
        }
        EndTurnButtonState.update(buttonX, buttonY, buttonWidth, buttonHeight, true);
    }

    private static void drawBar(GuiGraphics graphics, Font font, int x, int y, int width, int background, int fillColor, String label, float value, float max) {
        float clampedMax = Math.max(1.0F, max);
        float ratio = Math.max(0.0F, Math.min(1.0F, value / clampedMax));
        int filledWidth = Math.max(0, Math.min(width, Math.round(width * ratio)));

        graphics.fill(x, y, x + width, y + 12, background);
        graphics.fill(x, y, x + filledWidth, y + 12, fillColor);
        graphics.drawString(font, label + " " + Math.round(value) + "/" + Math.round(max), x + 4, y + 2, 0xFFFFFF, false);
    }
}
