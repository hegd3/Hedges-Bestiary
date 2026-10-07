package com.hedge.hedges_bestiary.entity.util;

import net.minecraft.util.Mth;

public class SegmentHelper {
    private final float[] yawTrail;
    private final float[] prevYawTrail;
    private final float[] pitchTrail;
    private final float[] prevPitchTrail;
    private final float lerpSpeed;

    public SegmentHelper(int transformations, float lerpSpeed) {
        this.yawTrail = new float[transformations];
        this.prevYawTrail = new float[transformations];
        this.pitchTrail = new float[transformations];
        this.prevPitchTrail = new float[transformations];
        this.lerpSpeed = lerpSpeed;
    }

    public void tick(float bodyYaw, float bodyPitch) {
        System.arraycopy(this.yawTrail, 0, this.prevYawTrail, 0, this.yawTrail.length);
        System.arraycopy(this.pitchTrail, 0, this.prevPitchTrail, 0, this.pitchTrail.length);

        for (int i = this.yawTrail.length - 1; i > 0; i--) {
            float currentLerpSpeed = this.lerpSpeed / i;
            this.yawTrail[i] = Mth.rotLerp(currentLerpSpeed, this.yawTrail[i], Mth.wrapDegrees((this.yawTrail[i - 1] - this.yawTrail[i]) * i));
            this.pitchTrail[i] = Mth.rotLerp(currentLerpSpeed, this.pitchTrail[i], this.pitchTrail[i - 1]);
        }
        this.yawTrail[0] = Mth.rotLerp(lerpSpeed, this.yawTrail[0], bodyYaw);
        this.pitchTrail[0] = Mth.rotLerp(lerpSpeed, this.pitchTrail[0], bodyPitch);

    }


    public float getYawAtIndex(int index, float partialTicks) {
        return Mth.rotLerp(partialTicks, this.prevYawTrail[index], this.yawTrail[index]);
    }
    public float getPitchAtIndex(int index, float partialTicks) {
        return Mth.rotLerp(partialTicks, this.prevPitchTrail[index], this.pitchTrail[index]);
    }
}
