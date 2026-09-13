package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.animations.GenericPosesAnimation;
import com.hedge.hedges_bestiary.client.animations.ZappetAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.ZappetEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ZappetModel extends HBModel<ZappetEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.ZAPPET_LAYER;
	private final ModelPart root;
	private final ModelPart flycontrol;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart headrot;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart leftleg;
	private final ModelPart leftlegpos;
	private final ModelPart rightleg;
	private final ModelPart rightlegpos;
	private final ModelPart leftwing;
	private final ModelPart leftwing2;
	private final ModelPart rightwing;
	private final ModelPart rightwing2;

	public ZappetModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.flycontrol = this.root.getChild("flycontrol");
		this.body = this.flycontrol.getChild("body");
		this.tail = this.body.getChild("tail");
		this.tail2 = this.tail.getChild("tail2");
		this.headrot = this.body.getChild("headrot");
		this.head = this.headrot.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.leftleg = this.flycontrol.getChild("leftleg");
		this.leftlegpos = this.leftleg.getChild("leftlegpos");
		this.rightleg = this.flycontrol.getChild("rightleg");
		this.rightlegpos = this.rightleg.getChild("rightlegpos");
		this.leftwing = this.flycontrol.getChild("leftwing");
		this.leftwing2 = this.leftwing.getChild("leftwing2");
		this.rightwing = this.flycontrol.getChild("rightwing");
		this.rightwing2 = this.rightwing.getChild("rightwing2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition flycontrol = root.addOrReplaceChild("flycontrol", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = flycontrol.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -7.0F, -8.0F, 9.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 9).addBox(-2.5F, -2.0F, 0.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(37, 63).addBox(0.0F, -8.0F, 1.0F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -4.0F, 4.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(1, 43).addBox(0.0F, -1.0F, 0.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -1.0F, 6.0F));

		PartDefinition headrot = body.addOrReplaceChild("headrot", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, -3.5F));

		PartDefinition head = headrot.addOrReplaceChild("head", CubeListBuilder.create().texOffs(34, 19).addBox(-3.5F, -6.0F, -4.5F, 7.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 19).addBox(-3.5F, -6.0F, -14.5F, 7.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(18, 46).addBox(-3.5F, -10.0F, 0.5F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 35).addBox(-3.5F, -7.0F, -14.5F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(42, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.01F))
				.texOffs(34, 34).addBox(-3.5F, -2.0F, -15.0F, 7.0F, 2.0F, 10.0F, new CubeDeformation(0.01F))
				.texOffs(23, 56).addBox(0.0F, -7.0F, -10.0F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -6.0F, 0.5F));

		PartDefinition leftleg = flycontrol.addOrReplaceChild("leftleg", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 1.0F));

		PartDefinition leftlegpos = leftleg.addOrReplaceChild("leftlegpos", CubeListBuilder.create().texOffs(0, 55).addBox(-1.5F, -0.05F, -4.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightleg = flycontrol.addOrReplaceChild("rightleg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 1.0F));

		PartDefinition rightlegpos = rightleg.addOrReplaceChild("rightlegpos", CubeListBuilder.create().texOffs(0, 55).mirror().addBox(-1.5F, -0.05F, -4.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftwing = flycontrol.addOrReplaceChild("leftwing", CubeListBuilder.create().texOffs(40, 52).addBox(0.0F, 0.0F, -3.0F, 6.0F, 0.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offset(4.5F, -2.0F, -5.0F));

		PartDefinition leftwing2 = leftwing.addOrReplaceChild("leftwing2", CubeListBuilder.create().texOffs(40, 46).addBox(0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(6.0F, 0.0F, 0.0F));

		PartDefinition rightwing = flycontrol.addOrReplaceChild("rightwing", CubeListBuilder.create().texOffs(40, 52).mirror().addBox(-6.0F, 0.0F, -3.0F, 6.0F, 0.0F, 5.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-4.5F, -2.0F, -5.0F));

		PartDefinition rightwing2 = rightwing.addOrReplaceChild("rightwing2", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-5.0F, 0.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(ZappetEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

		netHeadYaw = Mth.clamp(netHeadYaw, -35.0F, 35.0F) * ((float)Math.PI / 180F);
		headPitch = Mth.clamp(headPitch, -25.0F, 25.0F) * ((float)Math.PI / 180F);;
		float partialTicks = ageInTicks - entity.tickCount;
		this.headrot.yRot = netHeadYaw;
		this.headrot.xRot = headPitch;
		if (this.young) {
			this.applyStatic(GenericPosesAnimation.BABY_TRANSFORM);
		}

		this.animateSmooth(entity.callAnimationState, ZappetAnimation.CALL, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.shootAnimationState, ZappetAnimation.SHOOT, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.sitAnimationState, ZappetAnimation.SIT, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.danceAnimationState, ZappetAnimation.DANCE, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.rideAnimationState, ZappetAnimation.FLY, ageInTicks, partialTicks, 2 + limbSwingAmount);

		if (entity.isPassenger()) {
			if (entity.getVehicle().onGround()) {
				this.animate(entity.idleAnimationState, ZappetAnimation.IDLE, ageInTicks, 0.5f);
			} else {
				this.animate(entity.idleAnimationState, ZappetAnimation.GLIDE, ageInTicks, 1 + limbSwingAmount);
			}
		} else {
			if (entity.onGround()) {
				this.animate(entity.idleAnimationState, ZappetAnimation.IDLE, ageInTicks, 0.5f);
			} else {
				this.animate(entity.idleAnimationState, ZappetAnimation.GLIDE, ageInTicks, 1 + limbSwingAmount);
			}
		}
		if (entity.isFlying()) {
			float flyProgress = entity.getFlyProgress(partialTicks);

			this.animateWalk(ZappetAnimation.FLY, limbSwing, limbSwingAmount, 2f, 2.5f);

			this.flycontrol.xRot = entity.getFlightPitch(partialTicks) / 57.295776F * flyProgress / 2;
			this.flycontrol.zRot = entity.getFlightRoll(partialTicks) / 57.295776F * flyProgress / 2;

		} else if (!entity.isPassenger()) {
			this.animateWalk(ZappetAnimation.WALK, limbSwing, limbSwingAmount, 1.8f, 2.5f);
		}
	}
}