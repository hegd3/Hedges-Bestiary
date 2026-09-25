package com.hedge.hedges_bestiary.client.models;

import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.animations.BlurpumAnimation;
import com.hedge.hedges_bestiary.client.animations.GenericPosesAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;


import com.hedge.hedges_bestiary.entity.living.BlurpumEntity;
import net.minecraft.util.Mth;

public class BlurpumModel extends HBModel<BlurpumEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.BLURPUM_LAYER;
	private final ModelPart root;
	private final ModelPart swimcontrol;
	private final ModelPart wholebody;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart leftleg;
	private final ModelPart leftfoot;
	private final ModelPart rightleg;
	private final ModelPart rightfoot;
	private final ModelPart leftleg2;
	private final ModelPart leftfoot2;
	private final ModelPart rightleg2;
	private final ModelPart rightfoot2;

	public BlurpumModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.swimcontrol = this.root.getChild("swimcontrol");
		this.wholebody = this.swimcontrol.getChild("wholebody");
		this.body = this.wholebody.getChild("body");
		this.head = this.wholebody.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.tail = this.wholebody.getChild("tail");
		this.tail2 = this.tail.getChild("tail2");
		this.leftleg = this.swimcontrol.getChild("leftleg");
		this.leftfoot = this.leftleg.getChild("leftfoot");
		this.rightleg = this.swimcontrol.getChild("rightleg");
		this.rightfoot = this.rightleg.getChild("rightfoot");
		this.leftleg2 = this.swimcontrol.getChild("leftleg2");
		this.leftfoot2 = this.leftleg2.getChild("leftfoot2");
		this.rightleg2 = this.swimcontrol.getChild("rightleg2");
		this.rightfoot2 = this.rightleg2.getChild("rightfoot2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition swimcontrol = root.addOrReplaceChild("swimcontrol", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition wholebody = swimcontrol.addOrReplaceChild("wholebody", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition body = wholebody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -12.0F, -15.0F, 19.0F, 12.0F, 27.0F, new CubeDeformation(0.0F))
		.texOffs(123, 95).addBox(9.5F, -8.0F, -14.0F, 4.0F, 0.0F, 17.0F, new CubeDeformation(0.0F))
		.texOffs(123, 95).mirror().addBox(-13.5F, -8.0F, -14.0F, 4.0F, 0.0F, 17.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = wholebody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 62).addBox(-16.5F, -3.0F, -19.0F, 33.0F, 3.0F, 19.0F, new CubeDeformation(0.01F))
		.texOffs(60, 84).addBox(-7.5F, -4.0F, -15.0F, 15.0F, 1.0F, 15.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -6.0F, -15.0F));

		PartDefinition teeth_r1 = head.addOrReplaceChild("teeth_r1", CubeListBuilder.create().texOffs(104, 46).addBox(-7.5F, 0.0F, 0.0F, 15.0F, 2.0F, 0.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(0.0F, 0.0F, -19.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition teeth_r2 = head.addOrReplaceChild("teeth_r2", CubeListBuilder.create().texOffs(104, 48).mirror().addBox(0.0F, 0.0F, -3.5F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.02F)).mirror(false), PartPose.offsetAndRotation(-16.5F, 0.0F, -12.5F, 0.0F, 0.0F, 0.3054F));

		PartDefinition teeth_r3 = head.addOrReplaceChild("teeth_r3", CubeListBuilder.create().texOffs(104, 48).addBox(0.0F, 0.0F, -3.5F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(16.5F, 0.0F, -12.5F, 0.0F, 0.0F, -0.3054F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 39).addBox(-16.5F, 0.0F, -19.0F, 33.0F, 3.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition teeth_r4 = jaw.addOrReplaceChild("teeth_r4", CubeListBuilder.create().texOffs(104, 57).mirror().addBox(0.0F, -2.0F, -3.5F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)).mirror(false), PartPose.offsetAndRotation(-16.5F, 0.0F, -14.5F, 0.0F, 0.0F, -0.3054F));

		PartDefinition teeth_r5 = jaw.addOrReplaceChild("teeth_r5", CubeListBuilder.create().texOffs(104, 64).mirror().addBox(-3.0F, -2.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.02F)).mirror(false)
		.texOffs(104, 64).addBox(21.0F, -2.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(-12.5F, 0.0F, -19.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition teeth_r6 = jaw.addOrReplaceChild("teeth_r6", CubeListBuilder.create().texOffs(104, 57).addBox(0.0F, -2.0F, -3.5F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(16.5F, 0.0F, -14.5F, 0.0F, 0.0F, 0.3054F));

		PartDefinition tail = wholebody.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 84).addBox(-6.5F, -4.0F, 0.0F, 13.0F, 8.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 12.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(92, 0).addBox(-4.5F, -1.0F, 0.0F, 9.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 17.0F));

		PartDefinition plane_r1 = tail2.addOrReplaceChild("plane_r1", CubeListBuilder.create().texOffs(5, 128).mirror().addBox(0.0F, -5.0F, -8.5F, 0.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.5F, -1.0F, 8.5F, 0.0F, 0.0F, -0.829F));

		PartDefinition plane_r2 = tail2.addOrReplaceChild("plane_r2", CubeListBuilder.create().texOffs(5, 128).addBox(0.0F, -5.0F, -8.5F, 0.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -1.0F, 8.5F, 0.0F, 0.0F, 0.829F));

		PartDefinition leftleg = swimcontrol.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(98, 100).addBox(-1.5F, 0.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -8.0F, -8.0F));

		PartDefinition leftfoot = leftleg.addOrReplaceChild("leftfoot", CubeListBuilder.create().texOffs(92, 21).addBox(-4.0F, 0.0F, -8.0F, 8.0F, 3.0F, 11.0F, new CubeDeformation(0.01F)), PartPose.offset(1.5F, 5.0F, 0.0F));

		PartDefinition rightleg = swimcontrol.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(98, 100).mirror().addBox(-4.5F, 0.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-9.0F, -8.0F, -8.0F));

		PartDefinition rightfoot = rightleg.addOrReplaceChild("rightfoot", CubeListBuilder.create().texOffs(92, 21).mirror().addBox(-4.0F, 0.0F, -8.0F, 8.0F, 3.0F, 11.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-1.5F, 5.0F, 0.0F));

		PartDefinition leftleg2 = swimcontrol.addOrReplaceChild("leftleg2", CubeListBuilder.create().texOffs(98, 100).addBox(-1.5F, 0.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -8.0F, 10.0F));

		PartDefinition leftfoot2 = leftleg2.addOrReplaceChild("leftfoot2", CubeListBuilder.create().texOffs(60, 100).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 3.0F, 11.0F, new CubeDeformation(0.01F)), PartPose.offset(1.5F, 5.0F, 0.0F));

		PartDefinition rightleg2 = swimcontrol.addOrReplaceChild("rightleg2", CubeListBuilder.create().texOffs(98, 100).mirror().addBox(-4.5F, 0.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-9.0F, -8.0F, 10.0F));

		PartDefinition rightfoot2 = rightleg2.addOrReplaceChild("rightfoot2", CubeListBuilder.create().texOffs(60, 100).mirror().addBox(-4.0F, 0.0F, -3.0F, 8.0F, 3.0F, 11.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-1.5F, 5.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}



	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(BlurpumEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		netHeadYaw = Mth.clamp(netHeadYaw, -15.0F, 15.0F) * ((float)Math.PI / 180F);
		headPitch = Mth.clamp(headPitch, -15.0F, 15.0F) * ((float)Math.PI / 180F);
		float partialTicks = ageInTicks - entity.tickCount;

		float pitch = entity.getPitch(partialTicks) * Mth.DEG_TO_RAD;
		float tailYaw = entity.getTrailYaw(partialTicks);

		this.head.yRot = netHeadYaw;
		this.head.xRot = headPitch;
		if (this.young) {
			this.applyStatic(GenericPosesAnimation.BABY_TRANSFORM);
		}
		this.animateWalk(BlurpumAnimation.SWIM, limbSwing, limbSwingAmount * (1 - entity.landProgress/5), 2f, 2.5f);
		this.animateWalk(BlurpumAnimation.WALK, limbSwing, limbSwingAmount * (entity.landProgress/5), 2f, 2.5f);
		this.animateSmooth(entity.idleAnimationState, BlurpumAnimation.IDLE, ageInTicks, partialTicks, 0.3F);
		this.animateSmooth(entity.swimIdleAnimationState, BlurpumAnimation.SWIM_IDLE, ageInTicks, partialTicks, 0.3F);
		this.animateSmooth(entity.sitAnimationState, BlurpumAnimation.SIT, ageInTicks, partialTicks, 1F);
		this.animateSmooth(entity.danceAnimationState, BlurpumAnimation.DANCE, ageInTicks, partialTicks, 1F);
		this.animate(entity.biteAnimationState, BlurpumAnimation.BITE, ageInTicks, 1F);
		if (entity.isInWater()) {
			this.swimcontrol.xRot = pitch;
		}
		this.tail.yRot = Mth.lerp(0.25F, this.tail.yRot, tailYaw * 0.25F);
		this.tail2.yRot = Mth.lerp(0.15F, this.tail2.yRot, tailYaw * 0.2F);

	}
}