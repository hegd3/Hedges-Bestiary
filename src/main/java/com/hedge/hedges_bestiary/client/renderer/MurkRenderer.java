package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.renderer.layer.MurkRiderLayer;
import com.hedge.hedges_bestiary.client.models.MurkModel;
import com.hedge.hedges_bestiary.entity.living.MurkEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MurkRenderer extends MobRenderer<MurkEntity, MurkModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/murk/murk.png");
    private static final ResourceLocation SLEEP = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/murk/murk_sleep.png");


    public MurkRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MurkModel(pContext.bakeLayer(EntityLayers.MURK_LAYER)), 2.0f);
        this.addLayer(new MurkEyeLayer(this));
        this.addLayer(new MurkRiderLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MurkEntity pEntity) {
        return pEntity.isNapping() ? SLEEP : TEXTURE;
    }

    static class MurkEyeLayer extends EyesLayer<MurkEntity, MurkModel> {

        private static final RenderType EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/murk/murk_eyes.png"));
        private static final RenderType CHARGE = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/murk/murk_charge.png"));

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, MurkEntity entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (!entity.isInvisible() && !entity.isNapping()) {
                VertexConsumer vertexconsumer = pBuffer.getBuffer(entity.isCharged() ? CHARGE : renderType());
                this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        public MurkEyeLayer(RenderLayerParent<MurkEntity, MurkModel> pRenderer) {
            super(pRenderer);
        }

        @Override
        public RenderType renderType() {
            return EYES;
        }
    }
}
