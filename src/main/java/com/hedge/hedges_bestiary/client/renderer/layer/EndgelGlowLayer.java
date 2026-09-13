package com.hedge.hedges_bestiary.client.renderer.layer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.models.EndgelModel;
import com.hedge.hedges_bestiary.client.renderer.EndgelRenderer;
import com.hedge.hedges_bestiary.entity.living.EndgelEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class EndgelGlowLayer extends RenderLayer<EndgelEntity, EndgelModel> {

    private static final RenderType texture = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/endgel/endgel_glow.png"));

    public EndgelGlowLayer(EndgelRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, EndgelEntity livingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        VertexConsumer vertexconsumer = pBuffer.getBuffer(texture);
        this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY);

    }

}
