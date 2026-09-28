package com.hedge.hedges_bestiary.entity.util;

import net.minecraft.util.Mth;

public class SegmentHelper {
    private final float[] yawTrail;
    private final float[] prevYawTrail;
    private final float[] pitchTrail;
    private final float[] prevPitchTrail;

    public SegmentHelper(int transformations) {
        this.yawTrail = new float[transformations];
        this.prevYawTrail = new float[transformations];
        this.pitchTrail = new float[transformations];
        this.prevPitchTrail = new float[transformations];
    }

    public void tick(float bodyYaw, float bodyPitch) {
        System.arraycopy(this.yawTrail, 0, this.prevYawTrail, 0, this.yawTrail.length);
        System.arraycopy(this.pitchTrail, 0, this.prevPitchTrail, 0, this.pitchTrail.length);
        this.yawTrail[0] = Mth.rotLerp(0.2F, this.yawTrail[0], bodyYaw);
        this.pitchTrail[0] = Mth.rotLerp(0.2F, this.pitchTrail[0], bodyPitch);
        for (int i = 1; i < this.yawTrail.length; i++) {
            this.yawTrail[i] = Mth.rotLerp(0.2F, this.yawTrail[i], this.yawTrail[i - 1]);
            this.pitchTrail[i] = Mth.rotLerp(0.2F, this.pitchTrail[i], this.pitchTrail[i - 1]);
        }
    }


    public float getYawAtIndex(int index, float partialTicks) {
        return Mth.rotLerp(partialTicks, this.prevYawTrail[index], this.yawTrail[index]);
    }
    public float getPitchAtIndex(int index, float partialTicks) {
        return Mth.rotLerp(partialTicks, this.prevPitchTrail[index], this.pitchTrail[index]);
    }
}
