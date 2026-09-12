package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.ChubModel;
import com.hedge.hedges_bestiary.entity.living.ambientfish.ChubEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChubRenderer extends MobRenderer<ChubEntity, ChubModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/chub.png");

    public ChubRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ChubModel(pContext.bakeLayer(EntityLayers.CHUB_LAYER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(ChubEntity pEntity) {
        return TEXTURE;
    }
}
