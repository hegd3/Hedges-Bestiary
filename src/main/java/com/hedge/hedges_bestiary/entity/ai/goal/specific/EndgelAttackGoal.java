package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.EndgelEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EndgelAttackGoal extends GenericMeleeGoal<EndgelEntity> {

    private boolean randomFlying;
    public EndgelAttackGoal(EndgelEntity pMob) {
        super(pMob, 1.2f);
    }

    @Override
    public void start() {
        super.start();
        this.randomFlying = false;
    }

    @Override
    protected void tickPath(LivingEntity livingentity) {

        if (this.attackReach * 0.75F < this.dist) {
            super.tickPath(livingentity);
            if (this.randomFlying) this.randomFlying = false;
        } else {
            if (!this.randomFlying || this.mob.getNavigation().isDone()) {
                Vec3 v = this.findFlightPos(livingentity);
                this.mob.getNavigation().moveTo(v.x, v.y, v.z, 1.2f);
                this.randomFlying = true;
            }
        }
    }

    @Override
    public void tick() {
        if (this.mob.getAnimState() < 3) {
            LivingEntity livingentity = this.mob.getTarget();
            this.attackReach = this.mob.getAttackReachSqr(livingentity);
            this.dist = this.mob.distanceToSqr(livingentity);
            this.tickPath(livingentity);
            if (this.mob.getAnimState() == 0) {
                if (this.mob.canSpin()) {
                    this.mob.setSpin();
                } else if (this.mob.canUseAttack(livingentity, attackReach, dist)) {
                    this.mob.setAttacking();
                } else if (this.mob.canScream(attackReach, dist)) {
                    this.mob.setAnimState(3);
                    this.mob.getNavigation().stop();
                }
            }
        }

    }


    private Vec3 findFlightPos(LivingEntity target) {
        Vec3 heightAdjusted = target.position().add(mob.getRandom().nextInt(25 * 2) - 25, 10 + mob.getRandom().nextInt(6), mob.getRandom().nextInt(25 * 2) - 25);
        BlockHitResult result = mob.level().clip(new ClipContext(mob.getEyePosition(), heightAdjusted, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        if (result.getType() == HitResult.Type.MISS) {
            return heightAdjusted;
        } else {
            return result.getLocation();
        }
    }
}
