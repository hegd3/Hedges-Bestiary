package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class HBSitWhenOrderedGoal extends Goal {
    private final boolean standsInWater;
    private final HBTamableAnimal mob;

    public HBSitWhenOrderedGoal(HBTamableAnimal mob) {
        this(mob, true);
    }

    public HBSitWhenOrderedGoal(HBTamableAnimal mob, boolean standsInWater) {
       this.mob = mob;
       this.standsInWater = standsInWater;
       this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.mob.isOrderedToSit();
    }

    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        } else if (this.standsInWater) {
            if (this.mob.isInFluidType() || !this.mob.onGround()) {
                return false;
            }
        }
        LivingEntity livingentity = this.mob.getOwner();
        if (livingentity == null) {
            return true;
        } else {
            return this.mob.isOrderedToSit(); //&& (!(this.mob.distanceToSqr(livingentity) < 144.0D) || livingentity.getLastHurtMob() == null);
        }
    }

    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setInSittingPose(true);
        this.mob.setSitting(true);
    }

    public void stop() {
        this.mob.setInSittingPose(false);
        this.mob.setSitting(false);
    }

}
