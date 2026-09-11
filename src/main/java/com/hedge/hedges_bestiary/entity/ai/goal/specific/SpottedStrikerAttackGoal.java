package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.SpottedStrikerEntity;
import net.minecraft.world.entity.LivingEntity;

public class SpottedStrikerAttackGoal extends GenericMeleeGoal<SpottedStrikerEntity> {


    public SpottedStrikerAttackGoal(SpottedStrikerEntity mob) {
        super(mob, 1.2f);
    }

    @Override
    public void stop() {
        super.stop();
        if (this.mob.isCloaked()) {
            this.mob.setCloaked(false);
        }
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.attackReach = this.mob.getAttackReachSqr(livingentity);
            this.dist = this.mob.distanceToSqr(livingentity);
            this.tickPath(livingentity);
            if (this.mob.getAnimState() == 0) {
                if (this.mob.canSuperBite(this.attackReach, this.dist)) {
                    this.mob.setAnimState(2);
                    if (this.mob.isCloaked()) {
                        this.mob.setCloaked(false);
                    }
                } else if (this.mob.canUseAttack(livingentity, this.attackReach, this.dist)) {
                    this.mob.setAnimState(1);
                    this.mob.setCloaked(false);
                } else if (this.mob.canCloak(this.attackReach, this.dist)) {
                    this.mob.setCloaked(true);
                }
            }
        }
    }

    @Override
    protected double getSpeedModifier() {
        if (this.mob.getAnimState() > 0) return 0.7;
        return 1.2f;
    }


}
