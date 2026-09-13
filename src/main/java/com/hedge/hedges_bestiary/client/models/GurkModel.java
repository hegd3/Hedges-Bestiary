package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.animations.GenericPosesAnimation;
import com.hedge.hedges_bestiary.client.animations.GurkAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.GurkEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class GurkModel extends HBModel<GurkEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.GURK_LAYER;
	private final ModelPart root;
	private final ModelPart swimcontrol;
	private final ModelPart bodyandarms;
	private final ModelPart body;
	private final ModelPart headrot;
	private final ModelPart head;
	private final ModelPart tail;
	private final ModelPart leftleg;
	private final ModelPart rightleg;
	private final ModelPart leftleg2;
	private final ModelPart rightleg2;

	public GurkModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.swimcontrol = this.root.getChild("swimcontrol");
		this.bodyandarms = this.swimcontrol.getChild("bodyandarms");
		this.body = this.bodyandarms.getChild("body");
		this.headrot = this.body.getChild("headrot");
		this.head = this.headrot.getChild("head");
		this.tail = this.body.getChild("tail");
		this.leftleg = this.bodyandarms.getChild("leftleg");
		this.rightleg = this.bodyandarms.getChild("rightleg");
		this.leftleg2 = this.swimcontrol.getChild("leftleg2");
		this.rightleg2 = this.swimcontrol.getChild("rightleg2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

		PartDefinition swimcontrol = root.addOrReplaceChild("swimcontrol", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bodyandarms = swimcontrol.addOrReplaceChild("bodyandarms", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 6.0F));

		PartDefinition body = bodyandarms.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.5F, -5.0F, -7.0F, 15.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 53).addBox(0.0F, -14.0F, -6.0F, 0.0F, 9.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -7.0F));

		PartDefinition headrot = body.addOrReplaceChild("headrot", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -7.0F));

		PartDefinition head = headrot.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 24).addBox(-5.5F, -4.0F, -10.0F, 11.0F, 7.0F, 10.0F, new CubeDeformation(0.01F))
				.texOffs(0, 41).addBox(-6.5F, 0.0F, -16.0F, 13.0F, 3.0F, 9.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 24).addBox(-3.5F, -3.0F, 0.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(37, 42).addBox(0.0F, -9.0F, 1.0F, 0.0F, 13.0F, 20.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 1.0F, 7.0F));

		PartDefinition leftleg = bodyandarms.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -0.5F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.01F))
				.texOffs(58, 7).addBox(5.0F, -0.5F, -4.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, -0.5F, -11.0F));

		PartDefinition rightleg = bodyandarms.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(58, 0).mirror().addBox(-7.0F, -0.5F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(58, 7).mirror().addBox(-8.0F, -0.5F, -4.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-7.5F, -0.5F, -11.0F));

		PartDefinition leftleg2 = swimcontrol.addOrReplaceChild("leftleg2", CubeListBuilder.create().texOffs(26, 53).addBox(0.0F, -0.5F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.01F))
				.texOffs(58, 13).addBox(3.0F, -0.5F, -2.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, 4.5F, 5.0F));

		PartDefinition rightleg2 = swimcontrol.addOrReplaceChild("rightleg2", CubeListBuilder.create().texOffs(26, 53).mirror().addBox(-5.0F, -0.5F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(58, 13).mirror().addBox(-6.0F, -0.5F, -2.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-7.5F, 4.5F, 5.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(GurkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

		netHeadYaw = Mth.clamp(netHeadYaw, -25.0F, 25.0F) * ((float)Math.PI / 180F);
		headPitch = Mth.clamp(headPitch, -25.0F, 25.0F) * ((float)Math.PI / 180F);
		float partialTicks = ageInTicks - entity.tickCount;
		this.swimcontrol.xRot = entity.isInFluidType() ? headPitch * 0.75f : 0;
		this.headrot.yRot = netHeadYaw;
		this.headrot.xRot = headPitch;
		if (this.young) {
			this.applyStatic(GenericPosesAnimation.BABY_TRANSFORM);
		}
		if (entity.isInFluidType()) {
			this.animateWalk(GurkAnimation.SWIM, limbSwing, limbSwingAmount, 1.2f, 2.5f);
			this.animate(entity.idleAnimationState, GurkAnimation.SWIM_IDLE, ageInTicks, 0.5f);
		} else {
			this.animateWalk(GurkAnimation.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
			this.animate(entity.idleAnimationState, GurkAnimation.IDLE, ageInTicks, 0.5f);
		}
		this.animateSmooth(entity.sitAnimationState, GurkAnimation.SIT, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.napAnimationState, GurkAnimation.SLEEP, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.danceAnimationState, GurkAnimation.DANCE, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.standAnimationState, GurkAnimation.STAND_UP, ageInTicks, partialTicks, 1f);

	}
}