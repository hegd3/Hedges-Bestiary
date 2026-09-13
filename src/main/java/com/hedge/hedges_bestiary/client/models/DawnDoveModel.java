package com.hedge.hedges_bestiary.client.models;


import com.hedge.hedges_bestiary.client.animations.GenericPosesAnimation;
import com.hedge.hedges_bestiary.client.animations.DawnDoveAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.DawnDoveEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;


public class DawnDoveModel extends HBModel<DawnDoveEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.DAWN_DOVE_LAYER;
	private final ModelPart root;
	public final ModelPart flycontrol;
	public final ModelPart body;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart leftwing;
	private final ModelPart leftwing2;
	private final ModelPart rightwing;
	private final ModelPart rightwing2;
	public final ModelPart leftleg;
	private final ModelPart rightleg;

	public DawnDoveModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.flycontrol = this.root.getChild("flycontrol");
		this.body = this.flycontrol.getChild("body");
		this.neck = this.body.getChild("neck");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.tail = this.body.getChild("tail");
		this.tail2 = this.tail.getChild("tail2");
		this.leftwing = this.body.getChild("leftwing");
		this.leftwing2 = this.leftwing.getChild("leftwing2");
		this.rightwing = this.body.getChild("rightwing");
		this.rightwing2 = this.rightwing.getChild("rightwing2");
		this.leftleg = this.flycontrol.getChild("leftleg");
		this.rightleg = this.flycontrol.getChild("rightleg");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition flycontrol = root.addOrReplaceChild("flycontrol", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 0.0F));

		PartDefinition body = flycontrol.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12.5F, -28.0F, -23.0F, 25.0F, 28.0F, 33.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 28.0F, 7.0F));

		PartDefinition spikes_r1 = body.addOrReplaceChild("spikes_r1", CubeListBuilder.create().texOffs(131, 173).mirror().addBox(0.0F, -6.0F, -13.0F, 0.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.5F, -28.0F, 4.0F, 0.0F, 0.0F, -0.6109F));

		PartDefinition spikes_r2 = body.addOrReplaceChild("spikes_r2", CubeListBuilder.create().texOffs(131, 173).addBox(0.0F, -6.0F, -13.0F, 0.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -28.0F, 4.0F, 0.0F, 0.0F, 0.6109F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(60, 113).addBox(-6.5F, -6.0F, -12.0F, 13.0F, 18.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -14.0F, -23.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(173, 5).addBox(-8.5F, -9.0F, -11.0F, 17.0F, 16.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(112, 117).addBox(-5.5F, -9.0F, -27.0F, 11.0F, 11.0F, 16.0F, new CubeDeformation(0.01F))
				.texOffs(161, 116).addBox(-5.5F, 2.0F, -27.0F, 11.0F, 2.0F, 9.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 4.0F, -7.0F));

		PartDefinition horn_r1 = head.addOrReplaceChild("horn_r1", CubeListBuilder.create().texOffs(42, 131).mirror().addBox(0.0F, -2.0F, -4.5F, 0.0F, 2.0F, 9.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-5.5F, -9.0F, -20.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition horn_r2 = head.addOrReplaceChild("horn_r2", CubeListBuilder.create().texOffs(42, 131).addBox(0.0F, -2.0F, -4.5F, 0.0F, 2.0F, 9.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(5.5F, -9.0F, -20.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(154, 92).addBox(-5.5F, 0.0F, -16.0F, 11.0F, 5.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -11.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(31, 172).addBox(-8.5F, -7.0F, 0.0F, 17.0F, 15.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 10.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(86, 86).addBox(-5.5F, -4.0F, 0.0F, 11.0F, 7.0F, 20.0F, new CubeDeformation(0.0F))
				.texOffs(110, 138).addBox(0.0F, -10.0F, 3.0F, 0.0F, 6.0F, 17.0F, new CubeDeformation(0.01F))
				.texOffs(80, 143).addBox(0.0F, 3.0F, 5.0F, 0.0F, 4.0F, 15.0F, new CubeDeformation(0.01F))
				.texOffs(42, 143).addBox(5.5F, 0.0F, 5.0F, 4.0F, 0.0F, 15.0F, new CubeDeformation(0.01F))
				.texOffs(42, 143).mirror().addBox(-9.5F, 0.0F, 5.0F, 4.0F, 0.0F, 15.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(0.0F, 3.0F, 17.0F));

		PartDefinition leftwing = body.addOrReplaceChild("leftwing", CubeListBuilder.create().texOffs(108, 61).addBox(0.0F, -3.0F, -5.0F, 26.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 86).addBox(0.0F, -1.0F, 4.0F, 26.0F, 0.0F, 17.0F, new CubeDeformation(0.01F))
				.texOffs(108, 76).addBox(2.0F, -3.0F, -8.0F, 22.0F, 0.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(12.5F, -11.0F, -16.0F));

		PartDefinition leftwing2 = leftwing.addOrReplaceChild("leftwing2", CubeListBuilder.create().texOffs(116, 44).addBox(0.0F, -2.0F, -5.0F, 20.0F, 4.0F, 7.0F, new CubeDeformation(0.01F))
				.texOffs(0, 61).addBox(0.0F, 0.0F, -5.0F, 29.0F, 0.0F, 25.0F, new CubeDeformation(0.01F)), PartPose.offset(26.0F, -1.0F, 0.0F));

		PartDefinition rightwing = body.addOrReplaceChild("rightwing", CubeListBuilder.create().texOffs(108, 61).mirror().addBox(-26.0F, -3.0F, -5.0F, 26.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 86).mirror().addBox(-26.0F, -1.0F, 4.0F, 26.0F, 0.0F, 17.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(108, 76).mirror().addBox(-24.0F, -3.0F, -8.0F, 22.0F, 0.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-12.5F, -11.0F, -16.0F));

		PartDefinition rightwing2 = rightwing.addOrReplaceChild("rightwing2", CubeListBuilder.create().texOffs(116, 44).mirror().addBox(-20.0F, -2.0F, -5.0F, 20.0F, 4.0F, 7.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(0, 61).mirror().addBox(-29.0F, 0.0F, -5.0F, 29.0F, 0.0F, 25.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-26.0F, -1.0F, 0.0F));

		PartDefinition leftleg = flycontrol.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(116, 19).addBox(-4.5F, 0.0F, -7.0F, 9.0F, 12.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(62, 107).addBox(0.5F, 9.0F, -11.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(62, 107).addBox(-3.5F, 9.0F, -11.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(53, 111).addBox(-3.5F, 9.0F, -9.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(53, 111).addBox(0.5F, 9.0F, -9.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(74, 105).addBox(-1.5F, 10.0F, 6.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 28.0F, 7.0F));

		PartDefinition rightleg = flycontrol.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(116, 19).mirror().addBox(-4.5F, 0.0F, -7.0F, 9.0F, 12.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(62, 107).mirror().addBox(-3.5F, 9.0F, -11.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(53, 111).mirror().addBox(-3.5F, 9.0F, -9.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(53, 111).mirror().addBox(0.5F, 9.0F, -9.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(74, 105).mirror().addBox(-1.5F, 10.0F, 6.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(62, 107).mirror().addBox(0.5F, 9.0F, -11.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 28.0F, 7.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(DawnDoveEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		netHeadYaw = Mth.clamp(netHeadYaw, -15.0F, 15.0F) * ((float)Math.PI / 180F);
		headPitch = Mth.clamp(headPitch, -8.0F, 8.0F) * ((float)Math.PI / 180F);;
		if (this.young) {
			this.applyStatic(GenericPosesAnimation.BABY_TRANSFORM_WITH_NECK);
		}
		this.animateSmooth(entity.idleAnimationState, DawnDoveAnimation.IDLE, ageInTicks, partialTicks, 0.5f);
		this.animateSmooth(entity.sitAnimationState, DawnDoveAnimation.SIT, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.danceAnimationState, DawnDoveAnimation.DANCE, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.flyUpAnimationState, DawnDoveAnimation.FLY_UP, ageInTicks, partialTicks, this.young? limbSwingAmount * 0.4f + 0.8f : limbSwingAmount * 0.25f + 0.6f);
		this.animateSmooth(entity.flyForwardAnimationState, DawnDoveAnimation.FLY_FORWARD, ageInTicks, partialTicks, 1);
		this.animateSmooth(entity.glideAnimationState, DawnDoveAnimation.GLIDE, ageInTicks, partialTicks, limbSwingAmount * 0.25f + 0.7f);
		this.animateSmooth(entity.napAnimationState, DawnDoveAnimation.SLEEP, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.clawAttackAnimationState, DawnDoveAnimation.CLAW_ATTACK, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.eatAnimationState, DawnDoveAnimation.BITE, ageInTicks, partialTicks, 2f);
		this.animate(entity.biteAnimationState, DawnDoveAnimation.BITE, ageInTicks);
		this.animate(entity.shootAnimationState, DawnDoveAnimation.SHOOT, ageInTicks);
		if (entity.isFlying()) {
			float flyProgress = entity.getFlyProgress(partialTicks);


			this.flycontrol.xRot = entity.getFlightPitch(partialTicks) / 57.295776F * flyProgress / 2;
			this.flycontrol.zRot = entity.getFlightRoll(partialTicks) / 57.295776F * flyProgress / 3;

		} else {
			this.animateWalk(DawnDoveAnimation.WALK, limbSwing, limbSwingAmount, 1.5f, 2.5f);
		}
		float tailYaw = entity.getTrailYaw(partialTicks);
		this.tail.yRot += Mth.lerp(0.3F, this.tail.yRot, tailYaw * 0.27F);
		this.tail2.yRot += Mth.lerp(0.3F, this.tail2.yRot, tailYaw * 0.23F);

		this.neck.yRot += netHeadYaw / 2;
		this.neck.xRot += headPitch / 2;
		this.head.yRot += netHeadYaw;
		this.head.xRot += headPitch;

	}

}