package com.hedge.hedges_bestiary.client.models;

import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.animations.ZipperlingAnimation;
import com.hedge.hedges_bestiary.client.animations.ZippermouthAnimation;
import com.hedge.hedges_bestiary.client.models.HBModel;
import com.hedge.hedges_bestiary.entity.living.ZipperlingEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ZipperlingModel extends HBModel<ZipperlingEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.ZIPPERLING_LAYER;
	private final ModelPart root;
	private final ModelPart segment;
	private final ModelPart leftfin;
	private final ModelPart rightfin;
	private final ModelPart segment2;
	private final ModelPart segment3;
	private final ModelPart tail;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart[] segmentParts;

	public ZipperlingModel(ModelPart root) {
		this.root = root.getChild("root");
		this.segment = this.root.getChild("segment");
		this.leftfin = this.segment.getChild("leftfin");
		this.rightfin = this.segment.getChild("rightfin");
		this.segment2 = this.segment.getChild("segment2");
		this.segment3 = this.segment2.getChild("segment3");
		this.tail = this.segment3.getChild("tail");
		this.head = this.root.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.segmentParts = new ModelPart[]{this.segment, this.segment2, this.segment3, this.tail};

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition segment = root.addOrReplaceChild("segment", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -5.0F, 0.0F, 13.0F, 11.0F, 21.0F, new CubeDeformation(0.0F))
		.texOffs(58, 76).addBox(0.0F, -12.0F, 3.0F, 0.0F, 7.0F, 16.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -6.0F, 3.0F));

		PartDefinition leftfin = segment.addOrReplaceChild("leftfin", CubeListBuilder.create().texOffs(70, 24).addBox(0.0F, 0.0F, -2.0F, 0.0F, 12.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 6.0F, 3.0F));

		PartDefinition rightfin = segment.addOrReplaceChild("rightfin", CubeListBuilder.create().texOffs(70, 24).mirror().addBox(0.0F, 0.0F, -2.0F, 0.0F, 12.0F, 15.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.5F, 6.0F, 3.0F));

		PartDefinition segment2 = segment.addOrReplaceChild("segment2", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -5.0F, 0.0F, 13.0F, 11.0F, 21.0F, new CubeDeformation(0.0F))
		.texOffs(90, 76).addBox(0.0F, -8.0F, 3.0F, 0.0F, 3.0F, 14.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 21.0F));

		PartDefinition segment3 = segment2.addOrReplaceChild("segment3", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -5.0F, 0.0F, 13.0F, 11.0F, 21.0F, new CubeDeformation(0.0F))
		.texOffs(90, 76).addBox(0.0F, -8.0F, 3.0F, 0.0F, 3.0F, 14.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 21.0F));

		PartDefinition tail = segment3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(58, 76).addBox(0.0F, -9.0F, 2.0F, 0.0F, 7.0F, 16.0F, new CubeDeformation(0.1F))
		.texOffs(68, 0).addBox(-3.5F, -2.0F, 0.0F, 7.0F, 4.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 21.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 55).addBox(-5.5F, -4.0F, -18.0F, 11.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-7.5F, 0.0F, -20.0F, 15.0F, 3.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 3.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(70, 51).addBox(-7.5F, 0.0F, 0.0F, 15.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -20.0F, -0.384F, 0.0F, 0.0F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 77).mirror().addBox(0.0F, 0.0F, -10.0F, 0.0F, 2.0F, 17.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.5F, 2.0F, -10.0F, 0.0F, 0.0F, 0.384F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 77).addBox(0.0F, 0.0F, -10.0F, 0.0F, 2.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.5F, 2.0F, -10.0F, 0.0F, 0.0F, -0.384F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(58, 55).addBox(-5.5F, 0.0F, -18.0F, 11.0F, 3.0F, 18.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 3.0F, 0.0F));

		PartDefinition cube_r4 = jaw.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(19, 110).addBox(-4.5F, -2.0F, 0.0F, 9.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -17.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r5 = jaw.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(14, 90).mirror().addBox(0.0F, -2.0F, -9.0F, 0.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.5F, 0.0F, -8.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition cube_r6 = jaw.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(14, 90).addBox(0.0F, -2.0F, -9.0F, 0.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 0.0F, -8.0F, 0.0F, 0.0F, 0.3927F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(ZipperlingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		headPitch = Mth.clamp(headPitch, -2.0F, 2.0F) * Mth.DEG_TO_RAD;
		netHeadYaw = Mth.clamp(netHeadYaw, -2.0F, 2.0F) * Mth.DEG_TO_RAD;
		if (entity.isInFluidType() || !entity.onGround()) {
			this.root.xRot += entity.getPitch(partialTicks) * Mth.DEG_TO_RAD;
		}
		this.head.xRot += headPitch;
		this.head.yRot += netHeadYaw;
		this.animateSmooth(entity.beachedAnimationState, ZipperlingAnimation.BEACHED, ageInTicks, partialTicks, 0.5F);
		this.animateSmooth(entity.idleAnimationState, ZippermouthAnimation.IDLE, ageInTicks, partialTicks, 0.5F);
		this.animate(entity.biteAnimationState, ZipperlingAnimation.BITE, ageInTicks,1F);
		this.animateWalk(ZippermouthAnimation.SWIM, limbSwing, limbSwingAmount, 1.0F, 2.5F);

		for (int i = 0; i < this.segmentParts.length; i++) {
			this.segmentParts[i].yRot += entity.segmentHelper.getYawAtIndex(i, partialTicks) * 0.25F;
			this.segmentParts[i].xRot += entity.segmentHelper.getPitchAtIndex(i, partialTicks) * 0.15F;
		}
	}
}