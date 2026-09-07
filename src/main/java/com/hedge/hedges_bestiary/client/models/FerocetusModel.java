package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hedge.hedges_bestiary.client.animations.FerocetusAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.FerocetusEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class FerocetusModel extends HBModel<FerocetusEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.FEROCETUS_LAYER;
	private final ModelPart root;
	public final ModelPart swimcontrol;
	public final ModelPart jaw;
	private final ModelPart leftfin;
	private final ModelPart rightfin;
	private final ModelPart fin;
	private final ModelPart tail;
	private final ModelPart tail2;

	public FerocetusModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.swimcontrol = this.root.getChild("swimcontrol");
		this.jaw = this.swimcontrol.getChild("jaw");
		this.leftfin = this.swimcontrol.getChild("leftfin");
		this.rightfin = this.swimcontrol.getChild("rightfin");
		this.fin = this.swimcontrol.getChild("fin");
		this.tail = this.swimcontrol.getChild("tail");
		this.tail2 = this.tail.getChild("tail2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition swimcontrol = root.addOrReplaceChild("swimcontrol", CubeListBuilder.create().texOffs(0, 0).addBox(-15.5F, -18.0F, -15.0F, 31.0F, 34.0F, 42.0F, new CubeDeformation(0.0F))
				.texOffs(195, 155).addBox(5.5F, -22.0F, -14.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(195, 155).mirror().addBox(-15.5F, -22.0F, -14.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 76).addBox(-15.5F, -18.0F, -38.0F, 31.0F, 26.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition jaw = swimcontrol.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(108, 105).addBox(-15.5F, 0.0F, -23.0F, 31.0F, 8.0F, 23.0F, new CubeDeformation(0.01F))
				.texOffs(146, 29).addBox(15.52F, -3.0F, -23.0F, 0.0F, 3.0F, 22.0F, new CubeDeformation(0.01F))
				.texOffs(146, 29).mirror().addBox(-15.52F, -3.0F, -23.0F, 0.0F, 3.0F, 22.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(146, 54).addBox(-15.5F, -3.0F, -23.0F, 31.0F, 3.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 8.0F, -15.0F));

		PartDefinition leftfin = swimcontrol.addOrReplaceChild("leftfin", CubeListBuilder.create().texOffs(76, 136).addBox(0.0F, -1.5F, -11.0F, 20.0F, 3.0F, 20.0F, new CubeDeformation(0.01F)), PartPose.offset(15.5F, 14.5F, 2.0F));

		PartDefinition rightfin = swimcontrol.addOrReplaceChild("rightfin", CubeListBuilder.create().texOffs(76, 136).mirror().addBox(-20.0F, -1.5F, -11.0F, 20.0F, 3.0F, 20.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-15.5F, 14.5F, 2.0F));

		PartDefinition fin = swimcontrol.addOrReplaceChild("fin", CubeListBuilder.create().texOffs(146, 0).addBox(-3.5F, -14.0F, 0.0F, 7.0F, 14.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 6.0F));

		PartDefinition tail = swimcontrol.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 146).addBox(-9.5F, -9.0F, 0.0F, 19.0F, 20.0F, 23.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 27.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(108, 76).addBox(-18.5F, -4.0F, 0.0F, 37.0F, 7.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 18.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}



	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(FerocetusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		headPitch = (Mth.clamp(headPitch, -25.0F, 25.0F)) * Mth.DEG_TO_RAD;
		float pitch = entity.getPitch(ageInTicks - entity.tickCount) * Mth.DEG_TO_RAD;
		float tailYaw = entity.getTrailYaw(ageInTicks - entity.tickCount);
		this.tail.yRot = Mth.lerp(0.3F, this.tail.yRot, tailYaw * 0.3F);
		this.tail.yRot = Mth.lerp(0.3F, this.tail.yRot, tailYaw * 0.25F);


		this.animateSmooth(entity.biteAnimationState, FerocetusAnimation.BITE, ageInTicks, 1f);
		this.animateSmooth(entity.ramAnimationState, FerocetusAnimation.RAM, ageInTicks, 1f);
		this.animateSmooth(entity.airAnimationState, FerocetusAnimation.AIR, ageInTicks, 1f);
		this.animateSmooth(entity.grabAnimationState, entity.isGrabbing() ? FerocetusAnimation.CHEW : FerocetusAnimation.GRAB, ageInTicks, 1f);
		this.animateSmooth(entity.grabbingAnimationState, FerocetusAnimation.GRABBING, ageInTicks, 1f);

		this.animate(entity.spinAnimationState, entity.swingingLeft() ? FerocetusAnimation.SPIN_LEFT : FerocetusAnimation.SPIN_RIGHT, ageInTicks, 1f);
		this.animateSmooth(entity.callAnimationState, FerocetusAnimation.CALL, ageInTicks, 1f);
		this.animateSmooth(entity.idleAnimationState, FerocetusAnimation.IDLE, ageInTicks, 0.5f);
		this.animateSmooth(entity.beachedAnimationState, FerocetusAnimation.BEACHED, ageInTicks, 0.5f);
		if (entity.isInFluidType()) {
			this.swimcontrol.xRot = headPitch + entity.getPitch(ageInTicks - entity.tickCount) * Mth.DEG_TO_RAD;
			this.swimcontrol.zRot = entity.roll;
			this.animateWalk(FerocetusAnimation.SWIM, limbSwing, limbSwingAmount, 1.3f, 1f);
		} else {
			if (entity.groundTimer == 0 ) {
				this.swimcontrol.xRot = pitch;
				this.tail.xRot += -pitch * 0.5f;
				this.tail2.xRot += -pitch * 0.7f;
			}
		}
	}
}