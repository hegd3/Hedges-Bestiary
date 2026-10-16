package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.animations.TearacudaAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.TearacudaEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class TearacudaModel extends HBModel<TearacudaEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.TEARACUDA_LAYER;
	private final ModelPart root;
	private final ModelPart swimcontrol;
	private final ModelPart body;
	private final ModelPart leftfin;
	private final ModelPart rightfin;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart tailrot;
	private final ModelPart tail;
	private final ModelPart leftfin2;
	private final ModelPart rightfin2;
	private final ModelPart tail2rot;
	private final ModelPart tail2;

	public TearacudaModel(ModelPart root) {
		this.root = root.getChild("root");
		this.swimcontrol = this.root.getChild("swimcontrol");
		this.body = this.swimcontrol.getChild("body");
		this.leftfin = this.body.getChild("leftfin");
		this.rightfin = this.body.getChild("rightfin");
		this.head = this.swimcontrol.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.tailrot = this.swimcontrol.getChild("tailrot");
		this.tail = this.tailrot.getChild("tail");
		this.leftfin2 = this.tail.getChild("leftfin2");
		this.rightfin2 = this.tail.getChild("rightfin2");
		this.tail2rot = this.tail.getChild("tail2rot");
		this.tail2 = this.tail2rot.getChild("tail2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, 0.0F));

		PartDefinition swimcontrol = root.addOrReplaceChild("swimcontrol", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition body = swimcontrol.addOrReplaceChild("body", CubeListBuilder.create().texOffs(43, 82).addBox(0.0F, -15.0F, -8.0F, 0.0F, 7.0F, 21.0F, new CubeDeformation(0.01F))
				.texOffs(0, 0).addBox(-4.5F, -8.0F, -12.0F, 9.0F, 17.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition leftfin = body.addOrReplaceChild("leftfin", CubeListBuilder.create().texOffs(44, 42).addBox(0.0F, 0.0F, -13.0F, 0.0F, 8.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 9.0F, 0.0F));

		PartDefinition rightfin = body.addOrReplaceChild("rightfin", CubeListBuilder.create().texOffs(44, 42).addBox(0.0F, 0.0F, -13.0F, 0.0F, 8.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 9.0F, 0.0F));

		PartDefinition head = swimcontrol.addOrReplaceChild("head", CubeListBuilder.create().texOffs(68, 0).addBox(-5.5F, 1.0F, -13.0F, 11.0F, 15.0F, 13.0F, new CubeDeformation(0.01F))
				.texOffs(96, 50).addBox(5.5F, 6.0F, -13.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.01F))
				.texOffs(96, 50).mirror().addBox(-6.5F, 6.0F, -13.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(0, 85).addBox(-3.5F, 1.0F, -16.0F, 7.0F, 15.0F, 3.0F, new CubeDeformation(0.01F))
				.texOffs(68, 28).addBox(-3.5F, 1.0F, -18.0F, 7.0F, 9.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -18.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(20, 85).addBox(-3.5F, -14.0F, -3.0F, 7.0F, 14.0F, 3.0F, new CubeDeformation(0.02F))
				.texOffs(96, 39).addBox(-1.5F, -12.0F, 0.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 15.0F, -16.0F));

		PartDefinition tailrot = swimcontrol.addOrReplaceChild("tailrot", CubeListBuilder.create(), PartPose.offset(0.0F, -8.5F, 13.0F));

		PartDefinition tail = tailrot.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(84, 76).addBox(-3.5F, -5.5F, 0.0F, 7.0F, 10.0F, 11.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftfin2 = tail.addOrReplaceChild("leftfin2", CubeListBuilder.create().texOffs(86, 28).addBox(0.0F, 0.0F, -3.5F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 4.5F, 3.5F));

		PartDefinition rightfin2 = tail.addOrReplaceChild("rightfin2", CubeListBuilder.create().texOffs(86, 28).mirror().addBox(0.0F, 0.0F, -3.5F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.5F, 4.5F, 3.5F));

		PartDefinition tail2rot = tail.addOrReplaceChild("tail2rot", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 9.0F));

		PartDefinition tail2 = tail2rot.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 42).addBox(0.0F, -13.5F, 0.0F, 0.0F, 21.0F, 22.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(TearacudaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		headPitch = Mth.clamp(headPitch, -55.0F, 55.0F) * ((float) Math.PI / 180F);
		float tailYaw = entity.getTrailYaw(ageInTicks - entity.tickCount);
		this.tailrot.yRot = Mth.lerp(0.3F, this.tailrot.yRot, tailYaw * 0.2F);
		this.tail2rot.yRot = Mth.lerp(0.3F, this.tail2rot.yRot, tailYaw * 0.15F);

		if (entity.isInFluidType()) {
			this.swimcontrol.xRot = headPitch;
			this.swimcontrol.zRot = entity.roll;
			this.animate(entity.idleAnimationState, TearacudaAnimation.idle, ageInTicks, 0.5f);
			if (entity.getAnimState() != 2) {
				this.animateWalk(TearacudaAnimation.swim, limbSwing, limbSwingAmount, 2.5f, 2.5f);
			} else {
				this.animate(entity.frenzyAnimationState, TearacudaAnimation.frenzy, ageInTicks, 1.5f);
			}
		} else {
			if (entity.groundTimer > 0) {
				this.swimcontrol.xRot = 0;
				this.animate(entity.idleAnimationState, TearacudaAnimation.flop, ageInTicks, 1f);
			} else {
				this.swimcontrol.xRot = headPitch;
				this.animate(entity.idleAnimationState, TearacudaAnimation.air, ageInTicks, 2f);
			}
		}
		this.animate(entity.biteAnimationState, TearacudaAnimation.bite, ageInTicks, 1f);
	}
}