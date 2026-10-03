package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.ZippermouthEntity;
import net.minecraft.world.entity.LivingEntity;

public class ZippermouthAttackGoal extends GenericMeleeGoal<ZippermouthEntity> {
    public ZippermouthAttackGoal(ZippermouthEntity pMob) {
        super(pMob, 1.8F);
    }

    @Override
    protected void look(LivingEntity target) {
        this.mob.getLookControl().setLookAt(target, 5.0F, 5.0F);
        this.mob.lookAt(target, 5F, 5F);
    }

    @Override
    public void tick() {
        LivingEntity living = this.mob.getTarget();
        int animState = this.mob.getAnimState();
        this.attackReach = this.mob.getAttackReachSqr(living);
        this.dist = this.mob.distanceToSqr(living);
        if (this.attackReach < this.dist) {
            this.tickPath(living);
        } else {
            this.look(living);
        }
        if (animState == 0) {
            if (this.mob.canRush(attackReach, dist)) {
                this.mob.setAnimState(2);
            }
            else if (this.mob.canUseAttack(living, attackReach, dist)) {
                this.mob.setAttacking();
            }
        }
    }
}
