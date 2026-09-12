package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.renderer.layer.TearacudaEyeLayer;
import com.hedge.hedges_bestiary.client.models.TearacudaModel;
import com.hedge.hedges_bestiary.entity.living.TearacudaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TearacudaRenderer extends MobRenderer<TearacudaEntity, TearacudaModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/tearacuda/tearacuda.png");

    public TearacudaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new TearacudaModel(pContext.bakeLayer(EntityLayers.TEARACUDA_LAYER)), 0.8f);
        this.addLayer(new TearacudaEyeLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(TearacudaEntity pEntity) {
        return TEXTURE;
    }
}
