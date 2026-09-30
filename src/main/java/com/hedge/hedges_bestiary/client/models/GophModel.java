package com.hedge.hedges_bestiary.client.models;

import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.animations.GophAnimation;
import com.hedge.hedges_bestiary.client.animations.GurkAnimation;
import com.hedge.hedges_bestiary.client.models.HBModel;
import com.hedge.hedges_bestiary.entity.living.GophEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class GophModel extends HBModel<GophEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.GOPH_LAYER;
	private final ModelPart root;
	private final ModelPart wholebody;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart head;
	private final ModelPart leftleg2;
	private final ModelPart rightleg2;
	private final ModelPart leftleg;
	private final ModelPart rightleg;

	public GophModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.wholebody = this.root.getChild("wholebody");
		this.body = this.wholebody.getChild("body");
		this.tail = this.wholebody.getChild("tail");
		this.head = this.wholebody.getChild("head");
		this.leftleg2 = this.root.getChild("leftleg2");
		this.rightleg2 = this.root.getChild("rightleg2");
		this.leftleg = this.root.getChild("leftleg");
		this.rightleg = this.root.getChild("rightleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition wholebody = root.addOrReplaceChild("wholebody", CubeListBuilder.create(), PartPose.offset(0.5F, 0.0F, 4.0F));

		PartDefinition body = wholebody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -7.0F, 11.0F, 6.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -4.0F));

		PartDefinition plane_r1 = body.addOrReplaceChild("plane_r1", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-4.0F, 0.0F, -7.0F, 4.0F, 0.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition plane_r2 = body.addOrReplaceChild("plane_r2", CubeListBuilder.create().texOffs(38, 0).addBox(0.0F, 0.0F, -7.0F, 4.0F, 0.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition plane_r3 = body.addOrReplaceChild("plane_r3", CubeListBuilder.create().texOffs(0, 20).mirror().addBox(0.0F, -4.0F, -7.0F, 0.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

		PartDefinition plane_r4 = body.addOrReplaceChild("plane_r4", CubeListBuilder.create().texOffs(0, 20).addBox(0.0F, -4.0F, -7.0F, 0.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

		PartDefinition tail = wholebody.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(28, 30).addBox(-2.5F, -3.0F, 0.0F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(28, 49).addBox(0.0F, -5.0F, 1.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -2.0F, 3.0F));

		PartDefinition head = wholebody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 38).addBox(-3.5F, -2.5F, -5.0F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(28, 20).addBox(-3.5F, -1.5F, -11.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 48).addBox(-1.5F, -4.5F, -11.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -2.5F, -11.0F));

		PartDefinition leftleg2 = root.addOrReplaceChild("leftleg2", CubeListBuilder.create().texOffs(14, 49).addBox(0.0F, 0.0F, -1.5F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 49).addBox(3.0F, 0.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(5.5F, -1.0F, 3.5F));

		PartDefinition rightleg2 = root.addOrReplaceChild("rightleg2", CubeListBuilder.create().texOffs(14, 49).mirror().addBox(-3.0F, 0.0F, -1.5F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(48, 49).mirror().addBox(-4.0F, 0.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-5.5F, -1.0F, 3.5F));

		PartDefinition leftleg = root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(24, 42).addBox(0.0F, 0.0F, -2.5F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(45, 42).addBox(3.0F, 0.0F, -3.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offset(5.5F, -2.0F, -3.5F));

		PartDefinition rightleg = root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(24, 42).mirror().addBox(-6.0F, -1.0F, -2.5F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(45, 42).mirror().addBox(-8.0F, -1.0F, -3.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-5.5F, -1.0F, -3.5F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(GophEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

		netHeadYaw = Mth.clamp(netHeadYaw, -25.0F, 25.0F) * ((float)Math.PI / 180F);
		headPitch = Mth.clamp(headPitch, -25.0F, 25.0F) * ((float)Math.PI / 180F);
		float partialTicks = ageInTicks - entity.tickCount;
		this.head.yRot = netHeadYaw;
		this.head.xRot = headPitch;
		this.animateWalk(GophAnimation.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.animate(entity.idleAnimationState, GophAnimation.IDLE, ageInTicks, 0.5f);
		this.animateSmooth(entity.sitAnimationState, GophAnimation.SIT, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.napAnimationState, GophAnimation.SLEEP, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.danceAnimationState, GophAnimation.DANCE, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.stretchAnimationState, GophAnimation.STRETCH, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.digAnimationState, GophAnimation.DIG, ageInTicks, partialTicks, 1.5f);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

}