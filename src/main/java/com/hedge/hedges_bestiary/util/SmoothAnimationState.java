package com.hedge.hedges_bestiary.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class SmoothAnimationState extends AnimationState {
    // source https://github.com/Peeko32213/OpposingForce/blob/main/src/main/java/com/barl_inc/opposing_force/utils/SmoothAnimationState.java
    public static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public float factorOld;
    public float factor;
    public final float lerpSpeed;

    public SmoothAnimationState(float lerpSpeed) {
        this.lerpSpeed = lerpSpeed;
    }

    public SmoothAnimationState() {
        this(0.5F);
    }

    @Override
    public void animateWhen(boolean condition, int tickCount) {
        float target = condition ? 1.0F : 0.0F;
        this.factorOld = this.factor;
        this.factor = Mth.clamp(Mth.lerp(this.lerpSpeed, this.factor, target), 0.0F, 1.0F);
        if (condition) {
            this.startIfStopped(tickCount);
        } else {
            this.stop();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float factor() {
        return Mth.lerp(Minecraft.getInstance().getPartialTick(), this.factorOld, this.factor);
    }

    @OnlyIn(Dist.CLIENT)
    public void animate(HierarchicalModel<?> model, AnimationDefinition definition, float ageInTicks) {
        this.animate(model, definition, ageInTicks, this.factor(), 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public void animate(HierarchicalModel<?> model, AnimationDefinition definition, float ageInTicks, float speed) {
        this.animate(model, definition, ageInTicks, this.factor(), speed);
    }

    @OnlyIn(Dist.CLIENT)
    public void animate(HierarchicalModel<?> model, AnimationDefinition definition, float ageInTicks, float factor, float speed) {
        this.updateTime(ageInTicks, speed);
        KeyframeAnimations.animate(model, definition, this.getAccumulatedTime(), factor, ANIMATION_VECTOR_CACHE);
    }

}
