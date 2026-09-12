package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.renderer.layer.EndgelGlowLayer;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.EndgelModel;
import com.hedge.hedges_bestiary.entity.living.EndgelEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EndgelRenderer extends MobRenderer<EndgelEntity, EndgelModel>  {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/endgel/endgel.png");

    public EndgelRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new EndgelModel(pContext.bakeLayer(EntityLayers.BANSHEE_LAYER)), 1.8f);
        this.addLayer(new EndgelGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(EndgelEntity pEntity) {
        return TEXTURE;
    }
}
