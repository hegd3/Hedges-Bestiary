package com.hedge.hedges_bestiary.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class AvoidTargetWhenLowGoal extends Goal {

    protected final PathfinderMob mob;
    private final double speedModifier;
    private final int radius;
    private final int yRange;
    private final float healthThreshold;
    protected final float maxDist;
    @Nullable
    protected Path path;


    public AvoidTargetWhenLowGoal(PathfinderMob mob, double speedModifier, float maxDist, float healthThreshold, int radius, int yRange) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.maxDist = maxDist;
        this.healthThreshold = healthThreshold;
        this.radius = radius;
        this.yRange = yRange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
    }


    @Override
    public boolean canUse() {
        LivingEntity toAvoid = this.mob.getTarget();
        if (toAvoid == null) {
            return false;
        } else if (this.mob.getHealth() > healthThreshold) {
            return false;
        }
        Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, radius, yRange, toAvoid.position());
        if (vec3 == null) {
            return false;
        } else if (toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < toAvoid.distanceToSqr(this.mob)) {
            return false;
        } else {
            this.path = this.mob.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
            return this.path != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

}
