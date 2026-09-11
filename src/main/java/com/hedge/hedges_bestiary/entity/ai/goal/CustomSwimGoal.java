package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.util.WorldHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CustomSwimGoal extends RandomStrollGoal {

    private final int radius;
    private final int height;
    private final int bound;
    private int ticksTilHeightCheck;
    private Vec3 pos;
    private final boolean preferSurface;

    public CustomSwimGoal(PathfinderMob mob, double speedModifier, int interval, int radius, int height, boolean preferSurface) {
        super(mob, speedModifier, interval);
        this.radius = radius;
        this.height = height;
        this.bound = height / 2;
        this.preferSurface = preferSurface;
    }

    @Override
    public void start() {
        super.start();
        this.ticksTilHeightCheck = 20;
    }

    @Override
    public boolean canUse() {
        return this.mob.isInWaterOrBubble() && super.canUse();
    }


    @Override
    public boolean canContinueToUse() {
        return this.mob.isInWaterOrBubble() && super.canContinueToUse();
    }

    @Override
    public void tick() {
        if (--this.ticksTilHeightCheck <= 0) {
            BlockPos blockPos = WorldHelpers.fromVec3(this.pos);
            if (this.preferSurface) {
                int blocksFromBoundary = EntityHelpers.blocksFromWaterBoundary(this.mob.level(), blockPos, this.height, Direction.UP);
                if (blocksFromBoundary > this.bound) {
                    this.pos = this.pos.add(0, 1, 0);
                    this.mob.getNavigation().moveTo(this.pos.x, this.pos.y, this.pos.z, this.speedModifier);
                } else {
                    this.pos = this.pos.add(0, -1, 0);
                    this.mob.getNavigation().moveTo(this.pos.x, this.pos.y, this.pos.z, this.speedModifier);
                }
            } else {
                int blocksFromBoundary = EntityHelpers.blocksFromWaterBoundary(this.mob.level(), blockPos, this.height, Direction.DOWN);
                if (blocksFromBoundary <= this.bound) {
                    this.pos = this.pos.add(0, 1, 0);
                    this.mob.getNavigation().moveTo(this.pos.x, this.pos.y, this.pos.z, this.speedModifier);
                } else {
                    this.pos = this.pos.add(0, -1, 0);
                    this.mob.getNavigation().moveTo(this.pos.x, this.pos.y, this.pos.z, this.speedModifier);
                }
            }
            this.ticksTilHeightCheck = 20;
        }
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        this.pos = EntityHelpers.getRandomSwimPos(this.mob, this.radius, this.height, this.preferSurface);
        return this.pos;
    }
}
