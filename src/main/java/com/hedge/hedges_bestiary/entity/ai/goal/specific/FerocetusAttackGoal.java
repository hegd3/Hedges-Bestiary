package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.FerocetusEntity;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;


public class FerocetusAttackGoal extends GenericMeleeGoal<FerocetusEntity> {


    public FerocetusAttackGoal(FerocetusEntity pMob) {
        super(pMob, 1.6f);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public boolean canUse() {
        return !this.mob.isBaby() && super.canUse();
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null && this.mob.isInWater()) {
            if (this.mob.isGrabbing()) {
                if (this.mob.canJump(1, 0)) {
                    this.mob.playSound(SoundEvents.DOLPHIN_JUMP);
                    Vec3 toward = livingentity.position().subtract(this.mob.position()).normalize();
                    Vec3 dash = new Vec3(toward.x, 1, toward.z).normalize().scale(1.5);
                    this.mob.setDeltaMovement(dash);
                    float yaw = (float) (Mth.atan2(dash.z, dash.x) * (180F / Math.PI)) - 90.0F;
                    float pitch = (float) (-(Mth.atan2(dash.y, Mth.sqrt((float) (dash.x * dash.x + dash.z * dash.z))) * (180F / Math.PI))) * 0.5f;

                    this.mob.setYHeadRot(yaw);
                    this.mob.setYRot(yaw);

                    this.mob.setXRot(pitch);
                    this.mob.setAnimState(3);

                }
                else if (this.mob.getNavigation().isDone()) {
                    Vec3 pos = EntityHelpers.getRandomSwimPos(this.mob, 10, 10, true);
                    if (pos != null) {
                        this.mob.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.6F);
                    }
            }
            } else {
                this.attackReach = this.mob.getAttackReachSqr(livingentity);
                this.dist = this.mob.distanceToSqr(livingentity);
                if (this.mob.getAnimState() < 2 || this.mob.getAnimState() == 4) {
                    if (this.attackReach < this.dist) {
                        this.tickPath(livingentity);
                    } else {
                        this.mob.lookAt(livingentity, 30f, 30f);
                        this.mob.getLookControl().setLookAt(livingentity, 30f, 30f);
                    }
                }

                if (this.mob.getAnimState() == 0) {
                    if (this.mob.canUseAttack(livingentity, this.attackReach, this.dist)) {
                        if (mob.smallEnoughToGrab(livingentity)) {
                            this.mob.setAnimState(4);
                        } else {
                            this.mob.setAttacking();
                        }
                    } else if (this.mob.canJump(this.attackReach, this.dist) && livingentity.isInWater()) {
                        this.mob.getNavigation().stop();
                        this.mob.playSound(SoundEvents.DOLPHIN_JUMP);
                        Vec3 toward = livingentity.position().subtract(this.mob.position()).normalize();
                        Vec3 dash = new Vec3(toward.x, 1, toward.z).normalize().scale(1.5);
                        this.mob.setDeltaMovement(dash);
                        float yaw = (float) (Mth.atan2(dash.z, dash.x) * (180F / Math.PI)) - 90.0F;
                        float pitch = (float) (-(Mth.atan2(dash.y, Mth.sqrt((float) (dash.x * dash.x + dash.z * dash.z))) * (180F / Math.PI))) * 0.5f;

                        this.mob.setYHeadRot(yaw);
                        this.mob.setYRot(yaw);

                        this.mob.setXRot(pitch);
                        this.mob.setAnimState(3);
                    }
                }
            }
        }

    }

}
