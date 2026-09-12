package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.GildGliderModel;
import com.hedge.hedges_bestiary.entity.living.ambientfish.GildGliderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class GildGliderRenderer extends MobRenderer<GildGliderEntity, GildGliderModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/gild_glider.png");


    public GildGliderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new GildGliderModel(pContext.bakeLayer(EntityLayers.GILD_GLIDER_LAYER)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(GildGliderEntity pEntity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(GildGliderEntity pEntity, BlockPos pPos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(GildGliderEntity pEntity, BlockPos pPos) {
        return 15;
    }
}
