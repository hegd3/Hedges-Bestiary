package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RandomlySitGoal extends Goal {

    private final HBTamableAnimal mob;
    private final int cooldown;
    private final int maxTicksSitting;


    private int ticksSitting;
    private int ticksTillSit;

    public RandomlySitGoal(HBTamableAnimal mob) {
        this(mob, 500, 500);
    }

    public RandomlySitGoal(HBTamableAnimal mob, int cooldown, int maxTicksSitting) {
        this.mob = mob;
        this.cooldown = cooldown;
        this.maxTicksSitting = maxTicksSitting;
        this.ticksTillSit = this.cooldown + this.mob.getRandom().nextInt(this.cooldown);
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setInSittingPose(true);
        this.mob.setSitting(true);
        this.ticksSitting = 0;
    }

    @Override
    public boolean canContinueToUse() {

        if (this.mob.getCommand() != 0  || this.mob.isVehicle()) {
            return false;
        }
        else if (this.mob.isInFluidType() || !this.mob.onGround())
            return false;
        return this.ticksSitting <  this.maxTicksSitting;
    }

    @Override
    public boolean canUse() {
        if (this.mob.getCommand() != 0 || this.mob.isVehicle()) {
            return false;
        }
        else if (this.mob.isInFluidType() || !this.mob.onGround())
            return false;

        if (this.mob.isSitting()) {
            return true;
        }
        this.ticksTillSit = Math.max(this.ticksTillSit - 1, 0);
        return this.ticksTillSit == 0;
    }

    @Override
    public void tick() {
        this.ticksSitting++;
    }

    @Override
    public void stop() {
        this.mob.setInSittingPose(false);
        if (this.mob.isSitting() && this.mob.getCommand() != 1) {
            this.mob.setSitting(false);
        }
        this.ticksTillSit = this.cooldown + this.mob.getRandom().nextInt(this.cooldown);

    }
}
