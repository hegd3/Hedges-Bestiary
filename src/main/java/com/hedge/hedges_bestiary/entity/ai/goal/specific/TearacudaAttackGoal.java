package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.TearacudaEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class TearacudaAttackGoal extends GenericMeleeGoal<TearacudaEntity> {


    public TearacudaAttackGoal(TearacudaEntity entity) {
        super(entity, 1.3);
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.attackReach = this.mob.getAttackReachSqr(livingentity);
            this.dist = this.mob.distanceToSqr(livingentity);
            if (this.mob.isInFluidType()) {
                if (this.attackReach < this.dist) {
                    this.tickPath(livingentity);
                } else {
                    this.mob.lookAt(livingentity, 30f, 30f);
                    this.mob.getLookControl().setLookAt(livingentity, 30f, 30f);
                }
            }
        }

        if (this.mob.getAnimState() == 0) {
            if (this.mob.canFrenzy(this.attackReach, this.dist)) {
                this.mob.setAnimState(2);
            }
            else if (this.mob.canUseAttack(livingentity, this.attackReach, this.dist)) {
                this.mob.setAttacking();
            } else if (this.mob.canJump(this.attackReach, this.dist)) {
                Vec3 towardTarget = livingentity.position().subtract(this.mob.position()).normalize();
                Vec3 dashVector = new Vec3(towardTarget.x, 0.67, towardTarget.z).normalize().scale(1.2);
                this.mob.setDeltaMovement(dashVector);
                float yaw = (float)(Mth.atan2(dashVector.z, dashVector.x) * (Mth.RAD_TO_DEG)) - 90.0F;
                float pitch = (float)(-(Mth.atan2(dashVector.y, Mth.sqrt((float)(dashVector.x * dashVector.x + dashVector.z * dashVector.z))) * (Mth.RAD_TO_DEG))) * 0.5f;

                this.mob.setYHeadRot(yaw);
                this.mob.setYRot(yaw);

                this.mob.setXRot(pitch);
            }
        }
    }
}
