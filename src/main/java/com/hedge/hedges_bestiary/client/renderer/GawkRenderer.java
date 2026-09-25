package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.GawkModel;
import com.hedge.hedges_bestiary.entity.living.GawkEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GawkRenderer extends MobRenderer<GawkEntity, GawkModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/gawk/gawk.png");

    public GawkRenderer(EntityRendererProvider.Context context) {
        super(context, new GawkModel(context.bakeLayer(EntityLayers.GAWK_LAYER)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(GawkEntity gawkEntity) {
        return TEXTURE;
    }
}
