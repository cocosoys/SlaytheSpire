package soys.mods.slaythespire.client.ironclad;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.resources.ResourceLocation;
import soys.mods.slaythespire.Slaythespire;

/**
 * 中文：铁甲战士玩家外观层定义。模型比例刻意比原版玩家更宽更厚，用原生 HumanoidModel 管线完成外观替换感。
 * English: Ironclad player appearance layer definition. The model is intentionally wider and thicker than vanilla while using the native HumanoidModel pipeline.
 */
public final class IroncladPlayerLayers {
    public static final ModelLayerLocation IRONCLAD = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "ironclad_player"),
            "main"
    );

    private IroncladPlayerLayers() {
    }

    /**
     * 中文：创建客户端模型层。所有部件名保持 HumanoidModel 规范，便于复制玩家姿态动画。
     * English: Creates the client model layer. Part names follow HumanoidModel conventions so player pose animation can be copied.
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F),
                PartPose.offset(0.0F, -1.0F, 0.0F));
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(20, 16).addBox(-5.5F, -1.0F, -3.0F, 11.0F, 13.0F, 6.0F)
                        .texOffs(20, 10).addBox(-6.0F, -2.0F, -3.5F, 12.0F, 4.0F, 7.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(46, 16).addBox(-4.5F, -2.0F, -2.5F, 5.0F, 14.0F, 5.0F),
                PartPose.offset(-6.0F, -1.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(46, 16).mirror().addBox(-0.5F, -2.0F, -2.5F, 5.0F, 14.0F, 5.0F),
                PartPose.offset(6.0F, -1.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 40).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 13.0F, 5.0F),
                PartPose.offset(-2.0F, 11.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(0, 40).mirror().addBox(-2.5F, 0.0F, -2.5F, 5.0F, 13.0F, 5.0F),
                PartPose.offset(2.0F, 11.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
