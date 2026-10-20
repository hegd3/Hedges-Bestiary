package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.GophModel;
import com.hedge.hedges_bestiary.entity.living.GophEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GophRenderer extends MobRenderer<GophEntity, GophModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/goph/goph.png");
    private static final ResourceLocation SLEEP = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/goph/goph_sleep.png");


    public GophRenderer(EntityRendererProvider.Context context) {
        super(context, new GophModel(context.bakeLayer(EntityLayers.GOPH_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(GophEntity gophEntity) {
        return gophEntity.isNapping() ? SLEEP : TEXTURE;
    }
}
