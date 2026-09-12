package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.GurkModel;
import com.hedge.hedges_bestiary.entity.living.GurkEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class GurkRenderer extends MobRenderer<GurkEntity, GurkModel> {

    private static final ResourceLocation GURK_SWAMP = generateTexture("swamp");
    private static final ResourceLocation GURK_RIVER = generateTexture("river");
    private static final ResourceLocation GURK_TROPICAL = generateTexture("tropical");
    private static final ResourceLocation GURK_SHINY = generateTexture("shiny");

    private static final ResourceLocation GURK_SWAMP_SLEEPING = generateTexture("swamp_sleeping");
    private static final ResourceLocation GURK_RIVER_SLEEPING = generateTexture("river_sleeping");
    private static final ResourceLocation GURK_TROPICAL_SLEEPING = generateTexture("tropical_sleeping");
    private static final ResourceLocation GURK_SHINY_SLEEPING = generateTexture("shiny_sleeping");

    public GurkRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new GurkModel(pContext.bakeLayer(EntityLayers.GURK_LAYER)), 0.8f);
    }

    @Override
    public ResourceLocation getTextureLocation(GurkEntity entity) {
        return switch (entity.getVariant()) {
            case 1 -> entity.isNapping() ? GURK_RIVER_SLEEPING : GURK_RIVER;
            case 2 -> entity.isNapping() ? GURK_TROPICAL_SLEEPING : GURK_TROPICAL;
            case 3 -> entity.isNapping() ? GURK_SHINY_SLEEPING : GURK_SHINY;
            default -> entity.isNapping() ? GURK_SWAMP_SLEEPING : GURK_SWAMP;
        };
    }

    private static ResourceLocation generateTexture(String var) {
        return ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/gurk/gurk_" + var + ".png");
    }

    @Override
    protected int getBlockLightLevel(GurkEntity entity, BlockPos pPos) {
        return entity.getVariant() == 3 ? 15 : super.getBlockLightLevel(entity, pPos);
    }
}
