package soys.mods.slaythespire.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import soys.mods.slaythespire.Slaythespire;

/**
 * 中文：客户端按键绑定注册。当前提供回合结束快捷键。
 * English: Client key mapping registration. Currently provides an end-turn hotkey.
 */
@Mod.EventBusSubscriber(modid = Slaythespire.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class KeyBindings {

    // 中文：回合结束按键，默认 R 键。
    // English: End-turn key, defaults to R.
    public static final KeyMapping END_TURN = new KeyMapping(
            "key.slaythespire.end_turn",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.slaythespire"
    );

    private KeyBindings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(END_TURN);
    }
}
