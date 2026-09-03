package soys.mods.slaythespire.client.collectible;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import soys.mods.slaythespire.collectible.CollectibleDefinition;
import soys.mods.slaythespire.collectible.CollectibleItem;

/**
 * 中文：遗物/药水收藏品的纯图片渲染器。它以 128x128 为画布绘制完整 PNG，避免普通物品模型把图标压成 16x16 精灵。
 * English: Pure image renderer for relic and potion collectibles. It draws the complete PNG on a 128x128 canvas instead of compressing it into a 16x16 item sprite.
 */
public final class CollectibleItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final CollectibleItemRenderer INSTANCE = new CollectibleItemRenderer(
            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
            Minecraft.getInstance().getEntityModels()
    );

    private static final int CANVAS = 128;
    private static final int COLLECTIBLE_LIGHT = LightTexture.FULL_BRIGHT;
    private static final float NORMAL_X = 0.0F;
    private static final float NORMAL_Y = 1.0F;
    private static final float NORMAL_Z = 0.0F;

    private CollectibleItemRenderer(net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (!(stack.getItem() instanceof CollectibleItem collectibleItem)) {
            return;
        }

        CollectibleDefinition definition = collectibleItem.definition();
        poseStack.pushPose();
        applyDisplayContextAdjustment(displayContext, poseStack);
        // 中文：保持和卡牌渲染器相同的二维坐标习惯：后续四边形按左上到右下写入。
        // English: Uses the same 2D coordinate convention as the card renderer: later quads are written from top-left to bottom-right.
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, -0.02F);
        poseStack.scale(1.0F / CANVAS, 1.0F / CANVAS, 1.0F);

        ResourceLocation texture = toTextureLocation(definition.texturePath());
        drawFullImage(poseStack, bufferSource, texture, 0.0F, COLLECTIBLE_LIGHT, overlay, false);
        drawFullImage(poseStack, bufferSource, texture, 0.018F, COLLECTIBLE_LIGHT, overlay, true);
        poseStack.popPose();
    }

    private static void applyDisplayContextAdjustment(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case GUI -> scaleAroundCenter(poseStack, 1.18F);
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.0F, 0.20F, 0.0F);
                scaleAroundCenter(poseStack, 0.68F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.34F, 0.42F, 0.42F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-12.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(6.0F));
                poseStack.scale(0.42F, 0.42F, 0.42F);
                poseStack.translate(-0.5F, -0.5F, 0.0F);
            }
            case GROUND -> scaleAroundCenter(poseStack, 0.58F);
            case FIXED -> scaleAroundCenter(poseStack, 0.82F);
            default -> {
            }
        }
    }

    private static void scaleAroundCenter(PoseStack poseStack, float scale) {
        poseStack.translate(0.5F, 0.5F, 0.0F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5F, -0.5F, 0.0F);
    }

    private static ResourceLocation toTextureLocation(ResourceLocation texturePath) {
        return ResourceLocation.fromNamespaceAndPath(texturePath.getNamespace(), "textures/" + texturePath.getPath() + ".png");
    }

    private static void drawFullImage(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, float z, int light, int overlay, boolean backFace) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(texture));
        Matrix4f pose = poseStack.last().pose();
        int packedOverlay = overlay == 0 ? OverlayTexture.NO_OVERLAY : overlay;

        if (backFace) {
            consumer.vertex(pose, 0.0F, CANVAS, z).color(255, 255, 255, 255).uv(0.0F, 1.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
            consumer.vertex(pose, 0.0F, 0.0F, z).color(255, 255, 255, 255).uv(0.0F, 0.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
            consumer.vertex(pose, CANVAS, 0.0F, z).color(255, 255, 255, 255).uv(1.0F, 0.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
            consumer.vertex(pose, CANVAS, CANVAS, z).color(255, 255, 255, 255).uv(1.0F, 1.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
            return;
        }

        consumer.vertex(pose, 0.0F, CANVAS, z).color(255, 255, 255, 255).uv(0.0F, 1.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
        consumer.vertex(pose, CANVAS, CANVAS, z).color(255, 255, 255, 255).uv(1.0F, 1.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
        consumer.vertex(pose, CANVAS, 0.0F, z).color(255, 255, 255, 255).uv(1.0F, 0.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
        consumer.vertex(pose, 0.0F, 0.0F, z).color(255, 255, 255, 255).uv(0.0F, 0.0F).overlayCoords(packedOverlay).uv2(light).normal(NORMAL_X, NORMAL_Y, NORMAL_Z).endVertex();
    }
}
