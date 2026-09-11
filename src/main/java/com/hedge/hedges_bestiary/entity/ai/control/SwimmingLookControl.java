package com.hedge.hedges_bestiary.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class SwimmingLookControl extends LookControl {

    private final int maxYRotFromCenterWater;
    private final int maxYRotFromCenterLand;

    public SwimmingLookControl(Mob pMob, int pMaxYRotFromCenterWater, int pMaxYRotFromCenterLand) {
        super(pMob);
        this.maxYRotFromCenterWater = pMaxYRotFromCenterWater;
        this.maxYRotFromCenterLand = pMaxYRotFromCenterLand;
    }

    public void tick() {
        if (this.lookAtCooldown > 0) {
            --this.lookAtCooldown;
            this.getYRotD().ifPresent((p_287449_) -> {
                this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, p_287449_ + 20.0F, this.yMaxRotSpeed);
            });
            this.getXRotD().ifPresent((p_289401_) -> {
                this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), p_289401_ + 10.0F, this.xMaxRotAngle));
            });
        } else {
            if (this.mob.getNavigation().isDone()) {
                this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), 0.0F, 5.0F));
            }

            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
        }

        float f = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
        if (this.mob.isInWaterOrBubble()) {
            if (f < (float) (-this.maxYRotFromCenterWater)) {
                this.mob.yBodyRot -= 4.0F;
            } else if (f > (float) this.maxYRotFromCenterWater) {
                this.mob.yBodyRot += 4.0F;
            }
        } else {
            if (f < (float) (-this.maxYRotFromCenterLand)) {
                this.mob.yBodyRot -= 4.0F;
            } else if (f > (float) this.maxYRotFromCenterLand) {
                this.mob.yBodyRot += 4.0F;
            }
        }

    }
}
