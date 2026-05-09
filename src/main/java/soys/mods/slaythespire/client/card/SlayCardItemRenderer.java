package soys.mods.slaythespire.client.card;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardItem;
import soys.mods.slaythespire.card.CardTooltipPreview;

import java.util.List;

/**
 * 中文：杀戮尖塔卡牌的物品渲染器。这里不再使用普通 Minecraft 物品贴图，而是按卡牌定义逐层绘制底板、卡图、边框、标题横幅、费用球、文字和卡背。
 * English: Item renderer for Slay the Spire cards. It no longer relies on a normal Minecraft item sprite; it composes background, art, frame, banner, cost orb, text, and card back from the card definition.
 */
public final class SlayCardItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final SlayCardItemRenderer INSTANCE = new SlayCardItemRenderer(
            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
            Minecraft.getInstance().getEntityModels()
    );

    // 中文：所有布局坐标都以 1024x1024 牌面画布为基准，和当前 1024Portraits/cardui 图集保持一致。
    // English: Every layout coordinate is based on a 1024x1024 card canvas, matching the current 1024Portraits and cardui atlases.
    private static final int CANVAS = 1024;
    // 中文：卡牌在 GUI、手持、地面等场景中都使用满亮度，避免背包或低头时被环境光压暗。
    // English: Cards use full-bright lighting in GUI, hand, and ground contexts so inventory or camera angle lighting cannot dim the card face.
    private static final int CARD_LIGHT = LightTexture.FULL_BRIGHT;
    // 中文：卡背资源来自 512 图集，绘制到 1024 画布背面时需要放大两倍。
    // English: The card back comes from the 512 atlas and is scaled by two when drawn on the 1024 card canvas back face.
    private static final int CARD_BACK_SCALE = 2;
    // 中文：固定法线用于所有牌面四边形，让 RenderType 的顶点格式完整，同时实际亮度由 CARD_LIGHT 控制。
    // English: A fixed normal is used for every card quad so the vertex format is complete, while brightness is controlled by CARD_LIGHT.
    private static final float CARD_NORMAL_X = 0.0F;
    private static final float CARD_NORMAL_Y = 1.0F;
    private static final float CARD_NORMAL_Z = 0.0F;
    // 中文：GUI 中略微放大，让卡牌尽量铺满 16x16 slot，同时保持长宽比例。
    // English: GUI rendering is slightly scaled up so the card fills the 16x16 slot better while preserving aspect ratio.
    private static final float GUI_SLOT_SCALE = 1.20F;
    // 中文：原版字体 lineHeight 约为 9，杀戮尖塔字号先换算到 1024 画布，再除以该高度得到 PoseStack 缩放值。
    // English: Vanilla font lineHeight is about 9; Slay the Spire font sizes are converted to the 1024 canvas and then divided by that height to get PoseStack scale.
    private static final float VANILLA_FONT_HEIGHT = 9.0F;
    private static final float STS_TO_CANVAS = CANVAS / 420.0F;
    private static final float TITLE_FONT_SCALE = 27.0F * STS_TO_CANVAS / VANILLA_FONT_HEIGHT;
    private static final float TYPE_FONT_SCALE = 17.0F * STS_TO_CANVAS / VANILLA_FONT_HEIGHT;
    private static final float BODY_FONT_SCALE = 24.0F * STS_TO_CANVAS / VANILLA_FONT_HEIGHT;
    private static final ResourceLocation STS_EN_REGULAR = stsFont("sts_en_regular");
    private static final ResourceLocation STS_EN_BOLD = stsFont("sts_en_bold");
    private static final ResourceLocation STS_ZHS_REGULAR = stsFont("sts_zhs_regular");
    private static final ResourceLocation STS_ZHS_BOLD = stsFont("sts_zhs_bold");
    private static final ResourceLocation STS_ENERGY = stsFont("sts_energy");
    private static final CardUiAtlasRegion CARD_BACK = CardUiAtlasRegion.REGION_512_CARD_BACK;

    // 中文：创建共享卡牌物品渲染器实例。
    // English: Creates the shared card item renderer instance.
    private SlayCardItemRenderer(net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    // 中文：按卡牌定义渲染物品正面、文字和背面。
    // English: Renders the item front face, text, and back face from the card definition.
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
        // 中文：先处理不同显示上下文的整体缩放、位移和旋转；这一步决定 GUI、手持、第三人称、掉落物中的卡牌姿态。
        // English: The first transform adapts scale, position, and rotation for GUI, first-person hand, third-person hand, and ground display contexts.
        applyDisplayContextAdjustment(displayContext, poseStack);
        // 中文：第三人称手持模型的原点和 GUI 原点不同，需要额外把牌面锚点移回中心附近。
        // English: Third-person hand rendering has a different origin from GUI rendering, so the card anchor is shifted back near the center.
        applyDisplayContextAnchor(displayContext, poseStack);
        // 中文：Minecraft 物品渲染坐标和 2D 卡牌画布方向相反，这里翻转 Y/Z 轴，让后续坐标按卡牌从左上到右下书写。
        // English: Minecraft item render coordinates are opposite to the 2D card canvas; flipping Y/Z lets later coordinates read from top-left to bottom-right.
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, -0.02F);

        // 中文：正面采用从后到前的分层绘制顺序：底板 -> 卡图 -> 图框 -> 标题横幅 -> 标题装饰 -> 费用球 -> 文字。
        // English: The front face is layered back-to-front: background -> portrait -> portrait frame -> title banner -> title decorations -> cost orb -> text.
        drawAtlasRegion(poseStack, bufferSource, spec.cardBackground(), -0.002F, CARD_LIGHT, overlay);
        drawFullTexture(poseStack, bufferSource, spec.art().texture(), toRect(layout.art()), -0.003F, CARD_LIGHT, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.artFrame(), -0.004F, CARD_LIGHT, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleBanner(), -0.005F, CARD_LIGHT, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleDecorationLeft(), 0, layout.titleDecorationOffsetY(), -0.006F, CARD_LIGHT, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleDecorationCenter(), 0, layout.titleDecorationOffsetY(), -0.006F, CARD_LIGHT, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.titleDecorationRight(), 0, layout.titleDecorationOffsetY(), -0.006F, CARD_LIGHT, overlay);
        drawAtlasRegion(poseStack, bufferSource, spec.costOrb(), toRect(layout.cost()), -0.007F, CARD_LIGHT, overlay);

        renderText(definition, stack, layout, poseStack, bufferSource, CARD_LIGHT);
        // 中文：最后绘制背面。backFace=true 会反转顶点顺序，使卡牌从背后观察时显示卡背而不是正面镜像。
        // English: The back face is drawn last. backFace=true reverses vertex order so viewing the item from behind shows the card back instead of a mirrored front.
        drawScaledAtlasRegion(poseStack, bufferSource, CARD_BACK, CARD_BACK_SCALE, 0.018F, CARD_LIGHT, overlay, true);
        poseStack.popPose();
    }

    // 中文：根据 Minecraft 物品显示上下文调整卡牌整体姿态。
    // English: Adjusts the card's overall pose for the Minecraft item display context.
    private static void applyDisplayContextAdjustment(ItemDisplayContext displayContext, PoseStack poseStack) {
        // 中文：这些数值只控制物品模型在不同显示上下文里的摆放，不影响牌面 1024 坐标系本身。
        // English: These values only place the item model for each display context; they do not change the 1024 card layout coordinate system.
        switch (displayContext) {
            case GUI -> scaleAroundCardCenter(poseStack, GUI_SLOT_SCALE);
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.0F, 0.36F, 0.0F);
                scaleAroundCardCenter(poseStack, 0.78F);
            }
            case THIRD_PERSON_RIGHT_HAND,THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(0.45F, 0.50F, 0.48F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-16.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(8.0F));
                poseStack.scale(0.50F, 0.50F, 0.50F);
            }
            case GROUND -> scaleAroundCardCenter(poseStack, 0.72F);
            default -> {
            }
        }
    }

    // 中文：修正特定显示上下文中的卡牌锚点。
    // English: Corrects the card anchor for display contexts that use a different origin.
    private static void applyDisplayContextAnchor(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> poseStack.translate(-0.5F, -0.5F, 0.0F);
            default -> {
            }
        }
    }

    // 中文：围绕牌面中心缩放，避免缩放后卡牌偏离 slot 或手持锚点。
    // English: Scales around the card center so the card stays aligned with slots or hand anchors.
    private static void scaleAroundCardCenter(PoseStack poseStack, float scale) {
        poseStack.translate(0.5F, 0.5F, 0.0F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5F, -0.5F, 0.0F);
    }

    // 中文：在牌面上渲染标题、类型、费用和描述文字。
    // English: Renders title, type, cost, and description text on the card face.
    private static void renderText(CardDefinition definition, ItemStack stack, SlayCardRenderSpec.Layout layout, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        poseStack.pushPose();
        // 中文：文字比贴图再向观察者方向偏一点，避免和横幅、装饰、费用球处在同一深度时发生闪烁。
        // English: Text is nudged toward the viewer so it does not z-fight with banners, decorations, or the cost orb.
        poseStack.translate(0.0F, 0.0F, -0.012F);
        // 中文：前面的贴图坐标已经归一化到 0-1；文字仍按 1024 像素布局书写，所以这里把画布缩放回像素单位。
        // English: Texture quads are normalized to 0-1, but text layout uses 1024 pixel coordinates, so this scale returns the text pass to pixel units.
        poseStack.scale(1.0F / CANVAS, 1.0F / CANVAS, 1.0F);

        // 中文：文字顺序对应杀戮尖塔卡牌结构：标题、类型、费用、描述。
        // English: Text order mirrors a Slay the Spire card: title, type label, energy cost, and description.
        drawCentered(font, poseStack, bufferSource, definition.displayName(), toRect(layout.title()), titleStyle(definition.displayName()), light);
        Component type = typeText(definition);
        drawCentered(font, poseStack, bufferSource, type, toRect(layout.type()), typeStyle(type), light);
        drawCentered(font, poseStack, bufferSource, Component.literal(CardTooltipPreview.displayCost(definition, stack)), offset(toRect(layout.cost()), -1, 29), energyStyle(), light);
        drawWrapped(font, poseStack, bufferSource, definition.description(), toRect(layout.body()), bodyStyle(definition.description()), light);

        poseStack.popPose();
    }

    // 中文：返回卡牌类型的本地化牌面文本。
    // English: Returns the localized card-face text for the card type.
    private static Component typeText(CardDefinition definition) {
        // 中文：卡牌类型显示走语言键，避免 tooltip 已国际化但牌面仍显示英文枚举名。
        // English: Card type labels use language keys so the card face does not fall back to raw enum names while tooltips are localized.
        return switch (definition.type()) {
            case ATTACK -> Component.translatable("card.slaythespire.type.attack");
            case SKILL -> Component.translatable("card.slaythespire.type.skill");
            case POWER -> Component.translatable("card.slaythespire.type.power");
            case STATUS -> Component.translatable("card.slaythespire.type.status");
        };
    }

    // 中文：把渲染规格中的 Bounds 转换为本渲染器内部使用的 Rect。
    // English: Converts render-spec Bounds into this renderer's internal Rect.
    private static Rect toRect(SlayCardRenderSpec.Bounds bounds) {
        return new Rect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
    }

    // 中文：按 atlas 原始偏移绘制图集区域。
    // English: Draws an atlas region using its original atlas offsets.
    private static void drawAtlasRegion(PoseStack poseStack, MultiBufferSource bufferSource, CardUiAtlasRegion region, float z, int light, int overlay) {
        drawAtlasRegion(poseStack, bufferSource, region, 0, 0, z, light, overlay);
    }

    // 中文：把图集区域拉伸绘制到指定布局矩形。
    // English: Stretches an atlas region into a specified layout rectangle.
    private static void drawAtlasRegion(PoseStack poseStack, MultiBufferSource bufferSource, CardUiAtlasRegion region, Rect rect, float z, int light, int overlay) {
        if (region.rotated()) {
            throw new IllegalArgumentException("Rotated atlas region is not supported: " + region.atlasName());
        }

        // 中文：这个重载用于把某个图集区域强制拉伸到布局矩形，典型用途是费用球和卡图框指定位置。
        // English: This overload stretches an atlas region into a layout rectangle, commonly used for the cost orb and fixed card art slots.
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

    // 中文：按 atlas 原始偏移加额外偏移绘制图集区域。
    // English: Draws an atlas region with its original offsets plus an extra offset.
    private static void drawAtlasRegion(PoseStack poseStack, MultiBufferSource bufferSource, CardUiAtlasRegion region, int offsetX, int offsetY, float z, int light, int overlay) {
        if (region.rotated()) {
            throw new IllegalArgumentException("Rotated atlas region is not supported: " + region.atlasName());
        }

        // 中文：Spine/TexturePacker 的 region 保存了原始画布尺寸与偏移，这里把裁剪区域还原回原始 1024 卡牌坐标。
        // English: The TexturePacker region stores original canvas size and offsets; this restores the trimmed region back into the original 1024 card coordinates.
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

    // 中文：按整数倍率放大图集区域，并可选择按背面顶点顺序绘制。
    // English: Draws an atlas region at an integer scale and can use back-face vertex order.
    private static void drawScaledAtlasRegion(PoseStack poseStack, MultiBufferSource bufferSource, CardUiAtlasRegion region, int scale, float z, int light, int overlay, boolean backFace) {
        if (region.rotated()) {
            throw new IllegalArgumentException("Rotated atlas region is not supported: " + region.atlasName());
        }

        // 中文：卡背来自 512 region，scale 会同时放大原始偏移和区域尺寸，从而精确贴到 1024 背面。
        // English: The 512 card back uses scale for both original offsets and region size, placing it accurately on the 1024 back face.
        Rect rect = new Rect(
                region.offsetX() * scale,
                (region.originalHeight() - region.offsetY() - region.height()) * scale,
                region.width() * scale,
                region.height() * scale
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
                overlay,
                backFace
        );
    }

    // 中文：绘制整张卡图纹理到指定牌面矩形。
    // English: Draws a full portrait texture into the specified card-face rectangle.
    private static void drawFullTexture(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, Rect rect, float z, int light, int overlay) {
        // 中文：卡图文件本身是完整图片，不来自 cardui 图集，所以 UV 使用 0-1 的整张贴图范围。
        // English: Card portraits are full textures rather than cardui atlas regions, so their UV range is the entire 0-1 image.
        drawTexturedQuad(poseStack, bufferSource, texture, 1, 1, 0, 0, 1, 1, rect, z, light, overlay);
    }

    // 中文：绘制普通正面贴图四边形。
    // English: Draws a normal front-facing textured quad.
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
        drawTexturedQuad(poseStack, bufferSource, texture, textureWidth, textureHeight, sourceX, sourceY, sourceWidth, sourceHeight, rect, z, light, overlay, false);
    }

    // 中文：绘制最终贴图四边形，可控制是否使用背面顶点顺序。
    // English: Draws the final textured quad, optionally using back-face vertex order.
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
            int overlay,
            boolean backFace
    ) {
        // 中文：所有贴图最终都落到这个四边形函数，rect 使用 1024 画布坐标，UV 使用源图集像素坐标。
        // English: Every texture pass ends here. rect uses 1024 canvas coordinates, while UVs use source atlas pixel coordinates.
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(texture));
        Matrix4f pose = poseStack.last().pose();

        float x0 = rect.x() / (float) CANVAS;
        float y0 = rect.y() / (float) CANVAS;
        float x1 = (rect.x() + rect.width()) / (float) CANVAS;
        float y1 = (rect.y() + rect.height()) / (float) CANVAS;
        float u0 = sourceX / (float) textureWidth;
        float v0 = sourceY / (float) textureHeight;
        float u1 = (sourceX + sourceWidth) / (float) textureWidth;
        float v1 = (sourceY + sourceHeight) / (float) textureHeight;
        int packedOverlay = overlay == 0 ? OverlayTexture.NO_OVERLAY : overlay;

        if (backFace) {
            // 中文：背面顶点顺序和正面相反，配合 entityTranslucentCull 的面剔除显示正确卡背。
            // English: Back-face vertices use the reverse order so entityTranslucentCull culls the correct side and shows the card back.
            consumer.vertex(pose, x0, y1, z).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
            consumer.vertex(pose, x0, y0, z).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
            consumer.vertex(pose, x1, y0, z).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
            consumer.vertex(pose, x1, y1, z).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
            return;
        }

        consumer.vertex(pose, x0, y1, z).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
        consumer.vertex(pose, x1, y1, z).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
        consumer.vertex(pose, x1, y0, z).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
        consumer.vertex(pose, x0, y0, z).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(packedOverlay).uv2(light).normal(CARD_NORMAL_X, CARD_NORMAL_Y, CARD_NORMAL_Z).endVertex();
    }

    // 中文：在矩形内居中绘制单行文字，并在过宽时自动缩小。
    // English: Draws one centered text line inside a rectangle and shrinks it when too wide.
    private static void drawCentered(Font font, PoseStack poseStack, MultiBufferSource bufferSource, Component text, Rect rect, CardTextStyle style, int light) {
        FormattedCharSequence sequence = style.apply(text).getVisualOrderText();
        // 中文：标题、类型、费用都按矩形居中；当翻译文本过长时会自动缩小到矩形宽度内。
        // English: Title, type, and cost are centered in their rectangles; long localized text is automatically scaled down to fit width.
        float scale = fitScale(font, sequence, rect.width(), style.scale());
        CardTextStyle fittedStyle = style.withScale(scale);
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);
        float width = font.width(sequence) * scale;
        float height = font.lineHeight * scale;
        float x = (rect.x() + Math.max(0.0F, (rect.width() - width) / 2.0F)) / scale;
        float y = (rect.y() + Math.max(0.0F, (rect.height() - height) / 2.0F)) / scale;
        drawStyledText(font, poseStack, bufferSource, sequence, x, y, fittedStyle, light);
        poseStack.popPose();
    }

    // 中文：在描述框内自动换行并垂直居中绘制文字。
    // English: Wraps text inside the description box and vertically centers the resulting lines.
    private static void drawWrapped(Font font, PoseStack poseStack, MultiBufferSource bufferSource, Component text, Rect rect, CardTextStyle style, int light) {
        float scale = style.scale();
        Component styledText = style.apply(text);
        // 中文：描述文字先按当前字号换算自动换行，再把整块文本垂直居中到描述框。
        // English: Body text is wrapped at the current font scale and then vertically centered in the description box.
        int wrapWidth = Math.max(1, Math.round(rect.width() / scale));
        List<FormattedCharSequence> lines = font.split(styledText, wrapWidth);
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
            drawStyledText(font, poseStack, bufferSource, line, x, y, style, light);
        }
        poseStack.popPose();
    }

    // 中文：按照字体、描边、阴影和颜色配置绘制一段文字。
    // English: Draws one text sequence with configured font, outline, shadow, and color.
    private static void drawStyledText(
            Font font,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            FormattedCharSequence sequence,
            float x,
            float y,
            CardTextStyle style,
            int light
    ) {
        Matrix4f pose = poseStack.last().pose();
        if (style.shadowColor() != 0 && (style.shadowX() != 0.0F || style.shadowY() != 0.0F)) {
            // 中文：描述文字保留轻微阴影，接近杀戮尖塔正文的可读性处理。
            // English: Body text keeps a light shadow, matching the readability treatment used by Slay the Spire body text.
            font.drawInBatch(
                    sequence,
                    x + style.shadowX() / style.scale(),
                    y + style.shadowY() / style.scale(),
                    style.shadowColor(),
                    false,
                    pose,
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    light
            );
        }

        if (style.outline() > 0.0F) {
            // 中文：标题描边通过八方向重复绘制实现；目前描边很薄，避免中文标题糊成黑块。
            // English: Title outline is simulated by drawing in eight directions; it stays thin so Chinese titles do not become dark blobs.
            float outline = style.outline() / style.scale();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        font.drawInBatch(
                                sequence,
                                x + dx * outline,
                                y + dy * outline,
                                style.outlineColor(),
                                false,
                                pose,
                                bufferSource,
                                Font.DisplayMode.NORMAL,
                                0,
                                light
                        );
                    }
                }
            }
        }

        font.drawInBatch(sequence, x, y, style.color(), false, pose, bufferSource, Font.DisplayMode.NORMAL, 0, light);
    }

    // 中文：计算让文字不超过最大宽度的字号缩放。
    // English: Computes a font scale that keeps the text within the maximum width.
    private static float fitScale(Font font, FormattedCharSequence sequence, int maxWidth, float preferredScale) {
        int width = font.width(sequence);
        if (width <= 0) {
            return preferredScale;
        }
        return Math.min(preferredScale, maxWidth / (float) width);
    }

    // 中文：创建卡牌标题文字样式。
    // English: Creates the card-title text style.
    private static CardTextStyle titleStyle(Component text) {
        // 中文：标题按文本内容选择中英字体，白色正文配极细灰色描边。
        // English: The title chooses Chinese or English font by content and uses white fill with a very thin gray outline.
        return new CardTextStyle(
                regularFont(text),
                TITLE_FONT_SCALE,
                0xFFFFFFFF,
                0.1F,
                0xFF747474,
                0.0F,
                0.0F,
                0
        );
    }

    // 中文：创建卡牌类型标签文字样式。
    // English: Creates the card-type label text style.
    private static CardTextStyle typeStyle(Component text) {
        // 中文：类型文字使用粗体字体，颜色贴近原作卡图装饰槽位上的灰色文字。
        // English: Type labels use the bold font and a gray tone close to the original game's type plate text.
        return new CardTextStyle(boldFont(text), TYPE_FONT_SCALE, 0x595959, 0.0F, 0, 0.0F, 0.0F, 0);
    }

    // 中文：创建卡牌正文描述文字样式。
    // English: Creates the card body-description text style.
    private static CardTextStyle bodyStyle(Component text) {
        // 中文：正文使用暖白色和弱阴影，保持在深色描述框上可读。
        // English: Body text uses warm white with a subtle shadow so it remains readable on the dark description panel.
        return new CardTextStyle(
                regularFont(text),
                BODY_FONT_SCALE,
                0xF5EBD2,
                0.0F,
                0,
                1.0F * STS_TO_CANVAS,
                1.0F * STS_TO_CANVAS,
                0x55000000
        );
    }

    @SuppressWarnings("unused")
    // 中文：创建左上角费用数字文字样式。
    // English: Creates the top-left energy-cost number text style.
    private static CardTextStyle energyStyle() {
        // 中文：费用数字使用单独的能量字体，位置由 layout.cost 再加 offset 微调到费用球中心。
        // English: Cost numbers use a dedicated energy font; layout.cost plus offset places the glyph in the orb center.
        return new CardTextStyle(STS_ENERGY, 38.0F * STS_TO_CANVAS / VANILLA_FONT_HEIGHT, 0xFFFFFFFF, 0.0F, 0xFF686868, 0.0F, 0.0F, 0);
    }

    // 中文：按文本内容选择常规中英字体。
    // English: Selects the regular Chinese or English font based on text content.
    private static ResourceLocation regularFont(Component text) {
        // 中文：只要包含中日韩字符就切换到中文字体，否则使用英文原作字体。
        // English: Any CJK character switches to the Chinese font; otherwise the renderer uses the English original-style font.
        return containsCjk(text.getString()) ? STS_ZHS_REGULAR : STS_EN_REGULAR;
    }

    // 中文：按文本内容选择粗体中英字体。
    // English: Selects the bold Chinese or English font based on text content.
    private static ResourceLocation boldFont(Component text) {
        return containsCjk(text.getString()) ? STS_ZHS_BOLD : STS_EN_BOLD;
    }

    // 中文：判断文本中是否包含中日韩字符。
    // English: Checks whether the text contains CJK characters.
    private static boolean containsCjk(String text) {
        return text.codePoints().anyMatch(codePoint -> {
            Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
            return script == Character.UnicodeScript.HAN
                    || script == Character.UnicodeScript.HIRAGANA
                    || script == Character.UnicodeScript.KATAKANA
                    || script == Character.UnicodeScript.HANGUL;
        });
    }

    // 中文：创建本模组字体资源位置。
    // English: Creates a font resource location in this mod namespace.
    private static ResourceLocation stsFont(String path) {
        return ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, path);
    }

    // 中文：返回偏移后的矩形副本。
    // English: Returns a shifted copy of the rectangle.
    private static Rect offset(Rect rect, int x, int y) {
        return new Rect(rect.x() + x, rect.y() + y, rect.width(), rect.height());
    }

    // 中文：牌面 1024 坐标系中的矩形。
    // English: Rectangle in the 1024 card-face coordinate system.
    private record Rect(int x, int y, int width, int height) {
    }

    private record CardTextStyle(
            ResourceLocation font,
            float scale,
            int color,
            float outline,
            int outlineColor,
            float shadowX,
            float shadowY,
            int shadowColor
    ) {
        // 中文：把该文字样式的字体应用到组件副本上。
        // English: Applies this text style's font to a copy of the component.
        private Component apply(Component text) {
            return text.copy().withStyle(style -> style.withFont(font));
        }

        // 中文：返回只替换缩放值的新文字样式。
        // English: Returns a new text style with only the scale replaced.
        private CardTextStyle withScale(float scale) {
            return new CardTextStyle(font, scale, color, outline, outlineColor, shadowX, shadowY, shadowColor);
        }
    }
}
