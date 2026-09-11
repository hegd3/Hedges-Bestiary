package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class HBFollowOwnerGoal extends Goal {

    protected final HBTamableAnimal mob;
    protected Entity owner;
    protected final double speedModifier;
    protected final double sprintSpeedModifier;
    protected int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    protected final boolean canFly;
    protected final boolean shouldChangeMalus;

    public HBFollowOwnerGoal(HBTamableAnimal mob, double speedModifier, double sprintSpeedModifier, float startDistance, float stopDistance) {
        this(mob, speedModifier, sprintSpeedModifier, startDistance, stopDistance, false, false);
    }

    public HBFollowOwnerGoal(HBTamableAnimal mob, double speedModifier, double sprintSpeedModifier, float startDistance, float stopDistance, boolean canFly, boolean shouldChangeMalus) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.sprintSpeedModifier = sprintSpeedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.canFly = canFly;
        this.shouldChangeMalus = shouldChangeMalus;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }


    @Override
    public boolean canUse() {
        LivingEntity owner = mob.getOwner();
        if (owner == null) {
            return false;
        } else if (owner.isSpectator()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else if (this.mob.distanceToSqr(owner) < (double) (this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = owner;
            return this.shouldFollow() && !this.isInCombat();
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (mob.getNavigation().isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else {
            return !(mob.distanceToSqr(owner) <= (double) (stopDistance * stopDistance)) && this.shouldFollow() && !this.isInCombat();
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        if (shouldChangeMalus) {
            this.oldWaterCost = mob.getPathfindingMalus(BlockPathTypes.WATER);
            this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }
    }

    @Override
    public void stop() {
        this.owner = null;
        this.mob.getNavigation().stop();
        if (shouldChangeMalus) {
            this.mob.setPathfindingMalus(BlockPathTypes.WATER, oldWaterCost);
        }
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(owner, 10.0F, (float) mob.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.mob.distanceToSqr(owner) >= 256.0D) {
                this.teleportToOwner();
            } else if (this.mob.distanceToSqr(owner) >= 64.0D) {
                this.mob.getNavigation().moveTo(owner, sprintSpeedModifier);
            } else {
                this.mob.getNavigation().moveTo(owner, speedModifier);
            }
        }
    }

    protected boolean unableToMove() {
        return mob.isOrderedToSit() || mob.isPassenger() || mob.isLeashed();
    }

    protected void teleportToOwner() {
        BlockPos blockpos = owner.blockPosition();
        for (int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }
    }

    protected boolean maybeTeleportTo(int x, int y, int z) {
        if (Math.abs((double) x - owner.getX()) < 2.0D && Math.abs((double) z - owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.mob.moveTo((double) x + 0.5D, y, (double) z + 0.5D, mob.getYRot(), mob.getXRot());
            this.mob.getNavigation().stop();
            return true;
        }
    }

    protected boolean canTeleportTo(BlockPos blockPos) {
        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.mob.level(), blockPos.mutable());
        if (blockpathtypes != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = mob.level().getBlockState(blockPos.below());
            if (!canFly && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = blockPos.subtract(mob.blockPosition());
                return mob.level().noCollision(mob, mob.getBoundingBox().move(blockpos));
            }
        }
    }

    protected boolean shouldFollow() {
        return this.mob.getCommand() == 2;
    }

    protected boolean isInCombat() {
        if (this.owner != null) {
            return mob.distanceTo(owner) < 30 && mob.getTarget() != null && mob.getTarget().isAlive();
        }
        return false;
    }

    protected int randomIntInclusive(int min, int max) {
        return this.mob.getRandom().nextInt(max - min + 1) + min;
    }
}
