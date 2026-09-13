package com.hedge.hedges_bestiary.client.renderer.layer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.HBRenderTypes;
import com.hedge.hedges_bestiary.client.models.ZappetModel;
import com.hedge.hedges_bestiary.client.renderer.ZappetRenderer;
import com.hedge.hedges_bestiary.entity.living.ZappetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class ZappetGlowLayer extends RenderLayer<ZappetEntity, ZappetModel> {

    private static final RenderType TEXTURE = HBRenderTypes.getEyesAlphaEnabled(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zappet/zappet_glow.png"));

    public ZappetGlowLayer(ZappetRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ZappetEntity entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        if (entity.isInvisible()) return;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(TEXTURE);
        float alpha = entity.getGlowProgress(pPartialTicks);
        int i = FastColor.ARGB32.color(Mth.floor(alpha * 255.0F), 255, 255, 255);

        this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, 1, OverlayTexture.NO_OVERLAY, i);

    }




}
