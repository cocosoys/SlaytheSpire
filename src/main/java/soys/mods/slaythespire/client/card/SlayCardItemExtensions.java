package soys.mods.slaythespire.client.card;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * 中文：卡牌物品的客户端扩展。它只负责把 CardItem 接到自定义 BEWLR 渲染器。
 * English: Client extension for card items. It only connects CardItem to the custom BEWLR renderer.
 */
public final class SlayCardItemExtensions implements IClientItemExtensions {
    public static final SlayCardItemExtensions INSTANCE = new SlayCardItemExtensions();

    // 中文：禁止外部创建客户端扩展实例，统一使用 INSTANCE。
    // English: Prevents external extension instances; callers use INSTANCE.
    private SlayCardItemExtensions() {
    }

    @Override
    // 中文：返回卡牌物品专用的自定义渲染器。
    // English: Returns the custom renderer dedicated to card items.
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        // 中文：所有卡牌物品共享同一个无方块实体渲染器实例，避免每张卡重复创建渲染器。
        // English: All card items share one block-entity-without-level renderer instance to avoid per-card renderer allocation.
        return SlayCardItemRenderer.INSTANCE;
    }
}
