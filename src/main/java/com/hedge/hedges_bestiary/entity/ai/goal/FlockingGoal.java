package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBGroupMob;
import com.hedge.hedges_bestiary.entity.types.TamableFlyer;

public class FlockingGoal<E extends TamableFlyer & HBGroupMob<E>> extends GroupFollowLeaderGoal<E> {
    public FlockingGoal(E mob) {
        super(mob);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mob.isFollower() && this.mob.getLeader().isFlying() && !this.mob.isFlying()) {
            this.mob.setFlying(true);
        }
    }
}
