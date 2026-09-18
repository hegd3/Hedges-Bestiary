package com.hedge.hedges_bestiary.client.renderer.layer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.models.DawnDoveModel;
import com.hedge.hedges_bestiary.client.models.MurkModel;
import com.hedge.hedges_bestiary.client.renderer.DawnDoveRenderer;
import com.hedge.hedges_bestiary.client.renderer.MurkRenderer;
import com.hedge.hedges_bestiary.entity.living.DawnDoveEntity;
import com.hedge.hedges_bestiary.entity.living.MurkEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class MurkRiderLayer extends RiderLayer<MurkEntity, MurkModel> {
    public MurkRiderLayer(MurkRenderer render) {
        super(render);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, MurkEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isVehicle()) {
            float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
            for (Entity passenger : entity.getPassengers()) {
                if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                    continue;
                }
                HedgesBestiary.PROXY.releaseRenderingEntity(passenger.getUUID());
                poseStack.pushPose();

                this.getParentModel().root().translateAndRotate(poseStack);
                this.getParentModel().swimcontrol.translateAndRotate(poseStack);
                this.getParentModel().wholebody.translateAndRotate(poseStack);
                poseStack.translate(0, passenger.getBbHeight() / -9f, 0.75f);
                poseStack.mulPose(Axis.XN.rotationDegrees(180F));
                poseStack.mulPose(Axis.YN.rotationDegrees(360F - bodyYaw));


                renderPassenger(passenger, 0, 0, 0, 0, partialTicks, poseStack, bufferIn, packedLightIn);
                poseStack.popPose();
                HedgesBestiary.PROXY.blockRenderingEntity(passenger.getUUID());
            }

        }
    }


}
