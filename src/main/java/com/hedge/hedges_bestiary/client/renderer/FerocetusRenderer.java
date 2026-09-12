package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.FerocetusModel;
import com.hedge.hedges_bestiary.client.renderer.layer.RiderLayer;
import com.hedge.hedges_bestiary.entity.living.FerocetusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class FerocetusRenderer extends MobRenderer<FerocetusEntity, FerocetusModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/ferocetus.png");

    public FerocetusRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new FerocetusModel(pContext.bakeLayer(EntityLayers.FEROCETUS_LAYER)), 1.2f);
        this.addLayer(new FerocetusMountLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(FerocetusEntity pEntity) {
        return TEXTURE;
    }

    private static class FerocetusMountLayer extends RiderLayer<FerocetusEntity, FerocetusModel> {

        public FerocetusMountLayer(FerocetusRenderer pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, FerocetusEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
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
                    poseStack.translate(0, passenger.getBbHeight() / -3f, 0.05f);
                    poseStack.mulPose(Axis.XN.rotationDegrees(180F));
                    poseStack.mulPose(Axis.YN.rotationDegrees(360F - bodyYaw));

                    renderPassenger(passenger, 0, 0, 0, 0, partialTicks, poseStack, bufferIn, packedLightIn);
                    poseStack.popPose();
                    HedgesBestiary.PROXY.blockRenderingEntity(passenger.getUUID());
                }
            }
            if (entity.isGrabbing()) {
                Entity grabbed = entity.getGrabbedEntity();
                if (grabbed == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                    return;
                }
                HedgesBestiary.PROXY.releaseRenderingEntity(grabbed.getUUID());
                poseStack.pushPose();
                this.getParentModel().root().translateAndRotate(poseStack);
                this.getParentModel().swimcontrol.translateAndRotate(poseStack);
                this.getParentModel().jaw.translateAndRotate(poseStack);
                poseStack.translate(0, grabbed.getBbHeight() * -0.2F, entity.getBbWidth() * -0.4F);
                if (grabbed.getBbHeight() > grabbed.getBbWidth() * 1.25F) {
                    poseStack.translate(grabbed.getBbHeight() * 0.5F, 0, 0);
                    poseStack.mulPose(Axis.ZN.rotationDegrees(90F));
                }
                poseStack.mulPose(Axis.XN.rotationDegrees(180F));
                renderPassenger(grabbed, 0, 0, 0, 0, partialTicks, poseStack, bufferIn, packedLightIn);
                poseStack.popPose();
                HedgesBestiary.PROXY.blockRenderingEntity(grabbed.getUUID());

            }
        }
    }
}
