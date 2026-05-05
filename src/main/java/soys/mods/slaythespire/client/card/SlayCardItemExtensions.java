package soys.mods.slaythespire.client.card;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class SlayCardItemExtensions implements IClientItemExtensions {
    public static final SlayCardItemExtensions INSTANCE = new SlayCardItemExtensions();

    private SlayCardItemExtensions() {
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return SlayCardItemRenderer.INSTANCE;
    }
}
