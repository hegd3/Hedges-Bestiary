package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class NapGoal extends Goal {

    private final HBTamableAnimal mob;
    private final boolean sleepsInWater;
    private int napCD;

    public NapGoal(HBTamableAnimal mob) {
        this(mob, false);
    }

    public NapGoal(HBTamableAnimal mob, boolean sleepsInWater) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.mob = mob;
        this.sleepsInWater = sleepsInWater;
        this.resetNapCD();
    }

    @Override
    public boolean canUse() {
        if (this.mob.isNapping()) {
            return true;
        }
        if (!this.sleepsInWater && (this.mob.isInFluidType() || !this.mob.onGround())) {
            return false;
        }
        this.napCD = Math.max(this.napCD - 1, 0);
        return this.napCD == 0 && this.canSleep();
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setNapping(true);
        this.resetNapCD();
    }

    @Override
    public void stop() {
        this.mob.setNapping(false);
        if (this.mob.isOrderedToSit() && !this.mob.isSitting()) {
            this.mob.setSitting(true);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.mob.isNapping()) {
            return false;
        }
        if (!this.sleepsInWater && this.mob.isInFluidType()) {
            return false;
        }
        if (this.mob.hasControllingPassenger()) {
            return false;
        }
        this.napCD = Math.max(this.napCD - 1, 0);
        if (this.napCD == 0) {
            return this.canSleep();
        }
        return true;
    }

    private boolean canSleep() {
        this.resetNapCD();
        long dayTime = this.mob.level().getDayTime() % 24000;
        return this.mob.getSleepType().canSleep(dayTime);
    }

    private void resetNapCD() {
        this.napCD = 60 + this.mob.getRandom().nextInt(120);
    }

}
