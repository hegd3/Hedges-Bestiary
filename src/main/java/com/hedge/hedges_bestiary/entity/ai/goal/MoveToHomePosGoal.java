package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MoveToHomePosGoal extends Goal {

    protected final HBTamableAnimal mob;
    private final double speedModifier;
    private final double startDist;
    private final double stopDist;
    private int closeToHomeTryTicks;


    public MoveToHomePosGoal(HBTamableAnimal mob) {
        this(mob, 1.0, 16D, 4D);
    }

    public MoveToHomePosGoal(HBTamableAnimal mob, double speedModifier, double startDist, double stopDist) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.startDist = startDist;
        this.stopDist = stopDist;
        this.closeToHomeTryTicks = 100 + this.mob.getRandom().nextInt(100);
        this.setFlags(EnumSet.of(Flag.MOVE));
    }


    @Override
    public boolean canUse() {
        if (!this.mob.hasHome() || this.mob.getCommand() == 2) {
            return false;
        }
        this.closeToHomeTryTicks = Math.max(this.closeToHomeTryTicks - 1, 0);
        if (this.closeToHomeTryTicks == 0) {
            this.closeToHomeTryTicks = 100 + this.mob.getRandom().nextInt(100);
            if (!this.mob.getHomePos().closerToCenterThan(this.mob.position(), this.startDist)) {
                return moveToHome();
            }
        }
        return false;

    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.hasControllingPassenger() || this.mob.getCommand() == 2) {
            return false;
        }
        return !this.mob.getHomePos().closerToCenterThan(this.mob.position(), this.stopDist); //&& !this.mob.getNavigation().isDone();
    }

    @Override
    public void tick() {
        if (this.mob.getNavigation().isDone()) {
            this.moveToHome();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        if (this.mob.getSleepType().canSleep(mob.level().getDayTime() % 24000)) {
            this.mob.setNapping(true);
        }
        this.closeToHomeTryTicks = 100 + this.mob.getRandom().nextInt(100);
    }

    protected boolean moveToHome() {

        BlockPos pos = this.mob.getHomePos().above();
        if (this.mob.isTame() && !pos.closerToCenterThan(this.mob.position(), this.startDist * 3)) {
            this.mob.moveTo(pos.getX(), pos.getY(), pos.getZ());
            return false;
        } else {
            double dx = Mth.clamp(pos.getX() - this.mob.getX(), -16, 16);
            double dy = pos.getY() - this.mob.getY();
            double dz = Mth.clamp(pos.getZ() - this.mob.getZ(), -16, 16);
            double x = this.mob.getX() + dx;
            double y = this.mob.getY() + dy;
            double z = this.mob.getZ() + dz;

            this.mob.getNavigation().moveTo(x, y, z, this.speedModifier);

            return this.mob.getNavigation().getPath() != null && this.mob.getNavigation().getPath().canReach();
        }

    }

}
