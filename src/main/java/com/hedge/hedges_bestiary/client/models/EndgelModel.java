package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.animations.EndgelAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.EndgelEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class EndgelModel extends HBModel<EndgelEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.BANSHEE_LAYER;
	private final ModelPart root;
	private final ModelPart flycontrol;
	private final ModelPart leftwing;
	private final ModelPart leftwingflap;
	private final ModelPart leftwing3;
	private final ModelPart leftwingflap3;
	private final ModelPart rightwing3;
	private final ModelPart rightwingflap3;
	private final ModelPart rightwing;
	private final ModelPart rightwingflap;
	private final ModelPart mouth;
	private final ModelPart tail;
	private final ModelPart leftwing2;
	private final ModelPart leftwingflap2;
	private final ModelPart leftwing4;
	private final ModelPart leftwingflap4;
	private final ModelPart rightwing2;
	private final ModelPart rightwingflap2;
	private final ModelPart rightwing4;
	private final ModelPart rightwingflap4;
	private final ModelPart tail2;
	private final ModelPart tail3;

	public EndgelModel(ModelPart root) {
		this.root = root.getChild("root");
		this.flycontrol = this.root.getChild("flycontrol");
		this.leftwing = this.flycontrol.getChild("leftwing");
		this.leftwingflap = this.leftwing.getChild("leftwingflap");
		this.leftwing3 = this.flycontrol.getChild("leftwing3");
		this.leftwingflap3 = this.leftwing3.getChild("leftwingflap3");
		this.rightwing3 = this.flycontrol.getChild("rightwing3");
		this.rightwingflap3 = this.rightwing3.getChild("rightwingflap3");
		this.rightwing = this.flycontrol.getChild("rightwing");
		this.rightwingflap = this.rightwing.getChild("rightwingflap");
		this.mouth = this.flycontrol.getChild("mouth");
		this.tail = this.flycontrol.getChild("tail");
		this.leftwing2 = this.tail.getChild("leftwing2");
		this.leftwingflap2 = this.leftwing2.getChild("leftwingflap2");
		this.leftwing4 = this.tail.getChild("leftwing4");
		this.leftwingflap4 = this.leftwing4.getChild("leftwingflap4");
		this.rightwing2 = this.tail.getChild("rightwing2");
		this.rightwingflap2 = this.rightwing2.getChild("rightwingflap2");
		this.rightwing4 = this.tail.getChild("rightwing4");
		this.rightwingflap4 = this.rightwing4.getChild("rightwingflap4");
		this.tail2 = this.tail.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition flycontrol = root.addOrReplaceChild("flycontrol", CubeListBuilder.create().texOffs(4, 0).addBox(-17.5F, -11.0F, -22.0F, 35.0F, 27.0F, 48.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftwing = flycontrol.addOrReplaceChild("leftwing", CubeListBuilder.create().texOffs(0, 124).addBox(-0.5F, -3.0F, -9.0F, 41.0F, 6.0F, 17.0F, new CubeDeformation(0.02F))
				.texOffs(0, 147).addBox(-0.5F, -3.0F, 8.0F, 42.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(18.0F, 13.0F, 1.0F));

		PartDefinition leftwingflap = leftwing.addOrReplaceChild("leftwingflap", CubeListBuilder.create().texOffs(182, 0).addBox(0.0F, 0.0F, -7.0F, 16.0F, 4.0F, 13.0F, new CubeDeformation(0.01F))
				.texOffs(114, 160).addBox(0.0F, 0.0F, -7.0F, 27.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(40.5F, -3.0F, -2.0F));

		PartDefinition leftwing3 = flycontrol.addOrReplaceChild("leftwing3", CubeListBuilder.create().texOffs(0, 124).addBox(-0.5F, -3.0F, -9.0F, 41.0F, 6.0F, 17.0F, new CubeDeformation(0.02F))
				.texOffs(0, 147).addBox(-0.5F, -3.0F, 8.0F, 42.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(18.0F, -8.0F, 16.0F));

		PartDefinition leftwingflap3 = leftwing3.addOrReplaceChild("leftwingflap3", CubeListBuilder.create().texOffs(182, 0).addBox(0.0F, 0.0F, -7.0F, 16.0F, 4.0F, 13.0F, new CubeDeformation(0.01F))
				.texOffs(114, 160).addBox(0.0F, 0.0F, -7.0F, 27.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(40.5F, -3.0F, -2.0F));

		PartDefinition rightwing3 = flycontrol.addOrReplaceChild("rightwing3", CubeListBuilder.create().texOffs(0, 124).mirror().addBox(-40.5F, -3.0F, -9.0F, 41.0F, 6.0F, 17.0F, new CubeDeformation(0.02F)).mirror(false)
				.texOffs(0, 147).mirror().addBox(-41.5F, -3.0F, 8.0F, 42.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-18.0F, -8.0F, 16.0F));

		PartDefinition rightwingflap3 = rightwing3.addOrReplaceChild("rightwingflap3", CubeListBuilder.create().texOffs(182, 0).mirror().addBox(-16.0F, 0.0F, -7.0F, 16.0F, 4.0F, 13.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(114, 160).mirror().addBox(-27.0F, 0.0F, -7.0F, 27.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-40.5F, -3.0F, -2.0F));

		PartDefinition rightwing = flycontrol.addOrReplaceChild("rightwing", CubeListBuilder.create().texOffs(0, 124).mirror().addBox(-40.5F, -3.0F, -9.0F, 41.0F, 6.0F, 17.0F, new CubeDeformation(0.02F)).mirror(false)
				.texOffs(0, 147).mirror().addBox(-41.5F, -3.0F, 8.0F, 42.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-18.0F, 13.0F, 1.0F));

		PartDefinition rightwingflap = rightwing.addOrReplaceChild("rightwingflap", CubeListBuilder.create().texOffs(182, 0).mirror().addBox(-16.0F, 0.0F, -7.0F, 16.0F, 4.0F, 13.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(114, 160).mirror().addBox(-27.0F, 0.0F, -7.0F, 27.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-40.5F, -3.0F, -2.0F));

		PartDefinition mouth = flycontrol.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(125, 75).addBox(-20.5F, -17.0F, -7.0F, 41.0F, 33.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(127, 116).addBox(-20.5F, -17.0F, -12.0F, 41.0F, 33.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -22.0F));

		PartDefinition tail = flycontrol.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(2, 75).addBox(-15.5F, -13.0F, 0.0F, 31.0F, 23.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 26.0F));

		PartDefinition leftwing2 = tail.addOrReplaceChild("leftwing2", CubeListBuilder.create().texOffs(182, 19).addBox(-0.5F, -3.0F, -6.0F, 17.0F, 4.0F, 12.0F, new CubeDeformation(0.02F))
				.texOffs(182, 65).addBox(-0.5F, -3.0F, 6.0F, 17.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, 7.0F, 14.0F));

		PartDefinition leftwingflap2 = leftwing2.addOrReplaceChild("leftwingflap2", CubeListBuilder.create().texOffs(156, 196).addBox(0.0F, 0.0F, -6.0F, 15.0F, 3.0F, 11.0F, new CubeDeformation(0.01F))
				.texOffs(43, 224).addBox(0.0F, 0.0F, -6.0F, 21.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(16.5F, -3.0F, 0.0F));

		PartDefinition leftwing4 = tail.addOrReplaceChild("leftwing4", CubeListBuilder.create().texOffs(182, 19).addBox(-0.5F, -3.0F, -6.0F, 17.0F, 4.0F, 12.0F, new CubeDeformation(0.02F))
				.texOffs(182, 65).addBox(-0.5F, -3.0F, 6.0F, 17.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, -10.0F, 21.0F));

		PartDefinition leftwingflap4 = leftwing4.addOrReplaceChild("leftwingflap4", CubeListBuilder.create().texOffs(156, 196).addBox(0.0F, 0.0F, -6.0F, 15.0F, 3.0F, 11.0F, new CubeDeformation(0.01F))
				.texOffs(43, 224).addBox(0.0F, 0.0F, -6.0F, 21.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(16.5F, -3.0F, 0.0F));

		PartDefinition rightwing2 = tail.addOrReplaceChild("rightwing2", CubeListBuilder.create().texOffs(182, 19).mirror().addBox(-16.5F, -3.0F, -6.0F, 17.0F, 4.0F, 12.0F, new CubeDeformation(0.02F)).mirror(false)
				.texOffs(182, 65).mirror().addBox(-16.5F, -3.0F, 6.0F, 17.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-16.0F, 7.0F, 14.0F));

		PartDefinition rightwingflap2 = rightwing2.addOrReplaceChild("rightwingflap2", CubeListBuilder.create().texOffs(156, 196).mirror().addBox(-15.0F, 0.0F, -6.0F, 15.0F, 3.0F, 11.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(43, 224).mirror().addBox(-21.0F, 0.0F, -6.0F, 21.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-16.5F, -3.0F, 0.0F));

		PartDefinition rightwing4 = tail.addOrReplaceChild("rightwing4", CubeListBuilder.create().texOffs(182, 19).mirror().addBox(-16.5F, -3.0F, -6.0F, 17.0F, 4.0F, 12.0F, new CubeDeformation(0.02F)).mirror(false)
				.texOffs(182, 65).mirror().addBox(-16.5F, -3.0F, 6.0F, 17.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-16.0F, -10.0F, 21.0F));

		PartDefinition rightwingflap4 = rightwing4.addOrReplaceChild("rightwingflap4", CubeListBuilder.create().texOffs(156, 196).mirror().addBox(-15.0F, 0.0F, -6.0F, 15.0F, 3.0F, 11.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(43, 224).mirror().addBox(-21.0F, 0.0F, -6.0F, 21.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-16.5F, -3.0F, 0.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(80, 180).addBox(-10.5F, -9.0F, 0.0F, 21.0F, 18.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 26.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 159).addBox(-5.5F, -5.0F, 0.0F, 11.0F, 8.0F, 29.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 17.0F));

		PartDefinition body_r1 = tail3.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(182, 37).mirror().addBox(0.0F, -7.0F, -10.5F, 0.0F, 7.0F, 21.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-5.5F, -5.0F, 16.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition body_r2 = tail3.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(0, 196).mirror().addBox(0.0F, 0.0F, -10.5F, 0.0F, 7.0F, 21.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-5.5F, 3.0F, 16.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition body_r3 = tail3.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(0, 196).addBox(0.0F, 0.0F, -10.5F, 0.0F, 7.0F, 21.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(5.5F, 3.0F, 16.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition body_r4 = tail3.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(182, 37).addBox(0.0F, -7.0F, -10.5F, 0.0F, 7.0F, 21.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(5.5F, -5.0F, 16.5F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(EndgelEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		this.animateSmooth(entity.idleAnimationState, EndgelAnimation.FLY, ageInTicks, partialTicks, 0.5f + limbSwingAmount * 0.35f);
		this.animate(entity.spinAnimationState, entity.getLeft() ? EndgelAnimation.ROLL_LEFT: EndgelAnimation.ROLL_RIGHT, ageInTicks);
		this.animate(entity.shootAnimationState, EndgelAnimation.SHOOT, ageInTicks);
		this.animateSmooth(entity.screamWindupAnimationState, EndgelAnimation.SCREAM_WINDUP, partialTicks, ageInTicks, 1f);
		this.animateSmooth(entity.screamAnimationState, EndgelAnimation.SCREAM, ageInTicks, partialTicks, 1f);
		float tailYaw = entity.getTrailYaw(ageInTicks - entity.tickCount);
		this.tail.yRot = Mth.lerp(0.3F, this.tail.yRot, tailYaw * 0.3F);
		this.tail2.yRot = Mth.lerp(0.3F, this.tail2.yRot, tailYaw * 0.25F);
		this.tail3.yRot = Mth.lerp(0.3F, this.tail3.yRot, tailYaw * 0.15F);
		this.flycontrol.xRot = Mth.clamp(headPitch, -35.0F, 35.0F) * ((float) Math.PI / 180F);
		this.flycontrol.zRot = entity.roll;
	}
}