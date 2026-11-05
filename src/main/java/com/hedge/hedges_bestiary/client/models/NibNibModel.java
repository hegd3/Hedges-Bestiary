package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.animations.NibNibAnimation;
import com.hedge.hedges_bestiary.client.models.HBModel;
import com.hedge.hedges_bestiary.entity.living.NibNibEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class NibNibModel extends HBModel<NibNibEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.NIB_NIB_LAYER;
	private final ModelPart root;
	private final ModelPart siphon;
	private final ModelPart jaw;
	private final ModelPart lefttentacle;
	private final ModelPart righttentacle;

	public NibNibModel(ModelPart root) {
		this.root = root.getChild("root");
		this.siphon = this.root.getChild("siphon");
		this.jaw = this.root.getChild("jaw");
		this.lefttentacle = this.jaw.getChild("lefttentacle");
		this.righttentacle = this.jaw.getChild("righttentacle");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(36, 35).addBox(-4.5F, -3.0F, 5.0F, 9.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.5F, -6.0F, -6.0F, 9.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, -2.0F));

		PartDefinition siphon = root.addOrReplaceChild("siphon", CubeListBuilder.create().texOffs(0, 22).addBox(-2.5F, -1.0F, -1.0F, 5.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -5.0F));

		PartDefinition jaw = root.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(37, 23).addBox(-5.0F, -5.5F, 0.0F, 11.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(30, 38).addBox(6.0F, -5.5F, 2.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(30, 38).mirror().addBox(-5.0F, -5.5F, 2.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(1, 39).addBox(-5.0F, -5.5F, 2.0F, 11.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 43).addBox(-5.0F, 4.5F, 2.0F, 11.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 1.5F, 8.0F));

		PartDefinition lefttentacle = jaw.addOrReplaceChild("lefttentacle", CubeListBuilder.create().texOffs(28, 47).addBox(0.0F, -0.5F, 0.0F, 0.0F, 3.0F, 10.0F, new CubeDeformation(0.01F)), PartPose.offset(6.0F, 2.0F, 2.0F));

		PartDefinition righttentacle = jaw.addOrReplaceChild("righttentacle", CubeListBuilder.create().texOffs(28, 47).mirror().addBox(0.0F, -0.5F, 0.0F, 0.0F, 3.0F, 10.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-5.0F, 2.0F, 2.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(NibNibEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.root.xRot += Mth.clamp(headPitch, -45.0F, 45.0F) * Mth.DEG_TO_RAD;
		this.animate(entity.idleAnimationState, NibNibAnimation.IDLE, ageInTicks, 0.7F + limbSwingAmount * 1.2F);
		this.animate(entity.attackAnimationState, NibNibAnimation.ATTACK, ageInTicks, 1F);
	}
}