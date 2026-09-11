package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.PlomboEntity;
import net.minecraft.world.entity.LivingEntity;

public class PlomboAttackGoal extends GenericMeleeGoal<PlomboEntity> {
    public PlomboAttackGoal(PlomboEntity pMob) {
        super(pMob, 1.3);
    }

    @Override
    public boolean canUse() {
        return !this.mob.isBaby() && super.canUse();
    }

    @Override
    protected double getSpeedModifier() {
        return switch (this.mob.getAnimState()) {
            case 1, 2, 3 -> 1;
            default -> super.getSpeedModifier();
        };
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.attackReach = this.mob.getAttackReachSqr(livingentity);
            this.dist = this.mob.distanceToSqr(livingentity);
            if (this.mob.getAnimState() != 2) {
                if (this.attackReach < this.dist) {
                    this.tickPath(livingentity);
                } else {
                    this.mob.lookAt(livingentity, 30f, 30f);
                    this.mob.getLookControl().setLookAt(livingentity, 30f, 30f);
                }

            }
            if (this.mob.getAnimState() == 0) {
                if (this.mob.canUseMultiAttack(this.attackReach, this.dist)) {
                    this.mob.setAnimState(2);
                } else if (this.mob.canUseAttack(livingentity, this.attackReach, this.dist)) {
                    this.mob.setAttacking();
                }
            }
        }
    }
}
