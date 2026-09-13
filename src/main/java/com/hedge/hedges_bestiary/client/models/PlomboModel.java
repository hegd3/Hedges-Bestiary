package com.hedge.hedges_bestiary.client.models;// Made with Blockbench 5.0.7
import com.hedge.hedges_bestiary.client.animations.GenericPosesAnimation;
import com.hedge.hedges_bestiary.client.animations.PlomboAnimation;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.entity.living.PlomboEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class PlomboModel extends HBModel<PlomboEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = EntityLayers.PLOMBO_LAYER;
	private final ModelPart root;
	public final ModelPart bodyfrontlegs;
	public final ModelPart body;
	private final ModelPart mainbody;
	public final ModelPart body2;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart leftear;
	private final ModelPart rightear;
	private final ModelPart jaw;
	private final ModelPart leftarm;
	private final ModelPart leftarmpos;
	private final ModelPart rightarm;
	private final ModelPart rightarmpos;
	private final ModelPart leftleg;
	private final ModelPart leftlegpos;
	private final ModelPart rightleg;
	private final ModelPart rightlegpos;


	public PlomboModel(ModelPart root) {
		super(0.5f, 24);
		this.root = root.getChild("root");
		this.bodyfrontlegs = this.root.getChild("bodyfrontlegs");
		this.body = this.bodyfrontlegs.getChild("body");
		this.mainbody = this.body.getChild("mainbody");
		this.body2 = this.body.getChild("body2");
		this.head = this.body.getChild("head");
		this.nose = this.head.getChild("nose");
		this.leftear = this.head.getChild("leftear");
		this.rightear = this.head.getChild("rightear");
		this.jaw = this.head.getChild("jaw");
		this.leftarm = this.bodyfrontlegs.getChild("leftarm");
		this.leftarmpos = this.leftarm.getChild("leftarmpos");
		this.rightarm = this.bodyfrontlegs.getChild("rightarm");
		this.rightarmpos = this.rightarm.getChild("rightarmpos");
		this.leftleg = this.root.getChild("leftleg");
		this.leftlegpos = this.leftleg.getChild("leftlegpos");
		this.rightleg = this.root.getChild("rightleg");
		this.rightlegpos = this.rightleg.getChild("rightlegpos");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition bodyfrontlegs = root.addOrReplaceChild("bodyfrontlegs", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 13.0F));

		PartDefinition body = bodyfrontlegs.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 49).addBox(-12.5F, 11.0F, -18.0F, 25.0F, 3.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, -12.0F));

		PartDefinition mainbody = body.addOrReplaceChild("mainbody", CubeListBuilder.create().texOffs(0, 0).addBox(-12.5F, -26.0F, -11.0F, 25.0F, 26.0F, 23.0F, new CubeDeformation(0.0F))
				.texOffs(6, 198).addBox(9.5F, -28.0F, 1.0F, 7.0F, 16.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(6, 198).mirror().addBox(-16.5F, -28.0F, 1.0F, 7.0F, 16.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 11.0F, -7.0F));

		PartDefinition body2 = body.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 75).addBox(-11.5F, -13.0F, 0.0F, 23.0F, 21.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(128, 106).addBox(0.0F, 2.0F, 14.0F, 0.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(48, 124).addBox(0.0F, -15.0F, 2.0F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(96, 0).addBox(-11.5F, 8.0F, 0.0F, 23.0F, 6.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 5.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(96, 41).addBox(-9.5F, -6.0F, -11.0F, 19.0F, 10.0F, 11.0F, new CubeDeformation(0.01F))
				.texOffs(0, 124).addBox(-9.5F, 0.0F, -16.0F, 19.0F, 4.0F, 5.0F, new CubeDeformation(0.01F))
				.texOffs(58, 116).addBox(-9.5F, 4.0F, -16.0F, 19.0F, 3.0F, 11.0F, new CubeDeformation(0.01F))
				.texOffs(128, 73).addBox(-5.5F, -9.0F, -9.0F, 11.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -18.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(96, 62).addBox(-9.5F, -6.0F, -2.5F, 19.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -13.5F));

		PartDefinition leftear = head.addOrReplaceChild("leftear", CubeListBuilder.create().texOffs(128, 92).addBox(0.0F, -3.0F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -9.0F, -4.0F));

		PartDefinition rightear = head.addOrReplaceChild("rightear", CubeListBuilder.create().texOffs(128, 92).mirror().addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.5F, -9.0F, -4.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(96, 20).addBox(-9.5F, 0.0F, -16.0F, 19.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 149).addBox(-9.5F, 5.0F, -12.0F, 19.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition teeth_r1 = jaw.addOrReplaceChild("teeth_r1", CubeListBuilder.create().texOffs(128, 99).mirror().addBox(0.0F, -4.0F, -1.5F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.02F)).mirror(false), PartPose.offsetAndRotation(-9.5F, 0.0F, -2.5F, 0.0F, 0.0F, -0.2618F));

		PartDefinition teeth_r2 = jaw.addOrReplaceChild("teeth_r2", CubeListBuilder.create().texOffs(128, 99).addBox(0.0F, -4.0F, -1.5F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(9.5F, 0.0F, -2.5F, 0.0F, 0.0F, 0.2618F));

		PartDefinition leftarm = bodyfrontlegs.addOrReplaceChild("leftarm", CubeListBuilder.create(), PartPose.offset(8.0F, -14.5F, -20.0F));

		PartDefinition leftarmpos = leftarm.addOrReplaceChild("leftarmpos", CubeListBuilder.create().texOffs(74, 75).addBox(-6.5F, -0.5F, -7.0F, 13.0F, 27.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(128, 85).addBox(2.5F, 23.5F, -11.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(143, 88).addBox(2.5F, 25.5F, -11.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(143, 88).addBox(-1.5F, 25.5F, -11.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(143, 88).addBox(-5.5F, 25.5F, -11.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(128, 85).addBox(-1.5F, 23.5F, -11.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(128, 85).addBox(-5.5F, 23.5F, -11.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightarm = bodyfrontlegs.addOrReplaceChild("rightarm", CubeListBuilder.create(), PartPose.offset(-8.0F, -14.5F, -20.0F));

		PartDefinition rightarmpos = rightarm.addOrReplaceChild("rightarmpos", CubeListBuilder.create().texOffs(143, 88).mirror().addBox(2.5F, 25.5F, -11.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(143, 88).mirror().addBox(-1.5F, 25.5F, -11.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(143, 88).mirror().addBox(-5.5F, 25.5F, -11.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(74, 75).mirror().addBox(-6.5F, -0.5F, -7.0F, 13.0F, 27.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(128, 85).mirror().addBox(-5.5F, 23.5F, -11.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(128, 85).mirror().addBox(-1.5F, 23.5F, -11.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(128, 85).mirror().addBox(2.5F, 23.5F, -11.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftleg = root.addOrReplaceChild("leftleg", CubeListBuilder.create(), PartPose.offset(5.5F, 8.0F, 14.0F));

		PartDefinition leftlegpos = leftleg.addOrReplaceChild("leftlegpos", CubeListBuilder.create().texOffs(120, 114).addBox(-5.0F, 0.0F, -7.0F, 10.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightleg = root.addOrReplaceChild("rightleg", CubeListBuilder.create(), PartPose.offset(-5.5F, 8.0F, 14.0F));

		PartDefinition rightlegpos = rightleg.addOrReplaceChild("rightlegpos", CubeListBuilder.create().texOffs(120, 114).mirror().addBox(-5.0F, 0.0F, -7.0F, 10.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(PlomboEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float partialTicks = ageInTicks - entity.tickCount;
		if (this.young) {
			this.applyStatic(GenericPosesAnimation.BABY_TRANSFORM);
		}
		this.animate(entity.idleAnimationState, PlomboAnimation.IDLE, ageInTicks, 0.5f);
		this.animateWalk(PlomboAnimation.WALK, limbSwing, limbSwingAmount, 2.5f, 1.5f);
		this.animate(entity.swipeAnimationState, entity.swingingLeft() ? PlomboAnimation.SWIPE_LEFT : PlomboAnimation.SWIPE_RIGHT, ageInTicks);
		this.animate(entity.multiAttackAnimationState, entity.swingingLeft() ? PlomboAnimation.MULTIATTACK_LEFT : PlomboAnimation.MULTIATTACK_RIGHT, ageInTicks);

		this.animateSmooth(entity.napAnimationState, PlomboAnimation.SLEEP, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.danceAnimationState, PlomboAnimation.DANCE, ageInTicks, partialTicks, 1f);
		this.animateSmooth(entity.sitAnimationState, PlomboAnimation.SIT, ageInTicks, partialTicks,0.5f);
		this.animateSmooth(entity.scratchAnimationState, PlomboAnimation.SCRATCH, ageInTicks, partialTicks,1f);

		this.animateSmooth(entity.sniffAnimationState, PlomboAnimation.SNIFF, ageInTicks, partialTicks,1f);
		this.animateSmooth(entity.yawnAnimationState, PlomboAnimation.YAWN, ageInTicks, partialTicks,1f);
		this.animateSmooth(entity.earflickAnimationState, entity.swingingLeft() ? PlomboAnimation.EAR_FLICK_LEFT : PlomboAnimation.EAR_FLICK_RIGHT, ageInTicks, partialTicks, 1f);

		this.head.yRot += Mth.clamp(netHeadYaw, -25.0F, 25.0F) * ((float)Math.PI / 180F);
		this.head.xRot += Mth.clamp(headPitch, -5.0F, 25.0F) * ((float)Math.PI / 180F);

	}
}