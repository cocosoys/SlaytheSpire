package soys.mods.slaythespire.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import soys.mods.slaythespire.client.ClientCombatState;
import soys.mods.slaythespire.combat.CombatStateSnapshot;

/**
 * 中文：轻量战斗 HUD。它只展示服务端同步来的快照，不在客户端推算能量或格挡。
 * English: Lightweight combat HUD. It only displays the server-synced snapshot and does not predict energy or block on the client.
 */
public enum CombatHudOverlay implements IGuiOverlay {
    INSTANCE;

    @Override
    // 中文：在 HUD 层绘制当前服务端同步的战斗状态。
    // English: Draws the currently server-synced combat state on the HUD layer.
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        CombatStateSnapshot state = ClientCombatState.snapshot();
        if (!state.inCombat()) {
            // 中文：非战斗状态不绘制 HUD，避免普通生存玩法中出现杀戮尖塔 UI。
            // English: Outside combat, the overlay draws nothing so normal survival gameplay remains clean.
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int x = 10;
        int y = 10;

        drawBar(graphics, font, x, y, 110, 0xAA2D0B0B, 0xAAE74C3C, "HP", state.currentHp(), state.maxHp());
        drawBar(graphics, font, x, y + 16, 110, 0xAA0A3045, 0xAA3BB8FF, "EN", state.energy(), state.maxEnergy());

        graphics.fill(x, y + 34, x + 110, y + 46, 0xAA2A2A2A);
        graphics.drawString(font, "T" + state.turn() + " BLK " + state.block() + " STR " + state.strength(), x + 4, y + 37, 0xFFFFFF, false);
    }

    // 中文：绘制带文字的水平数值条。
    // English: Draws a horizontal labeled value bar.
    private static void drawBar(GuiGraphics graphics, Font font, int x, int y, int width, int background, int fillColor, String label, float value, float max) {
        // 中文：max 至少为 1，避免玩家最大生命或能量异常时出现除零。
        // English: max is clamped to at least 1 to avoid division by zero if health or energy becomes invalid.
        float clampedMax = Math.max(1.0F, max);
        float ratio = Math.max(0.0F, Math.min(1.0F, value / clampedMax));
        int filledWidth = Math.max(0, Math.min(width, Math.round(width * ratio)));

        graphics.fill(x, y, x + width, y + 12, background);
        graphics.fill(x, y, x + filledWidth, y + 12, fillColor);
        graphics.drawString(font, label + " " + Math.round(value) + "/" + Math.round(max), x + 4, y + 2, 0xFFFFFF, false);
    }
}
