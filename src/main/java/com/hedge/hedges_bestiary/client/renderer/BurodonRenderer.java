package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.BurodonModel;
import com.hedge.hedges_bestiary.entity.living.BurodonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BurodonRenderer extends MobRenderer<BurodonEntity, BurodonModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/burodon.png");

    public BurodonRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new BurodonModel(pContext.bakeLayer(EntityLayers.BURODON_LAYER)), 1.0f);
    }

    @Override
    public void render(BurodonEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BurodonEntity pEntity) {
        return TEXTURE;
    }
}
