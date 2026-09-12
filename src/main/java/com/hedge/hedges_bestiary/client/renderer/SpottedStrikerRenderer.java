package com.hedge.hedges_bestiary.client.renderer;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.SpottedStrikerModel;
import com.hedge.hedges_bestiary.entity.living.SpottedStrikerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SpottedStrikerRenderer extends MobRenderer<SpottedStrikerEntity, SpottedStrikerModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/spotted_striker.png");
    public SpottedStrikerRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SpottedStrikerModel(pContext.bakeLayer(EntityLayers.SPOTTED_STRIKER_LAYER)), 1.0f);
    }

    @Override
    public void render(SpottedStrikerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isInvisible()) return;
        this.shadowRadius = entity.getCloakProgress(partialTicks) > 0.0F ? 0.0F : 1.0F;
        float alpha = 1.0F - entity.getCloakProgress(partialTicks);
        if (alpha > 0) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    protected @Nullable RenderType getRenderType(SpottedStrikerEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (entity.getCloakProgress(1.0f) > 0.0F) {
            return RenderType.entityTranslucent(TEXTURE);
        }
        return super.getRenderType(entity, bodyVisible, translucent, glowing);
    }

    @Override
    protected void scale(SpottedStrikerEntity entity, PoseStack pPoseStack, float partialTicks) {
        super.scale(entity, pPoseStack, partialTicks);
        float alpha = 1.0F - entity.getCloakProgress(partialTicks);
        this.model.setAlpha(alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(SpottedStrikerEntity pEntity) {
        return TEXTURE;
    }
}
