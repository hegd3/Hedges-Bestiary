package com.hedge.hedges_bestiary.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MountOverrideGoal extends Goal {
    private final PathfinderMob mount;


    public MountOverrideGoal(PathfinderMob mount) {
        this.mount = mount;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public void start() {
        this.mount.getNavigation().stop();
    }

    @Override
    public boolean canUse() {
        return this.mount.hasControllingPassenger();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mount.hasControllingPassenger();
    }
}
