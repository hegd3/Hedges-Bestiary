package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.animations.*;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.MurkEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MurkModel extends HBModel<MurkEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.MURK_LAYER;
	private final ModelPart root;
	public final ModelPart swimcontrol;
	public final ModelPart wholebody;
	private final ModelPart body;
	private final ModelPart big_spike_left;
	private final ModelPart spikes_left;
	private final ModelPart spikes_right;
	private final ModelPart big_spike_right;
	private final ModelPart tail;
	private final ModelPart spikes_right2;
	private final ModelPart spikes_left2;
	private final ModelPart tail2;
	private final ModelPart neck;
	private final ModelPart spikes_left3;
	private final ModelPart spikes_right3;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart leftleg;
	private final ModelPart leftfoot;
	private final ModelPart leftleg2;
	private final ModelPart leftfoot2;
	private final ModelPart rightleg;
	private final ModelPart rightfoot;
	private final ModelPart rightleg2;
	private final ModelPart rightfoot2;
	public MurkModel(ModelPart root) {
		super(0.5f, 24);

		this.root = root.getChild("root");
		this.swimcontrol = this.root.getChild("swimcontrol");
		this.wholebody = this.swimcontrol.getChild("wholebody");
		this.body = this.wholebody.getChild("body");
		this.big_spike_left = this.body.getChild("big_spike_left");
		this.spikes_left = this.body.getChild("spikes_left");
		this.spikes_right = this.body.getChild("spikes_right");
		this.big_spike_right = this.body.getChild("big_spike_right");
		this.tail = this.body.getChild("tail");
		this.spikes_right2 = this.tail.getChild("spikes_right2");
		this.spikes_left2 = this.tail.getChild("spikes_left2");
		this.tail2 = this.tail.getChild("tail2");
		this.neck = this.wholebody.getChild("neck");
		this.spikes_left3 = this.neck.getChild("spikes_left3");
		this.spikes_right3 = this.neck.getChild("spikes_right3");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.leftleg = this.swimcontrol.getChild("leftleg");
		this.leftfoot = this.leftleg.getChild("leftfoot");
		this.leftleg2 = this.swimcontrol.getChild("leftleg2");
		this.leftfoot2 = this.leftleg2.getChild("leftfoot2");
		this.rightleg = this.swimcontrol.getChild("rightleg");
		this.rightfoot = this.rightleg.getChild("rightfoot");
		this.rightleg2 = this.swimcontrol.getChild("rightleg2");
		this.rightfoot2 = this.rightleg2.getChild("rightfoot2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition swimcontrol = root.addOrReplaceChild("swimcontrol", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition wholebody = swimcontrol.addOrReplaceChild("wholebody", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition body = wholebody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-14.5F, -12.5F, -18.0F, 29.0F, 25.0F, 36.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 1.0F));

		PartDefinition big_spike_left = body.addOrReplaceChild("big_spike_left", CubeListBuilder.create().texOffs(142, 128).addBox(-4.5F, -16.0F, -0.5F, 9.0F, 16.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(179, 71).addBox(-4.5F, -16.0F, 8.5F, 9.0F, 5.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(8.0F, -12.5F, -11.5F));

		PartDefinition spikes_left = body.addOrReplaceChild("spikes_left", CubeListBuilder.create().texOffs(0, 141).addBox(0.0F, -10.0F, -17.5F, 0.0F, 10.0F, 35.0F, new CubeDeformation(0.01F)), PartPose.offset(14.5F, -12.5F, 0.5F));

		PartDefinition spikes_right = body.addOrReplaceChild("spikes_right", CubeListBuilder.create().texOffs(0, 141).mirror().addBox(0.0F, -10.0F, -17.5F, 0.0F, 10.0F, 35.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-14.5F, -12.5F, 0.5F));

		PartDefinition big_spike_right = body.addOrReplaceChild("big_spike_right", CubeListBuilder.create().texOffs(142, 128).mirror().addBox(-4.5F, -16.0F, -0.5F, 9.0F, 16.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(179, 71).mirror().addBox(-4.5F, -16.0F, 8.5F, 9.0F, 5.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-8.0F, -12.5F, -11.5F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(78, 128).addBox(-8.5F, -8.5F, 0.5F, 17.0F, 17.0F, 15.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 4.0F, 17.5F));

		PartDefinition spikes_right2 = tail.addOrReplaceChild("spikes_right2", CubeListBuilder.create().texOffs(148, 21).mirror().addBox(0.0F, -5.0F, -6.5F, 0.0F, 5.0F, 13.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-8.5F, -8.5F, 10.0F));

		PartDefinition spikes_left2 = tail.addOrReplaceChild("spikes_left2", CubeListBuilder.create().texOffs(148, 21).addBox(0.0F, -5.0F, -6.5F, 0.0F, 5.0F, 13.0F, new CubeDeformation(0.01F)), PartPose.offset(8.5F, -8.5F, 10.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(94, 61).addBox(-5.5F, -2.5F, 0.5F, 11.0F, 11.0F, 27.0F, new CubeDeformation(0.02F))
				.texOffs(0, 148).addBox(0.0F, -8.5F, 12.5F, 0.0F, 6.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 15.0F));

		PartDefinition neck = wholebody.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(94, 99).addBox(-11.5F, -7.5F, -14.0F, 23.0F, 15.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -17.0F));

		PartDefinition spikes_left3 = neck.addOrReplaceChild("spikes_left3", CubeListBuilder.create().texOffs(177, 18).addBox(0.0F, -6.0F, -7.5F, 0.0F, 6.0F, 15.0F, new CubeDeformation(0.01F)), PartPose.offset(11.5F, -7.5F, -7.5F));

		PartDefinition spikes_right3 = neck.addOrReplaceChild("spikes_right3", CubeListBuilder.create().texOffs(177, 18).mirror().addBox(0.0F, -6.0F, -7.5F, 0.0F, 6.0F, 15.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-11.5F, -7.5F, -7.5F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(132, 7).addBox(-8.5F, -7.5F, -14.0F, 17.0F, 6.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(207, 23).addBox(-6.5F, -4.5F, -25.0F, 13.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(179, 103).addBox(-10.5F, -1.5F, -4.0F, 21.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 61).addBox(-12.5F, -1.5F, -26.0F, 25.0F, 3.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, -14.0F));

		PartDefinition ridge_r1 = head.addOrReplaceChild("ridge_r1", CubeListBuilder.create().texOffs(213, 5).mirror().addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-8.5F, -4.5F, -7.0F, 0.0F, 0.0F, -0.5672F));

		PartDefinition ridge_r2 = head.addOrReplaceChild("ridge_r2", CubeListBuilder.create().texOffs(213, 5).addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(8.5F, -4.5F, -7.0F, 0.0F, 0.0F, 0.5672F));

		PartDefinition ridge_r3 = head.addOrReplaceChild("ridge_r3", CubeListBuilder.create().texOffs(214, 12).addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(6.5F, -4.5F, -21.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition ridge_r4 = head.addOrReplaceChild("ridge_r4", CubeListBuilder.create().texOffs(214, 12).mirror().addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-6.5F, -4.5F, -21.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition ridge_r5 = head.addOrReplaceChild("ridge_r5", CubeListBuilder.create().texOffs(194, 78).mirror().addBox(0.0F, 0.0F, -7.0F, 0.0F, 4.0F, 14.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-12.5F, 1.5F, -19.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition ridge_r6 = head.addOrReplaceChild("ridge_r6", CubeListBuilder.create().texOffs(194, 78).addBox(0.0F, 0.0F, -7.0F, 0.0F, 4.0F, 14.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(12.5F, 1.5F, -19.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition ridge_r7 = head.addOrReplaceChild("ridge_r7", CubeListBuilder.create().texOffs(192, 124).addBox(-12.5F, 0.0F, 0.0F, 25.0F, 4.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 1.5F, -26.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 86).addBox(-12.5F, 0.0F, -22.0F, 25.0F, 3.0F, 22.0F, new CubeDeformation(0.0F))
				.texOffs(130, 39).addBox(-9.5F, -2.0F, -20.0F, 19.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, -4.0F));

		PartDefinition leftleg = swimcontrol.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(9, 116).addBox(-2.0F, 0.0F, -7.0F, 11.0F, 15.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(12.0F, -4.0F, -13.0F));

		PartDefinition leftfoot = leftleg.addOrReplaceChild("leftfoot", CubeListBuilder.create().texOffs(88, 164).addBox(-8.5F, 0.0F, -15.0F, 17.0F, 4.0F, 14.0F, new CubeDeformation(0.01F)), PartPose.offset(3.5F, 14.0F, 0.0F));

		PartDefinition leftleg2 = swimcontrol.addOrReplaceChild("leftleg2", CubeListBuilder.create().texOffs(9, 116).addBox(-2.0F, 0.0F, -7.0F, 11.0F, 15.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(12.0F, -4.0F, 17.0F));

		PartDefinition leftfoot2 = leftleg2.addOrReplaceChild("leftfoot2", CubeListBuilder.create().texOffs(90, 190).addBox(-6.5F, -1.0F, 0.0F, 13.0F, 4.0F, 14.0F, new CubeDeformation(0.01F)), PartPose.offset(3.5F, 15.0F, -3.0F));

		PartDefinition rightleg = swimcontrol.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(9, 116).mirror().addBox(-9.0F, 0.0F, -7.0F, 11.0F, 15.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-12.0F, -4.0F, -13.0F));

		PartDefinition rightfoot = rightleg.addOrReplaceChild("rightfoot", CubeListBuilder.create().texOffs(88, 164).mirror().addBox(-8.5F, -1.0F, -15.0F, 17.0F, 4.0F, 14.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-3.5F, 15.0F, 0.0F));

		PartDefinition rightleg2 = swimcontrol.addOrReplaceChild("rightleg2", CubeListBuilder.create().texOffs(9, 116).mirror().addBox(-9.0F, 0.0F, -7.0F, 11.0F, 15.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-12.0F, -4.0F, 17.0F));

		PartDefinition rightfoot2 = rightleg2.addOrReplaceChild("rightfoot2", CubeListBuilder.create().texOffs(90, 190).mirror().addBox(-6.5F, -1.0F, 0.0F, 13.0F, 4.0F, 14.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-3.5F, 15.0F, -3.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(MurkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

		netHeadYaw = Mth.clamp(netHeadYaw, -25.0F, 25.0F) * Mth.DEG_TO_RAD;
		headPitch = Mth.clamp(headPitch, -25.0F, 25.0F) * Mth.DEG_TO_RAD;
		float pitch = entity.getPitch(ageInTicks - entity.tickCount) * Mth.DEG_TO_RAD;
		float tailYaw = entity.getTrailYaw(ageInTicks - entity.tickCount);




		if (this.young) {
			this.applyStatic(GenericPosesAnimation.BABY_TRANSFORM_WITH_NECK);
		}
		this.animateSmooth(entity.swimIdleAnimationState, MurkBasicsAnimation.SWIM_IDLE, ageInTicks, 0.5f);
		this.animateSmooth(entity.idleAnimationState, MurkBasicsAnimation.IDLE, ageInTicks, 0.33f);
		this.animateWalk(MurkBasicsAnimation.SWIM, limbSwing, limbSwingAmount * (1 - entity.landProgress /5), 1.5f, 2.5f);
		this.animateWalk(MurkBasicsAnimation.WALK, limbSwing, limbSwingAmount* (entity.landProgress /5), 1.5f, 2.5f);

		if (entity.isInWater()) {
			headPitch *= 0.6F;
			this.swimcontrol.xRot = pitch;
			this.animate(entity.multiBiteAnimationState, entity.swingingLeft() ? MurkAttacksAnimation.MULTI_BITE_LEFT: MurkAttacksAnimation.MULTI_BITE_RIGHT, ageInTicks, 1);

			this.animate(entity.roarAnimationState, MurkAttacksAnimation.ROAR, ageInTicks, 1f);


		} else {
			this.animate(entity.roarAnimationState, MurkAttacksAnimation.ROAR_LAND, ageInTicks, 1f);

			this.animate(entity.multiBiteAnimationState, entity.swingingLeft() ? MurkAttacksAnimation.MULTIBITE_LEFT_LAND : MurkAttacksAnimation.MULTIBITE_RIGHT_LAND, ageInTicks, 1);


		}
		this.animate(entity.biteAnimationState, MurkAttacksAnimation.BITE, ageInTicks, 1f);
		this.animate(entity.breathAnimationState, entity.swingingLeft() ? MurkAttacksAnimation.BREATH_LEFT : MurkAttacksAnimation.BREATH_RIGHT, ageInTicks, 1f);
		this.animate(entity.sideSlamAnimationState, entity.swingingLeft() ? MurkAttacksAnimation.SIDE_SLAM_LEFT : MurkAttacksAnimation.SIDE_SLAM_RIGHT, ageInTicks, 1f);
		this.animateSmooth(entity.clicksAnimationState, MurkBasicsAnimation.CLICKS, ageInTicks, 1f);
		this.animateSmooth(entity.yawnAnimationState, MurkBasicsAnimation.YAWN, ageInTicks, 1f);
		this.animateSmooth(entity.napAnimationState, MurkBasicsAnimation.SLEEP, ageInTicks, 1f);
		this.animateSmooth(entity.sitAnimationState, MurkBasicsAnimation.SIT, ageInTicks, 1f);
		this.animateSmooth(entity.danceAnimationState, MurkBasicsAnimation.DANCE, ageInTicks, 1f);
		this.animateSmooth(entity.eatAnimationState, MurkAttacksAnimation.BITE, ageInTicks, 2f);

		this.tail.yRot = Mth.lerp(0.3F, this.tail.yRot, tailYaw * 0.25F);
		this.tail2.yRot = Mth.lerp(0.3F, this.tail2.yRot, tailYaw * 0.2F);

		this.neck.yRot += netHeadYaw;
		this.neck.xRot += headPitch / 2;
		this.head.yRot += netHeadYaw;
		this.head.xRot += headPitch;

	}
}