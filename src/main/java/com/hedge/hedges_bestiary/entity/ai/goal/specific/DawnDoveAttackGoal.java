package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.DawnDoveEntity;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DawnDoveAttackGoal extends GenericMeleeGoal<DawnDoveEntity> {

    private boolean randomFlying;
    private boolean reachedDropPos;
    private int phaseTicks;
    public DawnDoveAttackGoal(DawnDoveEntity pMob) {
        super(pMob, 1.2f);
    }

    @Override
    public boolean canUse() {
        return !this.mob.isBaby() && super.canUse();
    }

    @Override
    protected void tickPath(LivingEntity livingentity) {
        if (this.randomFlying) {
            if (this.attackReach * 20 < this.dist) {
                super.tickPath(livingentity);
                if (this.mob.getAnimState() == 0 && this.mob.canShoot(livingentity, attackReach, dist)) {
                    this.mob.lookAt(livingentity, 90.0F, 90.0F);
                    this.mob.setAnimState(2);
                }
            } else {
                if (this.phaseTicks++ > 80) {
                    this.randomFlying = false;
                    this.mob.getNavigation().stop();
                    this.phaseTicks = 0;
                } else if (this.mob.getNavigation().isDone() || !this.reachedDropPos) {
                    Vec3 pos = this.findFlightPos(livingentity);
                    this.mob.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.3);
                    this.reachedDropPos = true;
                }

            }
        } else {
            super.tickPath(livingentity);
            if (this.phaseTicks > 2 && this.attackReach * 20 >= this.dist) {
                this.randomFlying = true;
                this.phaseTicks = 0;
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


    @Override
    public void start() {
        super.start();
        if (!this.mob.isFlying()) {
            this.mob.setFlying(true);
        }
        this.randomFlying = true;
        this.reachedDropPos = false;
        this.phaseTicks = 0;
    }

    @Override
    protected void look(LivingEntity target) {
        this.mob.lookAt(target, 60F, 60F);
        this.mob.getLookControl().setLookAt(target, 60.0F, 60.0F);
    }

    @Override
    public void tick() {

        LivingEntity livingentity = this.mob.getTarget();
        if (this.mob.isGrabbing() && this.mob.getGrabbedEntity() == livingentity) {
            phaseTicks++;
            if (phaseTicks % 20 == 0) {
                AttackHelpers.betterHurt(this.mob, livingentity, 0.4f);
            }
            if (!this.reachedDropPos) {
                Vec3 pos = this.findFlightPos(this.mob).add(0, 10, 0);
                this.mob.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.2f);
                this.reachedDropPos = true;
            } else if (this.mob.getNavigation().isDone() || phaseTicks > 400) {
                this.mob.releaseGrab();
                this.phaseTicks = 0;
                if (!this.randomFlying) this.randomFlying = true;
            }
        } else {
            if (livingentity != null) {
                this.attackReach = this.mob.getAttackReachSqr(livingentity);
                this.dist = this.mob.distanceToSqr(livingentity);
                this.tickPath(livingentity);
                if (!this.randomFlying && this.mob.getAnimState() == 0) {
                    if (this.mob.canShoot(livingentity, attackReach, dist)) {
                        this.mob.lookAt(livingentity, 90.0F, 90.0F);
                        this.mob.setAnimState(2);
                    } else if (this.mob.canUseClawAttack(livingentity, attackReach, dist)) {
                        this.mob.setAnimState(3);
                        this.reachedDropPos = false;
                    } else if (this.mob.canUseAttack(livingentity, attackReach, dist)) {
                        this.mob.setAttacking();
                        this.phaseTicks++;
                    }
                }
            }
        }
    }


}
