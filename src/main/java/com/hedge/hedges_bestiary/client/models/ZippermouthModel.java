package com.hedge.hedges_bestiary.client.models;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.animations.ZippermouthAnimation;
import com.hedge.hedges_bestiary.entity.living.ZippermouthEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ZippermouthModel extends HBModel<ZippermouthEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.ZIPPERMOUTH_LAYER;
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart segment;
	private final ModelPart leftfin;
	private final ModelPart leftfinrot;
	private final ModelPart rightfin;
	private final ModelPart rightfinrot;
	private final ModelPart segment2;
	private final ModelPart segment3;
	private final ModelPart tail;
	private final ModelPart[] segmentParts;

	public ZippermouthModel(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.segment = this.root.getChild("segment");
		this.leftfin = this.segment.getChild("leftfin");
		this.leftfinrot = this.leftfin.getChild("leftfinrot");
		this.rightfin = this.segment.getChild("rightfin");
		this.rightfinrot = this.rightfin.getChild("rightfinrot");
		this.segment2 = this.segment.getChild("segment2");
		this.segment3 = this.segment2.getChild("segment3");
		this.tail = this.segment3.getChild("tail");
		this.segmentParts = new ModelPart[]{this.segment, this.segment2, this.segment3, this.tail};
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 26.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(172, 141).addBox(-20.5F, -7.0F, -52.0F, 41.0F, 10.0F, 52.0F, new CubeDeformation(0.0F))
		.texOffs(0, 83).addBox(-23.5F, 3.0F, -55.0F, 47.0F, 3.0F, 55.0F, new CubeDeformation(0.0F))
		.texOffs(284, 203).addBox(-9.5F, -10.0F, -34.0F, 19.0F, 3.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition plane_r1 = head.addOrReplaceChild("plane_r1", CubeListBuilder.create().texOffs(114, 227).mirror().addBox(0.0F, -5.0F, -11.0F, 0.0F, 5.0F, 26.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-9.5F, -10.0F, -20.0F, 0.0F, 0.0F, -0.5672F));

		PartDefinition plane_r2 = head.addOrReplaceChild("plane_r2", CubeListBuilder.create().texOffs(114, 227).addBox(0.0F, -5.0F, -11.0F, 0.0F, 5.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.5F, -10.0F, -20.0F, 0.0F, 0.0F, 0.5672F));

		PartDefinition teeth_r1 = head.addOrReplaceChild("teeth_r1", CubeListBuilder.create().texOffs(284, 270).addBox(-23.5F, 0.0F, 0.0F, 47.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, -55.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition teeth_r2 = head.addOrReplaceChild("teeth_r2", CubeListBuilder.create().texOffs(114, 258).mirror().addBox(0.0F, 0.0F, -13.5F, 0.0F, 2.0F, 27.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-23.5F, 5.0F, -41.5F, 0.0F, 0.0F, 0.3491F));

		PartDefinition teeth_r3 = head.addOrReplaceChild("teeth_r3", CubeListBuilder.create().texOffs(114, 258).addBox(0.0F, 0.0F, -13.5F, 0.0F, 2.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(23.5F, 5.0F, -41.5F, 0.0F, 0.0F, -0.3491F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(188, 0).addBox(-20.5F, 0.0F, -52.0F, 41.0F, 6.0F, 52.0F, new CubeDeformation(0.01F))
		.texOffs(186, 285).addBox(-2.5F, 6.0F, -52.0F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(188, 58).addBox(-2.5F, 9.0F, -48.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition teeth_r4 = jaw.addOrReplaceChild("teeth_r4", CubeListBuilder.create().texOffs(18, 334).addBox(-18.5F, -2.0F, 0.0F, 37.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -51.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition teeth_r5 = jaw.addOrReplaceChild("teeth_r5", CubeListBuilder.create().texOffs(325, 74).mirror().addBox(0.0F, -2.0F, -13.5F, 0.0F, 2.0F, 27.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-18.5F, 0.0F, -35.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition teeth_r6 = jaw.addOrReplaceChild("teeth_r6", CubeListBuilder.create().texOffs(325, 74).addBox(0.0F, -2.0F, -13.5F, 0.0F, 2.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(18.5F, 0.0F, -35.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition segment = root.addOrReplaceChild("segment", CubeListBuilder.create().texOffs(0, 0).addBox(-18.5F, -19.0F, 0.0F, 37.0F, 26.0F, 57.0F, new CubeDeformation(0.02F))
		.texOffs(172, 203).addBox(0.0F, -45.0F, 1.0F, 0.0F, 26.0F, 56.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftfin = segment.addOrReplaceChild("leftfin", CubeListBuilder.create().texOffs(284, 272).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 18.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(12.0F, 7.0F, 5.0F));

		PartDefinition leftfinrot = leftfin.addOrReplaceChild("leftfinrot", CubeListBuilder.create().texOffs(284, 233).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 5.0F, 32.0F, new CubeDeformation(0.01F))
		.texOffs(168, 267).addBox(0.0F, -7.0F, 8.0F, 0.0F, 9.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 4.0F));

		PartDefinition rightfin = segment.addOrReplaceChild("rightfin", CubeListBuilder.create().texOffs(284, 272).mirror().addBox(-2.5F, 0.0F, -2.0F, 5.0F, 18.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-12.0F, 7.0F, 5.0F));

		PartDefinition rightfinrot = rightfin.addOrReplaceChild("rightfinrot", CubeListBuilder.create().texOffs(284, 233).mirror().addBox(-2.5F, 0.0F, 0.0F, 5.0F, 5.0F, 32.0F, new CubeDeformation(0.01F)).mirror(false)
		.texOffs(168, 267).mirror().addBox(0.0F, -7.0F, 8.0F, 0.0F, 9.0F, 27.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 13.0F, 4.0F));

		PartDefinition segment2 = segment.addOrReplaceChild("segment2", CubeListBuilder.create().texOffs(0, 227).addBox(0.0F, -19.0F, 0.0F, 0.0F, 6.0F, 57.0F, new CubeDeformation(0.01F))
		.texOffs(0, 0).addBox(-18.5F, -13.0F, 0.0F, 37.0F, 26.0F, 57.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -6.0F, 57.0F));

		PartDefinition segment3 = segment2.addOrReplaceChild("segment3", CubeListBuilder.create().texOffs(0, 227).addBox(0.0F, -19.0F, 0.0F, 0.0F, 6.0F, 57.0F, new CubeDeformation(0.01F))
		.texOffs(0, 0).addBox(-18.5F, -13.0F, 0.0F, 37.0F, 26.0F, 57.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 57.0F));

		PartDefinition tail = segment3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 141).addBox(-9.5F, -12.0F, 0.0F, 19.0F, 19.0F, 67.0F, new CubeDeformation(0.0F))
		.texOffs(204, 57).addBox(0.0F, -38.0F, 7.0F, 0.0F, 26.0F, 56.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 57.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}


	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(ZippermouthEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		headPitch = Mth.clamp(headPitch, -2.0F, 2.0F) * Mth.DEG_TO_RAD;
		netHeadYaw = Mth.clamp(netHeadYaw, -5.0F, 5.0F) * Mth.DEG_TO_RAD;

		if (entity.isInFluidType() || !entity.onGround()) {
			this.root.xRot += entity.getPitch(partialTicks) * Mth.DEG_TO_RAD;
		}
		this.head.xRot += headPitch;
		this.head.yRot += netHeadYaw;
		this.animateSmooth(entity.beachedAnimationState, ZippermouthAnimation.BEACHED, ageInTicks, partialTicks, 0.5F);
		this.animateSmooth(entity.idleAnimationState, ZippermouthAnimation.IDLE, ageInTicks, partialTicks, 0.5F);
		this.animateSmooth(entity.rushAnimationState, ZippermouthAnimation.OPEN_MOUTH, ageInTicks, partialTicks, 1F);
		this.animate(entity.biteAnimationState, ZippermouthAnimation.BITE, ageInTicks,1F);
		this.animateWalk(ZippermouthAnimation.SWIM, limbSwing, limbSwingAmount, 1.0F, 2.5F);

		for (int i = 0; i < this.segmentParts.length; i++) {
			this.segmentParts[i].yRot += entity.segmentHelper.getYawAtIndex(i, partialTicks) * 0.25F;
			this.segmentParts[i].xRot += entity.segmentHelper.getPitchAtIndex(i, partialTicks) * 0.15F;
		}
	}
}