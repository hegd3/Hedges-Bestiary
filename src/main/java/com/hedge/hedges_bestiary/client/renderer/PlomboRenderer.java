package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.PlomboModel;
import com.hedge.hedges_bestiary.client.renderer.layer.PlomboRiderLayer;
import com.hedge.hedges_bestiary.entity.living.PlomboEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PlomboRenderer extends MobRenderer<PlomboEntity, PlomboModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/plombo/plombo.png");
    private static final ResourceLocation SLEEPING = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/plombo/plombo_sleeping.png");
    private static final ResourceLocation TEXTURE_BARRELED = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/plombo/plombo_barreled.png");
    private static final ResourceLocation SLEEPING_BARRELED = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/plombo/plombo_barreled_sleeping.png");

    public PlomboRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new PlomboModel(pContext.bakeLayer(EntityLayers.PLOMBO_LAYER)), 1.5f);
        this.addLayer(new PlomboRiderLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PlomboEntity pEntity) {
        if (pEntity.hasBarrel()) {
            return pEntity.isNapping() ? SLEEPING_BARRELED : TEXTURE_BARRELED;

        }
        return pEntity.isNapping() ? SLEEPING : TEXTURE;
    }
}
