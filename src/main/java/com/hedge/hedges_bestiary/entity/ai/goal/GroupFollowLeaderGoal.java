package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBGroupMob;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class GroupFollowLeaderGoal<E extends PathfinderMob & HBGroupMob<E>> extends Goal{
    protected final E mob;
    private final float stopRange;
    private final float startRange;
    private int timeToRecalcPath;
    private int nextStartTick;

    public GroupFollowLeaderGoal(E mob) {
        this(mob, 8, 0);
    }

    public GroupFollowLeaderGoal(E mob, float startRange, float stopRange) {
        this.mob = mob;
        this.nextStartTick = this.nextStartTick(mob);
        this.startRange = startRange * startRange;
        this.stopRange = stopRange * stopRange;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    protected int nextStartTick(E pTaskOwner) {
        return reducedTickDelay(200 + pTaskOwner.getRandom().nextInt(200) % 20);
    }

    public boolean canUse() {
        if (this.mob.canNeverFollow()) {
            return false;
        } else if (this.mob.hasFollowers()) {
            return false;
        } else if (this.mob.isFollower()) {
            return this.mob.distanceToSqr(this.mob.getLeader()) >= this.startRange;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            Predicate<E> predicate = (p_25258_) -> {
                return p_25258_.canBeFollowed() || !p_25258_.isFollower();
            };
            List<E> list = this.mob.level().getEntitiesOfClass((Class<E>) this.mob.getClass(), this.mob.getBoundingBox().inflate(10.0D, 10.0D, 10.0D), predicate);
            E schoolingMob = DataFixUtils.orElse(list.stream().filter(E::canBeFollowed).findAny(), this.mob);
            schoolingMob.addFollowers(list.stream().filter((p_25255_) -> {
                return !p_25255_.isFollower();
            }));
            return this.mob.isFollower() && this.mob.distanceToSqr(this.mob.getLeader()) >= this.startRange;
        }
    }

    public boolean canContinueToUse() {
        if (this.mob.canNeverFollow()) {
            return false;
        }
        if (!this.mob.isFollower()) {
            return false;
        }

        return this.stopRange == 0 || this.mob.distanceToSqr(this.mob.getLeader()) > this.stopRange;
    }

    @Override
    public void stop() {
        if (this.stopRange != 0) this.mob.getNavigation().stop();
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }



    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.mob.pathToLeader();
        }
    }
}
