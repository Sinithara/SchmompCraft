package de.schmompcraft.schmomp.client.model;

import de.schmompcraft.schmomp.SchmompMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import de.schmompcraft.schmomp.client.render.SchmompRenderState;

public class SchmompModel extends EntityModel<SchmompRenderState>{
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightAntenna;
    private final ModelPart leftAntenna;
    private final ModelPart butt;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public SchmompModel(ModelPart root){
        super(root);
        this.body = root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightAntenna = this.body.getChild("right_antenna");
        this.leftAntenna = this.body.getChild("left_antenna");
        this.butt = this.body.getChild("butt");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer(){
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -10, -5, 10, 10, 10), PartPose.offset(0, 22, 0));
        body.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(40, 0).addBox(-3.5f, -7, -5.5f, 2, 2, 1), PartPose.ZERO);
        body.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(40, 0).addBox(1.5f, -7, -5.5f, 2, 2, 1), PartPose.ZERO);
        body.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(40, 4).addBox(-1, -4, -5.5f, 2, 1, 1), PartPose.ZERO);
        body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(8, 20).addBox(-2, -1, -1, 2, 3, 2), PartPose.offset(-5, -5, 0));
        body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(8, 20).addBox(0, -1, -1, 2, 3, 2), PartPose.offset(5, -5, 0));
        body.addOrReplaceChild("right_antenna", CubeListBuilder.create().texOffs(26, 20).addBox(-1, -2, -1, 2, 2, 2), PartPose.offset(-3, -10, 0));
        body.addOrReplaceChild("left_antenna", CubeListBuilder.create().texOffs(26, 20).addBox(-1, -2, -1, 2, 2, 2), PartPose.offset(3, -10, 0));
        body.addOrReplaceChild("butt", CubeListBuilder.create().texOffs(16, 20).addBox(-3.25f, -1.5f, -1, 3, 3, 2).addBox(0.25f, -1.5f, -1, 3, 3, 2), PartPose.offset(0, -1.5f, 5));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 20).addBox(-1, 0, -1, 2, 2, 2), PartPose.offset(-2.5f, 22, 0));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 20).addBox(-1, 0, -1, 2, 2, 2), PartPose.offset(2.5f, 22, 0));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override public void setupAnim(SchmompRenderState state){
        super.setupAnim(state);
        float phase = state.walkAnimationPos * 0.6662f;
        float speed = state.walkAnimationSpeed;

        float hop = Math.abs(Mth.sin(phase)) * 2.5f * speed;
        this.body.y -= hop;
        this.rightLeg.y -= hop;
        this.leftLeg.y -= hop;

        this.rightLeg.xRot = Mth.cos(phase) * 1.6f * speed;
        this.leftLeg.xRot = Mth.cos(phase + Mth.PI) * 1.6f * speed;

        float wiggle = Mth.sin(state.ageInTicks + 0.15f) * 0.1f;
        this.rightArm.xRot = Mth.cos(phase + Mth.PI) * 1.2f * speed;
        this.leftArm.xRot = Mth.cos(phase) * 1.2f * speed;
        this.rightArm.zRot = 0.3f + wiggle;
        this.leftArm.zRot = -0.3f - wiggle;

        float bounce = Mth.cos(phase * 2.0f) + 0.25f * speed;
        this.rightAntenna.xRot = bounce;
        this.leftAntenna.xRot = bounce;

        this.butt.zRot = Mth.sin(phase) * 0.25f * speed;

        float squish = (Math.abs(Mth.sin(phase)) -0.5f) * 0.25f * speed + Mth.sin(state.ageInTicks * 0.1f) * 0.03f;
        if(state.sitting){
            squish -= 0.3f;
            this.body.y += 2.0f;
        }

        this.body.yScale = 1.0f + squish;
        this.body.xScale = 1.0f - squish * 0.5f;
        this.body.zScale = 1.0f - squish * 0.5f;

        this.body.yRot = state.yRot * Mth.DEG_TO_RAD;
    }

}
