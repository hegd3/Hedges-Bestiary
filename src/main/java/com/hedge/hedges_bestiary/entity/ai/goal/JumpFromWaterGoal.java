package com.hedge.hedges_bestiary.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class JumpFromWaterGoal extends JumpGoal {
    private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 4, 5, 6, 7};
    private final PathfinderMob mob;
    private final double jumpStrength;
    protected final int interval;
    private boolean breached;
    protected int jumpCD = 1;

    public JumpFromWaterGoal(PathfinderMob mob, int pInterval, double jumpStrength) {
        this.mob = mob;
        this.interval = reducedTickDelay(pInterval);
        this.jumpStrength = jumpStrength;
    }

    public boolean canUse() {
        if (this.mob.getTarget() != null) {
            return false;
        }
        this.jumpCD = Math.max(0, this.jumpCD - 1);
        if (this.jumpCD > 0 || this.mob.getRandom().nextInt(this.interval) != 0) {
            return false;
        } else {
            return this.canJump();
        }
    }

    protected boolean canJump() {
        Direction direction = this.mob.getMotionDirection();
        int i = direction.getStepX();
        int j = direction.getStepZ();
        BlockPos blockpos = this.mob.blockPosition();

        for(int k : STEPS_TO_CHECK) {
            if (!this.waterIsClear(blockpos, i, j, k) || !this.surfaceIsClear(blockpos, i, j, k)) {
                return false;
            }
        }

        return true;
    }

    private boolean waterIsClear(BlockPos pPos, int pDx, int pDz, int pScale) {
        BlockPos blockpos = pPos.offset(pDx * pScale, 0, pDz * pScale);
        return this.mob.level().getFluidState(blockpos).is(FluidTags.WATER) && !this.mob.level().getBlockState(blockpos).blocksMotion();
    }

    private boolean surfaceIsClear(BlockPos pPos, int pDx, int pDz, int pScale) {
        return this.mob.level().getBlockState(pPos.offset(pDx * pScale, 2, pDz * pScale)).isAir() && this.mob.level().getBlockState(pPos.offset(pDx * pScale, 2, pDz * pScale)).isAir();
    }


    public boolean canContinueToUse() {
        if (this.breached) {
            return !this.mob.isInWater();
        }
        double d0 = this.mob.getDeltaMovement().y;
        return (!(d0 * d0 < (double)0.03F) || this.mob.getXRot() == 0.0F || !(Math.abs(this.mob.getXRot()) < 10.0F) || !this.mob.isInWater()) && !this.mob.onGround();
    }

    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void stop() {
        this.jumpCD = 20;
    }

    public void start() {
        Direction direction = this.mob.getMotionDirection();
        this.mob.setDeltaMovement(this.mob.getDeltaMovement().add((double)direction.getStepX() * jumpStrength, jumpStrength, (double)direction.getStepZ() * jumpStrength));
        this.mob.getNavigation().stop();
    }



    public void tick() {
        boolean flag = this.breached;
        if (!flag) {
            FluidState fluidstate = this.mob.level().getFluidState(this.mob.blockPosition());
            this.breached = fluidstate.is(FluidTags.WATER);
        }

        if (this.breached && !flag) {
            this.mob.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
        }

        Vec3 vec3 = this.mob.getDeltaMovement();
        if (vec3.y * vec3.y < (double)0.03F && this.mob.getXRot() != 0.0F) {
            this.mob.setXRot(Mth.rotLerp(0.2F, this.mob.getXRot(), 0.0F));
        } else if (vec3.length() > (double)1.0E-5F) {
            double d0 = vec3.horizontalDistance();
            double d1 = Math.atan2(-vec3.y, d0) * (double)(180F / (float)Math.PI);
            this.mob.setXRot((float)d1);
        }

    }
}
