package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.BlurpumModel;
import com.hedge.hedges_bestiary.client.renderer.layer.RiderLayer;
import com.hedge.hedges_bestiary.entity.living.BlurpumEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BlurpumRenderer extends MobRenderer<BlurpumEntity, BlurpumModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/blurpum.png");

    public BlurpumRenderer(EntityRendererProvider.Context context) {
        super(context, new BlurpumModel(context.bakeLayer(EntityLayers.BLURPUM_LAYER)), 1.0F);
        this.addLayer(new BlurpumRiderLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BlurpumEntity blurpumEntity) {
        return TEXTURE;
    }

    private static class BlurpumRiderLayer extends RiderLayer<BlurpumEntity, BlurpumModel> {

        public BlurpumRiderLayer(RenderLayerParent<BlurpumEntity, BlurpumModel> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, BlurpumEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.isVehicle()) {
                float bodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks;
                for (Entity passenger : entity.getPassengers()) {
                    if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                        continue;
                    }
                    HedgesBestiary.PROXY.releaseRenderingEntity(passenger.getUUID());
                    poseStack.pushPose();
                    this.getParentModel().root().translateAndRotate(poseStack);
                    this.getParentModel().swimcontrol.translateAndRotate(poseStack);
                    this.getParentModel().wholebody.translateAndRotate(poseStack);
                    this.getParentModel().body.translateAndRotate(poseStack);

                    poseStack.translate(0, passenger.getBbHeight() / -8f, 0.05f);
                    poseStack.mulPose(Axis.XN.rotationDegrees(180F));
                    poseStack.mulPose(Axis.YN.rotationDegrees(360F - bodyYaw));

                    renderPassenger(passenger, 0, 0, 0, 0, partialTicks, poseStack, bufferIn, packedLightIn);
                    poseStack.popPose();
                    HedgesBestiary.PROXY.blockRenderingEntity(passenger.getUUID());
                }
            }

        }
    }
}
