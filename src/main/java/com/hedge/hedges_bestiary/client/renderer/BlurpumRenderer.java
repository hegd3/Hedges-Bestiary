package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.BlurpumModel;
import com.hedge.hedges_bestiary.entity.living.BlurpumEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlurpumRenderer extends MobRenderer<BlurpumEntity, BlurpumModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/blurpum.png");

    public BlurpumRenderer(EntityRendererProvider.Context context) {
        super(context, new BlurpumModel(context.bakeLayer(EntityLayers.BLURPUM_LAYER)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(BlurpumEntity blurpumEntity) {
        return TEXTURE;
    }


}
