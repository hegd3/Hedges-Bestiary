package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.HBRenderTypes;
import com.hedge.hedges_bestiary.client.models.SkibModel;
import com.hedge.hedges_bestiary.entity.living.ambientfish.SkibEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkibRenderer extends MobRenderer<SkibEntity, SkibModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/skib/skib.png");
    private static final RenderType GLOW = HBRenderTypes.getEyesAlphaEnabled(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/skib/skib_glow.png"));

    public SkibRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SkibModel(pContext.bakeLayer(EntityLayers.SKIB_LAYER)), 0.6f);
        this.addLayer(new SkibGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SkibEntity entity) {
        return TEXTURE;
    }

    private static class SkibGlowLayer extends RenderLayer<SkibEntity, SkibModel> {

        public SkibGlowLayer(SkibRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, SkibEntity entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (entity.isInvisible()) return;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(GLOW);
            float alpha = Mth.sin(entity.glowProgress * Mth.PI);
            this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, 1, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, alpha);

        }
    }
}
