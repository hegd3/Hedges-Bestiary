package com.hedge.hedges_bestiary.client.models;


import com.hedge.hedges_bestiary.client.animations.SpottedStrikerAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.SpottedStrikerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class SpottedStrikerModel extends HBModel<SpottedStrikerEntity> {

	private float alpha = 1.0F;

	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.SPOTTED_STRIKER_LAYER;
	private final ModelPart root;
	private final ModelPart swimcontrol;
	private final ModelPart leftfin;
	private final ModelPart rightfin;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart tail;
	private final ModelPart leftfin2;
	private final ModelPart rightfin2;
	private final ModelPart tail2;
	private final ModelPart tail3;

	public SpottedStrikerModel(ModelPart root) {
		this.root = root.getChild("root");
		this.swimcontrol = this.root.getChild("swimcontrol");
		this.leftfin = this.swimcontrol.getChild("leftfin");
		this.rightfin = this.swimcontrol.getChild("rightfin");
		this.head = this.swimcontrol.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.tail = this.swimcontrol.getChild("tail");
		this.leftfin2 = this.tail.getChild("leftfin2");
		this.rightfin2 = this.tail.getChild("rightfin2");
		this.tail2 = this.tail.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition swimcontrol = root.addOrReplaceChild("swimcontrol", CubeListBuilder.create().texOffs(0, 0).addBox(-12.5F, -17.0F, -13.0F, 25.0F, 17.0F, 22.0F, new CubeDeformation(0.0F))
				.texOffs(100, 101).addBox(0.0F, -23.0F, -1.0F, 0.0F, 6.0F, 10.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftfin = swimcontrol.addOrReplaceChild("leftfin", CubeListBuilder.create().texOffs(94, 0).addBox(0.0F, -1.0F, -5.5F, 13.0F, 2.0F, 11.0F, new CubeDeformation(0.01F))
				.texOffs(94, 31).addBox(0.0F, 0.0F, 5.5F, 13.0F, 0.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(12.5F, -1.0F, -5.5F));

		PartDefinition rightfin = swimcontrol.addOrReplaceChild("rightfin", CubeListBuilder.create().texOffs(94, 0).mirror().addBox(-13.0F, -1.0F, -5.5F, 13.0F, 2.0F, 11.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(94, 31).mirror().addBox(-13.0F, 0.0F, 5.5F, 13.0F, 0.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-12.5F, -1.0F, -5.5F));

		PartDefinition head = swimcontrol.addOrReplaceChild("head", CubeListBuilder.create().texOffs(115, 38).addBox(-9.5F, -6.5F, -16.0F, 19.0F, 8.0F, 16.0F, new CubeDeformation(0.01F))
				.texOffs(94, 13).addBox(9.51F, 1.5F, -16.0F, 0.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(94, 13).mirror().addBox(-9.51F, 1.5F, -16.0F, 0.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(94, 30).addBox(-9.5F, 1.5F, -16.0F, 19.0F, 1.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -8.5F, -13.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(94, 34).addBox(-7.5F, -1.0F, -15.0F, 15.0F, 1.0F, 0.0F, new CubeDeformation(0.01F))
				.texOffs(98, 84).addBox(7.5F, -1.0F, -15.0F, 0.0F, 1.0F, 14.0F, new CubeDeformation(0.01F))
				.texOffs(98, 84).mirror().addBox(-7.5F, -1.0F, -15.0F, 0.0F, 1.0F, 14.0F, new CubeDeformation(0.01F)).mirror(false)
				.texOffs(46, 62).addBox(-9.5F, 0.0F, -16.0F, 19.0F, 5.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));

		PartDefinition tail = swimcontrol.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(46, 84).addBox(-7.5F, -6.5F, 0.0F, 15.0F, 13.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, 9.0F));

		PartDefinition leftfin2 = tail.addOrReplaceChild("leftfin2", CubeListBuilder.create().texOffs(55, 118).addBox(0.0F, 0.0F, -3.5F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(3.5F, 6.5F, 3.5F));

		PartDefinition rightfin2 = tail.addOrReplaceChild("rightfin2", CubeListBuilder.create().texOffs(55, 118).mirror().addBox(0.0F, 1.0F, -3.5F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-3.5F, 5.5F, 3.5F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(70, 106).addBox(0.0F, 4.5F, 2.0F, 0.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(40, 108).addBox(0.0F, -9.5F, 2.0F, 0.0F, 4.0F, 10.0F, new CubeDeformation(0.01F))
				.texOffs(0, 92).addBox(-4.5F, -5.5F, 0.0F, 9.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 11.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(5, 52).addBox(0.0F, -5.5F, 0.0F, 0.0F, 18.0F, 18.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -9.0F, 12.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(SpottedStrikerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		headPitch = Mth.clamp(headPitch, -15.0F, 15.0F) * ((float) Math.PI / 180F);
		netHeadYaw = Mth.clamp(netHeadYaw, -15.0F, 15.0F) * ((float)Math.PI / 180F);

		this.animateSmooth(entity.idleAnimationState, SpottedStrikerAnimation.IDLE, ageInTicks, partialTicks, 0.6f);
		this.animateSmooth(entity.beachedAnimationState, SpottedStrikerAnimation.BEACHED, ageInTicks, partialTicks, 0.6f);
		this.animateSmooth(entity.danceAnimationState, SpottedStrikerAnimation.DANCE, ageInTicks, partialTicks, 1f);
		this.animate(entity.biteAnimationState, SpottedStrikerAnimation.BITE, ageInTicks, 1f);
		this.animate(entity.superBiteAnimationState, SpottedStrikerAnimation.SUPERBITE, ageInTicks, 1f);


		if (entity.isInFluidType()) {
			this.swimcontrol.xRot = headPitch;
			this.swimcontrol.zRot = entity.roll * 0.8f;
			this.animateWalk(SpottedStrikerAnimation.SWIM, limbSwing, limbSwingAmount, 1.7f, 2.5f);
		}

		float tailYaw = entity.getTrailYaw(ageInTicks - entity.tickCount);
		this.tail.yRot = Mth.lerp(0.3F, this.tail.yRot, tailYaw * 0.2F);
		this.tail2.yRot = Mth.lerp(0.3F, this.tail2.yRot, tailYaw * 0.25F);
		this.tail3.yRot = Mth.lerp(0.3F, this.tail3.yRot, tailYaw * 0.25F);
		this.head.yRot += netHeadYaw;

	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer buffer, int pPackedLight, int pPackedOverlay, int color) {
		int i = FastColor.ARGB32.color((int)(255 * this.alpha), 255, 255, 255);

		this.root().render(pPoseStack, buffer.setColor(1f, 1f, 1f, this.alpha * 255), pPackedLight, pPackedOverlay, i);
	}

}