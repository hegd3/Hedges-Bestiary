package com.hedge.hedges_bestiary.client.models;


import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.animations.GawkAnimation;
import com.hedge.hedges_bestiary.entity.living.GawkEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class GawkModel extends HBModel<GawkEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.GAWK_LAYER;
	private final ModelPart root;
	private final ModelPart swimcontrol;
	private final ModelPart bodyfins;
	private final ModelPart body;
	private final ModelPart jaw;
	private final ModelPart tail;
	private final ModelPart leftfin;
	private final ModelPart rightfin;
	private final ModelPart leftfoot;
	private final ModelPart rightfoot;

	public GawkModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.swimcontrol = this.root.getChild("swimcontrol");
		this.bodyfins = this.swimcontrol.getChild("bodyfins");
		this.body = this.bodyfins.getChild("body");
		this.jaw = this.body.getChild("jaw");
		this.tail = this.body.getChild("tail");
		this.leftfin = this.bodyfins.getChild("leftfin");
		this.rightfin = this.bodyfins.getChild("rightfin");
		this.leftfoot = this.swimcontrol.getChild("leftfoot");
		this.rightfoot = this.swimcontrol.getChild("rightfoot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 16.5F, 0.0F));

		PartDefinition swimcontrol = root.addOrReplaceChild("swimcontrol", CubeListBuilder.create(), PartPose.offset(0.0F, 7.5F, 0.0F));

		PartDefinition bodyfins = swimcontrol.addOrReplaceChild("bodyfins", CubeListBuilder.create(), PartPose.offset(0.5F, 0.0F, 3.0F));

		PartDefinition body = bodyfins.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -11.0F, -12.0F, 15.0F, 11.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(0, 29).addBox(-8.0F, -15.0F, -8.0F, 15.0F, 4.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 47).addBox(-0.5F, -19.0F, -8.0F, 0.0F, 4.0F, 14.0F, new CubeDeformation(0.1F))
		.texOffs(60, 55).addBox(-4.0F, -11.0F, -19.0F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(66, 0).addBox(-4.0F, -11.0F, -23.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition teeth_r1 = body.addOrReplaceChild("teeth_r1", CubeListBuilder.create().texOffs(9, 94).addBox(-3.5F, -2.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.5F, -11.0F, -23.0F, 0.7418F, 0.0F, 0.0F));

		PartDefinition teeth_r2 = body.addOrReplaceChild("teeth_r2", CubeListBuilder.create().texOffs(26, 75).mirror().addBox(0.0F, -2.0F, -4.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-4.0F, -11.0F, -19.0F, 0.0F, 0.0F, -0.7418F));

		PartDefinition teeth_r3 = body.addOrReplaceChild("teeth_r3", CubeListBuilder.create().texOffs(26, 75).addBox(0.0F, -2.0F, -4.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(3.0F, -11.0F, -19.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition jaw = body.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(28, 47).addBox(-7.5F, -4.0F, -4.0F, 15.0F, 4.0F, 4.0F, new CubeDeformation(0.01F))
		.texOffs(58, 29).addBox(-3.5F, -4.0F, -11.0F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.01F))
		.texOffs(60, 65).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 7.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(-0.5F, -11.0F, -8.0F));

		PartDefinition teeth_r4 = jaw.addOrReplaceChild("teeth_r4", CubeListBuilder.create().texOffs(66, 15).mirror().addBox(0.0F, 0.0F, -4.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-3.5F, 0.0F, -11.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition teeth_r5 = jaw.addOrReplaceChild("teeth_r5", CubeListBuilder.create().texOffs(66, 15).addBox(0.0F, 0.0F, -4.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(3.5F, 0.0F, -11.0F, 0.0F, 0.0F, -0.7418F));

		PartDefinition teeth_r6 = jaw.addOrReplaceChild("teeth_r6", CubeListBuilder.create().texOffs(9, 100).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, -15.0F, -0.7418F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 65).addBox(-3.5F, -2.0F, 0.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.01F))
		.texOffs(42, 66).addBox(0.0F, -5.0F, 1.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offset(-0.5F, -2.0F, 6.0F));

		PartDefinition leftfin = bodyfins.addOrReplaceChild("leftfin", CubeListBuilder.create().texOffs(28, 55).addBox(0.0F, -1.0F, -4.5F, 7.0F, 2.0F, 9.0F, new CubeDeformation(0.01F))
		.texOffs(85, 53).addBox(7.0F, -1.0F, -4.5F, 2.0F, 2.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offset(7.0F, -1.0F, -9.5F));

		PartDefinition rightfin = bodyfins.addOrReplaceChild("rightfin", CubeListBuilder.create().texOffs(28, 55).mirror().addBox(-7.0F, -1.0F, -4.5F, 7.0F, 2.0F, 9.0F, new CubeDeformation(0.01F)).mirror(false)
		.texOffs(85, 53).mirror().addBox(-9.0F, -1.0F, -4.5F, 2.0F, 2.0F, 12.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-8.0F, -1.0F, -9.5F));

		PartDefinition leftfoot = swimcontrol.addOrReplaceChild("leftfoot", CubeListBuilder.create().texOffs(66, 9).addBox(0.0F, 0.0F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.01F))
		.texOffs(42, 98).addBox(5.0F, 0.0F, -2.5F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(7.5F, -1.0F, 4.5F));

		PartDefinition rightfoot = swimcontrol.addOrReplaceChild("rightfoot", CubeListBuilder.create().texOffs(66, 9).mirror().addBox(-5.0F, 0.0F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.01F)).mirror(false)
		.texOffs(42, 98).mirror().addBox(-6.0F, 0.0F, -2.5F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-7.5F, -1.0F, 4.5F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(GawkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float v3, float v4) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		if (entity.onGround() || entity.isInWater()) {
			this.animateWalk(GawkAnimation.WALK, limbSwing, limbSwingAmount * (entity.landProgress/5), 2.5F, 2.5F);
			this.animateWalk(GawkAnimation.SWIM, limbSwing, limbSwingAmount * (1 - entity.landProgress/5), 2.5F, 2.5F);

		}
		this.animateSmooth(entity.idleAnimationState, GawkAnimation.IDLE, ageInTicks, partialTicks, 0.5F);
		this.animateSmooth(entity.swimIdleAnimationState, GawkAnimation.SWIM_IDLE, ageInTicks, partialTicks, 0.5F);
		this.animateSmooth(entity.airAnimationState, GawkAnimation.AIR, ageInTicks, partialTicks, 2F);
		this.animateSmooth(entity.danceAnimationState, GawkAnimation.DANCE, ageInTicks, partialTicks, 1F);
		this.animateSmooth(entity.sitAnimationState, GawkAnimation.SIT_LEFT, ageInTicks, partialTicks, 1F);
		this.animateSmooth(entity.sitAnimationState, GawkAnimation.SIT_MOVEMENT, ageInTicks, partialTicks, 0.5F);
		this.animateSmooth(entity.yawnAnimationState, GawkAnimation.YAWN, ageInTicks, partialTicks, 1F);
		this.animateSmooth(entity.staticYawnAnimationState, GawkAnimation.STATIC_YAWN, ageInTicks, partialTicks, 1F);

		this.animate(entity.spinAnimationState, entity.swingingLeft() ? GawkAnimation.SPIN_LEFT : GawkAnimation.SPIN_RIGHT, ageInTicks);

		this.jaw.xRot = this.jaw.xRot - (5 * Mth.DEG_TO_RAD + Mth.cos(ageInTicks * (0.5F + limbSwingAmount/10)) * Mth.DEG_TO_RAD);
		if (entity.isInWater() || !entity.onGround()) {
			this.swimcontrol.zRot = entity.roll;
			this.swimcontrol.xRot = entity.getPitch(partialTicks) * Mth.DEG_TO_RAD;
		}
	}
}