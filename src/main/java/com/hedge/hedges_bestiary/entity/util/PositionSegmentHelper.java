package com.hedge.hedges_bestiary.entity.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class PositionSegmentHelper {


    private final Vec3[] prevPositions;
    private final float[] yawTrail;
    private final float[] prevYawTrail;
    private final float[] pitchTrail;
    private final float[] prevPitchTrail;
    private final float segmentLength;
    private final float lerpSpeed;
    private boolean initialized = false;
    // lol this shi doesnt work gonna have to try agian later
    public PositionSegmentHelper(int transformations, float segmentLength, float lerpSpeed) {
        this.yawTrail = new float[transformations];
        this.prevYawTrail = new float[transformations];
        this.pitchTrail = new float[transformations];
        this.prevPitchTrail = new float[transformations];
        this.prevPositions = new Vec3[transformations];
        this.segmentLength = segmentLength;
        this.lerpSpeed = lerpSpeed;
    }

    public void tick(Vec3 position, float bodyYaw, float bodyPitch) {
        if (!this.initialized) {
            Arrays.fill(prevPositions, position);
            this.initialized = true;
        }
        System.arraycopy(this.yawTrail, 0, this.prevYawTrail, 0, this.yawTrail.length);
        System.arraycopy(this.pitchTrail, 0, this.prevPitchTrail, 0, this.pitchTrail.length);

        Vec3 direction = position.subtract(prevPositions[0]);
        if (direction.horizontalDistance() > this.segmentLength) {
            prevPositions[0] = position.add(new Vec3(0, 0, -this.segmentLength).yRot(bodyYaw));
        }
        float yaw = Mth.wrapDegrees((float)(Mth.atan2(direction.z, direction.x)) * Mth.RAD_TO_DEG - 90.0F - bodyYaw);
        this.yawTrail[0] = Mth.rotLerp(lerpSpeed, this.yawTrail[0], yaw);
        this.pitchTrail[0] = Mth.rotLerp(lerpSpeed, this.pitchTrail[0], bodyPitch);


        for (int i = 1; i < this.yawTrail.length; i++) {
            direction = prevPositions[i - 1].subtract(prevPositions[i]);
            yaw = Mth.wrapDegrees( (float)(Mth.atan2(direction.z, direction.x)) * Mth.RAD_TO_DEG - 90.0F - this.yawTrail[i- 1]);
            this.yawTrail[i] = Mth.rotLerp(lerpSpeed, this.yawTrail[i], yaw);
            this.pitchTrail[i] = Mth.rotLerp(lerpSpeed, this.pitchTrail[i], this.pitchTrail[i - 1]);
            if (direction.length() > segmentLength) {
                prevPositions[i] = prevPositions[i - 1].add(new Vec3(0, 0, -this.segmentLength).yRot(yawTrail[i - 1]));
            }
        }
    }


    public float getYawAtIndex(int index, float partialTicks) {
        return Mth.rotLerp(partialTicks, this.prevYawTrail[index], this.yawTrail[index]) * Mth.DEG_TO_RAD;
    }
    public float getPitchAtIndex(int index, float partialTicks) {
        return Mth.rotLerp(partialTicks, this.prevPitchTrail[index], this.pitchTrail[index]) * Mth.DEG_TO_RAD;
    }
}
