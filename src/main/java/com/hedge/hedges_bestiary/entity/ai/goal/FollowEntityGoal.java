package com.hedge.hedges_bestiary.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FollowEntityGoal<E extends LivingEntity> extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private final Class<E> toFollow;
    protected LivingEntity followTarget = null;
    private final Predicate<E> predicate;
    private final double maxDist;
    private final double stopDist;
    private int timeToRecalcPath;
    public FollowEntityGoal(PathfinderMob mob, double speedModifier, Class<E> toFollow, Predicate<E> predicate, double maxDist, double stopDist) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.toFollow = toFollow;
        this.predicate = predicate;
        this.maxDist = maxDist * maxDist;
        this.stopDist = stopDist * stopDist;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.followTarget != null) {
            if (!this.followTarget.isAlive()) {
                this.followTarget = null;
                return false;
            }
            return this.withinDist();
        } else if (this.mob.tickCount % 5 == 0) {
            List<E> potential = this.mob.level().getEntitiesOfClass(toFollow, mob.getBoundingBox().inflate(8), predicate);
            if (!potential.isEmpty()) {
                this.followTarget = potential.get(0);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.followTarget != null && this.followTarget.isAlive() && this.withinDist();
    }

    protected boolean withinDist() {
        double dist = this.mob.distanceToSqr(this.followTarget);
        return dist <= maxDist && dist > this.stopDist;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        if (this.followTarget != null && (!this.followTarget.isAlive() || this.mob.distanceToSqr(this.followTarget) > this.maxDist)) {
            this.followTarget = null;
        }
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.mob.getNavigation().moveTo(this.followTarget, this.speedModifier);
        }
    }

}
