package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.BurodonEntity;
import net.minecraft.world.entity.LivingEntity;

public class BurodonAttackGoal extends GenericMeleeGoal<BurodonEntity> {

    public BurodonAttackGoal(BurodonEntity entity) {
        super(entity, 1.4D);
    }

    @Override
    public boolean canUse() {
        return !this.mob.isBaby() && super.canUse();
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        int animState = this.mob.getAnimState();
        if (animState != BurodonEntity.JUMP_ANIM && animState != BurodonEntity.ROAR_ANIM && livingentity != null) {
            this.attackReach = this.mob.getAttackReachSqr(livingentity);
            this.dist = this.mob.distanceToSqr(livingentity);
            if (this.attackReach < this.dist) {
                this.tickPath(livingentity);
            } else {
                this.mob.lookAt(livingentity, 30f, 30f);
                this.mob.getLookControl().setLookAt(livingentity, 30f, 30f);
            }

            if (animState == 0) {
                if (this.mob.canJump(livingentity, attackReach, dist)) {
                    this.mob.getNavigation().stop();
                    this.mob.setAnimState(BurodonEntity.JUMP_ANIM);
                }
                else if (this.mob.canUseAttack(livingentity, attackReach, dist)) {
                    this.mob.setAttacking();
                } else if (this.mob.canRoar(attackReach, dist)) {
                    this.mob.getNavigation().stop();
                    this.mob.playSound(HBSounds.BURODON_ROAR.get(), 1.5F, 1 + mob.getRandom().nextFloat() * 0.25F);
                    this.mob.setAnimState(BurodonEntity.ROAR_ANIM);
                }
            }
        }
    }
}
