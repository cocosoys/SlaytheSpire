package soys.mods.slaythespire.collectible;

import net.minecraft.resources.ResourceLocation;

/**
 * 中文：单个遗物或药水收藏品的静态定义。所有字段都在开发期生成，运行时不会扫描资源目录。
 * English: Static definition for one relic or potion collectible. Entries are generated at dev time, so runtime never scans resource folders.
 *
 * @param id          中文：注册到 Forge 的物品 id。 English: Item id registered into Forge.
 * @param kind        中文：收藏品来源类型。 English: Collectible source category.
 * @param texturePath 中文：物品模型引用的贴图路径。 English: Texture path referenced by the generated item model.
 */
public record CollectibleDefinition(ResourceLocation id, CollectibleKind kind, ResourceLocation texturePath) {
}
