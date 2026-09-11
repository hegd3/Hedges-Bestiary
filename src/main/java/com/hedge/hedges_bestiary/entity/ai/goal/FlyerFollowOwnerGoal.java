package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.TamableFlyer;

public class FlyerFollowOwnerGoal extends HBFollowOwnerGoal {
    private final TamableFlyer flyer;
    public FlyerFollowOwnerGoal(TamableFlyer mob, double speedModifier, double sprintSpeedModifier, float startDistance, float stopDistance) {
        super(mob, speedModifier, sprintSpeedModifier, startDistance, stopDistance, true, false);
        this.flyer = mob;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(owner, 10.0F, (float) mob.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.mob.distanceToSqr(owner) >= 256.0D) {
                this.teleportToOwner();
            } else if (this.mob.distanceToSqr(owner) >= 64.0D) {
                if (!this.flyer.isFlying()) {
                    this.flyer.setFlying(true);
                }
                this.mob.getNavigation().moveTo(owner, sprintSpeedModifier);
            } else {
                if (this.flyer.isFlying()) {
                    this.flyer.setFlying(false);
                }
                this.mob.getNavigation().moveTo(owner, speedModifier);
            }
        }
    }
}
