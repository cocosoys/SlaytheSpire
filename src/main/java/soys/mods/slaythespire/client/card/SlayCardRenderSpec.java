package soys.mods.slaythespire.client.card;

import net.minecraft.resources.ResourceLocation;

public record SlayCardRenderSpec(
        ResourceLocation cardId,
        ResourceLocation backgroundTexture,
        ResourceLocation artTexture,
        ResourceLocation orbTexture
) {
}
