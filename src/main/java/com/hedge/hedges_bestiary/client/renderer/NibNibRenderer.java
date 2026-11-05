package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.NibNibModel;
import com.hedge.hedges_bestiary.entity.living.NibNibEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NibNibRenderer extends MobRenderer<NibNibEntity, NibNibModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/nib_nib.png");

    public NibNibRenderer(EntityRendererProvider.Context context) {
        super(context, new NibNibModel(context.bakeLayer(EntityLayers.NIB_NIB_LAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(NibNibEntity nibNibEntity) {
        return TEXTURE;
    }
}
