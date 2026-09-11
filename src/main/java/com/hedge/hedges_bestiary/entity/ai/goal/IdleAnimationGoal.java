package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.IdleAnimMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class IdleAnimationGoal<E extends PathfinderMob & IdleAnimMob> extends Goal {
    protected final E mob;
    protected final int cooldown;
    protected int animCD;

    public IdleAnimationGoal(E pMob) {
        this(pMob, 100);
    }

    public IdleAnimationGoal(E pMob, int cooldown) {
        this.mob = pMob;
        this.cooldown = cooldown;
        this.animCD = 0;
    }

    @Override
    public void start() {
        this.mob.playIdle();
        this.resetCD();
    }
    @Override
    public boolean canUse() {
        this.animCD = Math.max(this.animCD - 1, 0);
        if (this.animCD == 0) {
            return this.mob.canPlayIdle();
        }
        return false;
    }

    protected void resetCD() {
        this.animCD = this.cooldown + this.mob.getRandom().nextInt(this.cooldown) * 2;
    }
}
