package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.ZippermouthModel;
import com.hedge.hedges_bestiary.entity.living.ZippermouthEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class ZippermouthRenderer extends MobRenderer<ZippermouthEntity, ZippermouthModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zippermouth/zippermouth.png");
    private static final RenderType EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zippermouth/zippermouth_eyes.png"));

    public ZippermouthRenderer(EntityRendererProvider.Context context) {
        super(context, new ZippermouthModel(context.bakeLayer(EntityLayers.ZIPPERMOUTH_LAYER)), 2.0F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return EYES;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(ZippermouthEntity entity) {
        return TEXTURE;
    }
}
