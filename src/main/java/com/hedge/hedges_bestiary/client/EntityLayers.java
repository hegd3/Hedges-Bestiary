package com.hedge.hedges_bestiary.client;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class EntityLayers {
    public static final ModelLayerLocation BURODON_LAYER = main("burodon");
    public static final ModelLayerLocation SPOTTED_STRIKER_LAYER = main("spotted_striker");
    public static final ModelLayerLocation PLOMBO_LAYER = main("plombo");
    public static final ModelLayerLocation GURK_LAYER= main("gurk");
    public static final ModelLayerLocation MURK_LAYER = main("murk");
    public static final ModelLayerLocation TEARACUDA_LAYER = main("tearacuda");
    public static final ModelLayerLocation ZAPPET_LAYER = main("zappet");
    public static final ModelLayerLocation GILD_GLIDER_LAYER = main("gild_glider");
    public static final ModelLayerLocation CHUB_LAYER = main("chub");
    public static final ModelLayerLocation FEROCETUS_LAYER = main("ferocetus");
    public static final ModelLayerLocation WAVE_LAYER = main("wave");
    public static final ModelLayerLocation BANSHEE_LAYER = main("banshee");
    public static final ModelLayerLocation DAWN_DOVE_LAYER = main("dawn_dove");
    public static final ModelLayerLocation SKIB_LAYER = main("skib");

    public static final ModelLayerLocation GENERIC_PROJECTILE_LAYER = main("generic_projectile");

    private static ModelLayerLocation main(String id) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, id), "main");
    }
}
