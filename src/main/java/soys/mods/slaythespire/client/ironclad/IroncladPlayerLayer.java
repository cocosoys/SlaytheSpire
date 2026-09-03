package soys.mods.slaythespire.client.ironclad;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.equipment.IroncladSet;

/**
 * 中文：铁甲战士四件套的客户端玩家渲染层。它只读装备槽并绘制外观，不取消原版玩家渲染。
 * English: Client player render layer for the Ironclad four-piece set. It only reads armor slots and draws appearance without canceling vanilla rendering.
 */
public class IroncladPlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Slaythespire.MODID,
            "textures/entity/ironclad/ironclad_layer.png"
    );

    private final HumanoidModel<AbstractClientPlayer> model;

    public IroncladPlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, ModelPart modelPart) {
        super(parent);
        this.model = new HumanoidModel<>(modelPart);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!IroncladSet.isWearingFullSet(player)) {
            return;
        }
        getParentModel().copyPropertiesTo(model);
        model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.96F);
    }
}
