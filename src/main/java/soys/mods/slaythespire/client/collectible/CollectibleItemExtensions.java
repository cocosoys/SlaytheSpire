package soys.mods.slaythespire.client.collectible;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * 中文：收藏物品的客户端扩展。它把普通遗物/药水收藏品接到 128x128 完整图片渲染器。
 * English: Client extension for collectibles. It connects relic and potion collectibles to the 128x128 full-image renderer.
 */
public final class CollectibleItemExtensions implements IClientItemExtensions {
    public static final CollectibleItemExtensions INSTANCE = new CollectibleItemExtensions();

    private CollectibleItemExtensions() {
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return CollectibleItemRenderer.INSTANCE;
    }
}
