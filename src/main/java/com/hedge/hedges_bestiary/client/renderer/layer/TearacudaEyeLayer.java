package com.hedge.hedges_bestiary.client.renderer.layer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.models.TearacudaModel;
import com.hedge.hedges_bestiary.client.renderer.TearacudaRenderer;
import com.hedge.hedges_bestiary.entity.living.TearacudaEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class TearacudaEyeLayer extends EyesLayer<TearacudaEntity, TearacudaModel> {
    private static final RenderType texture = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/tearacuda/tearacuda_eyes.png"));

    public TearacudaEyeLayer(TearacudaRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public RenderType renderType() {
        return texture;
    }
}
