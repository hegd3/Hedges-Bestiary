package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.ZippermouthModel;
import com.hedge.hedges_bestiary.client.renderer.layer.RiderLayer;
import com.hedge.hedges_bestiary.entity.living.ZippermouthEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ZippermouthRenderer extends MobRenderer<ZippermouthEntity, ZippermouthModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zippermouth/zippermouth.png");
    private static final RenderType EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zippermouth/zippermouth_eyes.png"));

    public ZippermouthRenderer(EntityRendererProvider.Context context) {
        super(context, new ZippermouthModel(context.bakeLayer(EntityLayers.ZIPPERMOUTH_LAYER)), 2.0F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return EYES;
            }
        });
        this.addLayer(new ZippermouthRiderLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ZippermouthEntity entity) {
        return TEXTURE;
    }

    private static class ZippermouthRiderLayer extends RiderLayer<ZippermouthEntity, ZippermouthModel> {

        public ZippermouthRiderLayer(RenderLayerParent<ZippermouthEntity, ZippermouthModel> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, ZippermouthEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.isVehicle()) {
                float bodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks;
                for (Entity passenger : entity.getPassengers()) {
                    if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                        continue;
                    }
                    HedgesBestiary.PROXY.releaseRenderingEntity(passenger.getUUID());
                    poseStack.pushPose();
                    this.getParentModel().root().translateAndRotate(poseStack);
                    this.getParentModel().segment.translateAndRotate(poseStack);
                    poseStack.translate(0.8, passenger.getBbHeight() / -3f, 0.5F);
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
