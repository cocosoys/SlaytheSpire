package soys.mods.slaythespire.client.card;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardItem;
import soys.mods.slaythespire.card.CardTooltipPreview;

import java.util.List;

public final class SlayCardItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final SlayCardItemRenderer INSTANCE = new SlayCardItemRenderer(
            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
            Minecraft.getInstance().getEntityModels()
    );

    private static final int CANVAS = 1024;

    private SlayCardItemRenderer(net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (!(stack.getItem() instanceof CardItem cardItem)) {
            return;
        }

        CardDefinition definition = cardItem.definition(stack, null);
        if (definition == null) {
            return;
        }

        SlayCardRenderSpec spec = SlayCardRenderSpecs.require(definition.id());
        SlayCardRenderSpec.Layout layout = spec.layout();

        poseStack.pushPose();
        applyDisplayContextAdjustment(displayContext, poseStack);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, -0.02F);

        drawAtlasRegion(poseStack, bufferSource, spec.cardBackground(), -0.002F, light, overlay);
        drawFullTexture(poseStack, bufferSource, spec.art().texture(), toRect(layout.art()), -0.003F, light, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.artFrame(), -0.004F, light, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleBanner(), -0.005F, light, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleDecorationLeft(), 0, layout.titleDecorationOffsetY(), -0.006F, light, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleDecorationCenter(), 0, layout.titleDecorationOffsetY(), -0.006F, light, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleDecorationRight(), 0, layout.titleDecorationOffsetY(), -0.006F, light, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.costOrb(), toRect(layout.cost()), -0.007F, light, overlay);

        renderText(definition, stack, layout, poseStack, bufferSource, light);
        poseStack.popPose();
    }

    private static void applyDisplayContextAdjustment(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.0F, 0.36F, 0.0F);
                scaleAroundCardCenter(poseStack, 0.78F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> scaleAroundCardCenter(poseStack, 0.82F);
            case GROUND -> scaleAroundCardCenter(poseStack, 0.72F);
            default -> {
            }
        }
    }

    private static void scaleAroundCardCenter(PoseStack poseStack, float scale) {
        poseStack.translate(0.5F, 0.5F, 0.0F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5F, -0.5F, 0.0F);
    }

    private static void renderText(CardDefinition definition, ItemStack stack, SlayCardRenderSpec.Layout layout, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, -0.012F);
        poseStack.scale(1.0F / CANVAS, 1.0F / CANVAS, 1.0F);

        drawCentered(font, poseStack, bufferSource, definition.displayName(), toRect(layout.title()), 6.8F, 0xF5EBD2, light);
        drawCentered(font, poseStack, bufferSource, typeText(definition).copy().withStyle(ChatFormatting.BOLD), toRect(layout.type()), 3.6F, 0x5F5A52, light);
        drawCentered(font, poseStack, bufferSource, Component.literal(CardTooltipPreview.displayCost(definition, stack)), toRect(layout.cost()), 6.2F, 0xF5EBD2, light);
        drawWrapped(font, poseStack, bufferSource, definition.description(), toRect(layout.body()), 5.2F, 0xF5EBD2, light);

        poseStack.popPose();
    }

    private static Component typeText(CardDefinition definition) {
        return switch (definition.type()) {
            case ATTACK -> Component.translatable("card.slaythespire.type.attack");
            case SKILL -> Component.translatable("card.slaythespire.type.skill");
            case POWER -> Component.translatable("card.slaythespire.type.power");
            case STATUS -> Component.translatable("card.slaythespire.type.status");
        };
    }

    private static Rect toRect(SlayCardRenderSpec.Bounds bounds) {
        return new Rect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
    }

    private static void drawAtlasRegion(PoseStack poseStack, MultiBufferSource bufferSource, CardUiAtlasRegion region, float z, int light, int overlay) {
        drawAtlasRegion(poseStack, bufferSource, region, 0, 0, z, light, overlay);
    }

    private static void drawAtlasRegion(PoseStack poseStack, MultiBufferSource bufferSource, CardUiAtlasRegion region, Rect rect, float z, int light, int overlay) {
        if (region.rotated()) {
            throw new IllegalArgumentException("Rotated atlas region is not supported: " + region.atlasName());
        }

        drawTexturedQuad(
                poseStack,
                bufferSource,
                region.texture(),
                region.textureWidth(),
                region.textureHeight(),
                region.x(),
                region.y(),
                region.width(),
                region.height(),
                rect,
                z,
                light,
                overlay
        );
    }

    private static void drawAtlasRegion(PoseStack poseStack, MultiBufferSource bufferSource, CardUiAtlasRegion region, int offsetX, int offsetY, float z, int light, int overlay) {
        if (region.rotated()) {
            throw new IllegalArgumentException("Rotated atlas region is not supported: " + region.atlasName());
        }

        Rect rect = new Rect(
                region.offsetX() + offsetX,
                region.originalHeight() - region.offsetY() - region.height() + offsetY,
                region.width(),
                region.height()
        );
        drawTexturedQuad(
                poseStack,
                bufferSource,
                region.texture(),
                region.textureWidth(),
                region.textureHeight(),
                region.x(),
                region.y(),
                region.width(),
                region.height(),
                rect,
                z,
                light,
                overlay
        );
    }

    private static void drawFullTexture(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, Rect rect, float z, int light, int overlay) {
        drawTexturedQuad(poseStack, bufferSource, texture, 1, 1, 0, 0, 1, 1, rect, z, light, overlay);
    }

    private static void drawTexturedQuad(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            ResourceLocation texture,
            int textureWidth,
            int textureHeight,
            int sourceX,
            int sourceY,
            int sourceWidth,
            int sourceHeight,
            Rect rect,
            float z,
            int light,
            int overlay
    ) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float x0 = rect.x() / (float) CANVAS;
        float y0 = rect.y() / (float) CANVAS;
        float x1 = (rect.x() + rect.width()) / (float) CANVAS;
        float y1 = (rect.y() + rect.height()) / (float) CANVAS;
        float u0 = sourceX / (float) textureWidth;
        float v0 = sourceY / (float) textureHeight;
        float u1 = (sourceX + sourceWidth) / (float) textureWidth;
        float v1 = (sourceY + sourceHeight) / (float) textureHeight;
        int packedOverlay = overlay == 0 ? OverlayTexture.NO_OVERLAY : overlay;

        consumer.vertex(pose, x0, y1, z).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(packedOverlay).uv2(light).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(pose, x1, y1, z).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(packedOverlay).uv2(light).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(pose, x1, y0, z).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(packedOverlay).uv2(light).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(pose, x0, y0, z).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(packedOverlay).uv2(light).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
    }

    private static void drawCentered(Font font, PoseStack poseStack, MultiBufferSource bufferSource, Component text, Rect rect, float scale, int color, int light) {
        FormattedCharSequence sequence = text.getVisualOrderText();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);
        float width = font.width(sequence) * scale;
        float height = font.lineHeight * scale;
        float x = (rect.x() + Math.max(0.0F, (rect.width() - width) / 2.0F)) / scale;
        float y = (rect.y() + Math.max(0.0F, (rect.height() - height) / 2.0F)) / scale;
        font.drawInBatch(sequence, x, y, color, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
        poseStack.popPose();
    }

    private static void drawWrapped(Font font, PoseStack poseStack, MultiBufferSource bufferSource, Component text, Rect rect, float scale, int color, int light) {
        int wrapWidth = Math.max(1, Math.round(rect.width() / scale));
        List<FormattedCharSequence> lines = font.split(text, wrapWidth);
        int lineHeight = font.lineHeight + 2;
        int maxLines = Math.max(1, (int) Math.floor(rect.height() / (lineHeight * scale)));
        int lineCount = Math.min(lines.size(), maxLines);
        float blockHeight = lineCount * lineHeight * scale;
        float firstY = rect.y() + Math.max(0.0F, (rect.height() - blockHeight) / 2.0F);

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);
        for (int index = 0; index < lineCount; index++) {
            FormattedCharSequence line = lines.get(index);
            float width = font.width(line) * scale;
            float x = (rect.x() + Math.max(0.0F, (rect.width() - width) / 2.0F)) / scale;
            float y = (firstY + index * lineHeight * scale) / scale;
            font.drawInBatch(line, x, y, color, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
        }
        poseStack.popPose();
    }

    private record Rect(int x, int y, int width, int height) {
    }
}
