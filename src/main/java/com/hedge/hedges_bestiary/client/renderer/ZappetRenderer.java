package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.HBRenderTypes;
import com.hedge.hedges_bestiary.client.renderer.layer.ZappetGlowLayer;
import com.hedge.hedges_bestiary.client.models.ZappetModel;
import com.hedge.hedges_bestiary.entity.living.ZappetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ZappetRenderer extends MobRenderer<ZappetEntity, ZappetModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zappet/zappet.png");
    private static final ResourceLocation BEAM_0 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zappet/zappet_beam_0.png");
    private static final ResourceLocation BEAM_1 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zappet/zappet_beam_1.png");
    private static final ResourceLocation BEAM_2 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zappet/zappet_beam_2.png");


    public ZappetRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ZappetModel(pContext.bakeLayer(EntityLayers.ZAPPET_LAYER)), 0.7f);
        this.addLayer(new ZappetGlowLayer(this));
    }

    private static void vertex(VertexConsumer pConsumer, PoseStack.Pose pose, float pX, float pY, float pZ, int pRed, int pGreen, int pBlue, float pU, float pV) {
        pConsumer.addVertex(pose, pX, pY, pZ).setColor(pRed, pGreen, pBlue, 255).setUv(pU, pV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private Vec3 getPosition(LivingEntity pLivingEntity, double pYOffset, float pPartialTick) {
        double d0 = Mth.lerp(pPartialTick, pLivingEntity.xOld, pLivingEntity.getX());
        double d1 = Mth.lerp(pPartialTick, pLivingEntity.yOld, pLivingEntity.getY()) + pYOffset;
        double d2 = Mth.lerp(pPartialTick, pLivingEntity.zOld, pLivingEntity.getZ());
        return new Vec3(d0, d1, d2);
    }

    @Override
    public void render(ZappetEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        BlockPos beamPos = entity.getTargetedPos();
        if (beamPos != null) {
            float f = 1.0F;
            float f1 = (float) entity.level().getGameTime() + partialTicks;
            float f2 = -1.0F * (f1 * 0.15F % 1.0F);
            float f3 = 1.13F;
            poseStack.pushPose();
            poseStack.translate(0.0D, f3, 0.0D);
            Vec3 vector3d = Vec3.upFromBottomCenterOf(beamPos, 0.15F);
            Vec3 vector3d1 = this.getPosition(entity, f3, partialTicks);
            Vec3 vector3d2 = vector3d.subtract(vector3d1);
            float f4 = (float) (vector3d2.length());
            vector3d2 = vector3d2.normalize();
            float f5 = (float) Math.acos(vector3d2.y);
            float f6 = (float) Math.atan2(vector3d2.z, vector3d2.x);
            poseStack.mulPose(Axis.YP.rotationDegrees(((Mth.PI / 2F) - f6) * Mth.RAD_TO_DEG));
            poseStack.mulPose(Axis.XP.rotationDegrees(f5 * Mth.RAD_TO_DEG));
            int i = 1;
            float f7 = f1 * 0.05F * 1.5F;
            float f8 = 1F;
            int j = (int) (f8 * 179.0F);
            int k = (int) (f8 * 253.0F);
            int l = (int) (f8 * 255.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(0 + 2.3561945F) * 0.8F;
            float f12 = Mth.sin(0 + 2.3561945F) * 0.8F;
            float f13 = Mth.cos(0 + Mth.PI / 4) * 0.8F;
            float f14 = Mth.sin(0 + Mth.PI  / 4) * 0.8F;
            float f15 = Mth.cos(0 + 3.926991F) * 0.8F;
            float f16 = Mth.sin(0 + 3.926991F) * 0.8F;
            float f17 = Mth.cos(0 + 5.4977875F) * 0.8F;
            float f18 = Mth.sin(0 + 5.4977875F) * 0.8F;
            float f19 = Mth.cos(0 + Mth.PI) * 0.4F;
            float f20 = Mth.sin(0 + Mth.PI) * 0.4F;
            float f21 = Mth.cos(0 + 0.0F) * 0.4F;
            float f22 = Mth.sin(0 + 0.0F) * 0.4F;
            float f23 = Mth.cos(0 + (Mth.PI / 2F)) * 0.4F;
            float f24 = Mth.sin(0 + (Mth.PI / 2F)) * 0.4F;
            float f25 = Mth.cos(0 + (Mth.PI * 1.5F)) * 0.4F;
            float f26 = Mth.sin(0 + (Mth.PI * 1.5F)) * 0.4F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 0.5F + f29;
            VertexConsumer ivertexbuilder = buffer.getBuffer(HBRenderTypes.getBeam(this.getBeamTexture(entity)));
            PoseStack.Pose pose = poseStack.last();
            vertex(ivertexbuilder, pose, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(ivertexbuilder, pose, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(ivertexbuilder, pose, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(ivertexbuilder, pose, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(ivertexbuilder, pose, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(ivertexbuilder, pose, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(ivertexbuilder, pose, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(ivertexbuilder, pose, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (entity.tickCount % 4 > 1) {
                f31 = 0.5F;
            }

            vertex(ivertexbuilder, pose, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(ivertexbuilder, pose, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(ivertexbuilder, pose, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(ivertexbuilder, pose, f15, f4, f16, j, k, l, 0.5F, f31);
            poseStack.popPose();
        }
    }

    private ResourceLocation getBeamTexture(ZappetEntity entity) {
        int time = entity.tickCount / 2 % 3;
        return switch(time) {
            case 0 -> BEAM_0;
            case 1 -> BEAM_1;
            default -> BEAM_2;
        };
    }



    @Override
    public ResourceLocation getTextureLocation(ZappetEntity pEntity) {
        return TEXTURE;
    }
}
