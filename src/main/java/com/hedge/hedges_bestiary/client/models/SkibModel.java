package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.animations.SkibAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.ambientfish.SkibEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SkibModel extends HBModel<SkibEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.SKIB_LAYER;
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart leftleg;
	private final ModelPart rightleg;
	private final ModelPart leftleg2;
	private final ModelPart rightleg2;
	private final ModelPart leftleg3;
	private final ModelPart rightleg3;

	public SkibModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.tail = this.body.getChild("tail");
		this.leftleg = this.root.getChild("leftleg");
		this.rightleg = this.root.getChild("rightleg");
		this.leftleg2 = this.root.getChild("leftleg2");
		this.rightleg2 = this.root.getChild("rightleg2");
		this.leftleg3 = this.root.getChild("leftleg3");
		this.rightleg3 = this.root.getChild("rightleg3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -3.0F, -7.0F, 11.0F, 3.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -1.0F));

		PartDefinition spikes_r1 = body.addOrReplaceChild("spikes_r1", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(-3.0F, 0.0F, -5.5F, 3.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.5F, -3.0F, -0.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition spikes_r2 = body.addOrReplaceChild("spikes_r2", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, 0.0F, -5.5F, 3.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -3.0F, -0.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition spikes_r3 = body.addOrReplaceChild("spikes_r3", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(-3.0F, 0.0F, -5.5F, 3.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.5F, -3.0F, -0.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition spikes_r4 = body.addOrReplaceChild("spikes_r4", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, 0.0F, -5.5F, 3.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -3.0F, -0.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(28, 23).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 6.0F));

		PartDefinition leftleg = root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(10, 37).addBox(0.0F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -2.0F, -4.5F));

		PartDefinition rightleg = root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(10, 37).mirror().addBox(-3.0F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.5F, -2.0F, -4.5F));

		PartDefinition leftleg2 = root.addOrReplaceChild("leftleg2", CubeListBuilder.create().texOffs(10, 37).addBox(0.0F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -2.0F, -1.5F));

		PartDefinition rightleg2 = root.addOrReplaceChild("rightleg2", CubeListBuilder.create().texOffs(10, 37).mirror().addBox(-3.0F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.5F, -2.0F, -1.5F));

		PartDefinition leftleg3 = root.addOrReplaceChild("leftleg3", CubeListBuilder.create().texOffs(10, 37).addBox(0.0F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -2.0F, 1.5F));

		PartDefinition rightleg3 = root.addOrReplaceChild("rightleg3", CubeListBuilder.create().texOffs(10, 37).mirror().addBox(-3.0F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.5F, -2.0F, 1.5F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(SkibEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		this.animate(entity.idleAnimationState, SkibAnimation.IDLE, ageInTicks, 0.25f);
		this.animateWalk(SkibAnimation.WALK, limbSwing, limbSwingAmount, 2.5f, 2.5f);
		this.animateSmooth(entity.scratchAnimationState, SkibAnimation.SCRATCH, ageInTicks, partialTicks, 1.5f);
		this.animateSmooth(entity.hideAnimationState, SkibAnimation.HIDE, ageInTicks, partialTicks, 1f);

	}
}