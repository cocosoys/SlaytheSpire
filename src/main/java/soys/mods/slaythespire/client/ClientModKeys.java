package soys.mods.slaythespire.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ClientModKeys {
    public static final KeyMapping END_TURN = new KeyMapping(
            "key.slaythespire.end_turn",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "key.categories.slaythespire"
    );

    private ClientModKeys() {
    }
}
