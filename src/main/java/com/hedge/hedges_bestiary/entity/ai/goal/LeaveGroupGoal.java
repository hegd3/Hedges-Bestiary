package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBGroupMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class LeaveGroupGoal<E extends LivingEntity &HBGroupMob<E>> extends Goal {
    private final E mob;

    public LeaveGroupGoal(E mob) {
        this.mob = mob;
    }
    @Override
    public boolean canUse() {
        if (this.mob.isFollower()) {
            return !this.mob.inRangeOfLeader() || !this.mob.getLeader().canBeFollowed();
        }
        return false;
    }

    @Override
    public void start() {
        mob.stopFollowing();
    }
}
