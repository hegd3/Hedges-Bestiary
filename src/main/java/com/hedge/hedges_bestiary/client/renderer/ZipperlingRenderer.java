package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.ZipperlingModel;
import com.hedge.hedges_bestiary.entity.living.ZipperlingEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class ZipperlingRenderer extends MobRenderer<ZipperlingEntity, ZipperlingModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zippermouth/zipperling.png");
    private static final RenderType EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/zippermouth/zipperling_eyes.png"));

    public ZipperlingRenderer(EntityRendererProvider.Context context) {
        super(context, new ZipperlingModel(context.bakeLayer(EntityLayers.ZIPPERLING_LAYER)), 1.2F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return EYES;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(ZipperlingEntity entity) {
        return TEXTURE;
    }
}
