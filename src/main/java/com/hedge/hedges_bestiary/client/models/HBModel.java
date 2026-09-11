package com.hedge.hedges_bestiary.client.models;

import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class HBModel<E extends Entity> extends HierarchicalModel<E> {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
    protected final float youngScaleFactor;
    protected final float bodyYOffset;

    public HBModel() {
        this.youngScaleFactor = 1.0f;
        this.bodyYOffset = 0.0F;
    }

    public HBModel(float youngScaleFactor, float bodyYOffset) {
        this.youngScaleFactor = youngScaleFactor;
        this.bodyYOffset = bodyYOffset;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        if (this.young) {
            poseStack.scale(this.youngScaleFactor, this.youngScaleFactor, this.youngScaleFactor);
            poseStack.translate(0.0F, this.bodyYOffset / 16.0F, 0.0F);
        }
        this.root().render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }


    protected void animateSmooth(SmoothAnimationState animationState, @NotNull AnimationDefinition definition, float ageInTicks, float speed) {
        animationState.animate(this, definition, ageInTicks, speed);
    }

    @Override
    protected void applyStatic(@NotNull AnimationDefinition definition) {
        KeyframeAnimations.animate(this, definition, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
    }
}
