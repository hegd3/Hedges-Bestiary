package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.util.WorldHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyingWanderGoal extends Goal {

    private final PathfinderMob mob;
    protected final float speedModifier;
    private final int flightRange;
    private final int flightHeight;
    private final int minY;
    protected double x;
    protected double y;
    protected double z;

    public FlyingWanderGoal(PathfinderMob mob, float speedModifier, int flightRange, int flightHeight, int minY) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.flightRange = flightRange;
        this.flightHeight = flightHeight;
        this.minY = minY;
        this.speedModifier = speedModifier;
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.isVehicle() || mob.isPassenger()) {
            return false;
        }
        Vec3 target = this.findFlightPos();
        this.x = target.x;
        this.y = target.y;
        this.z = target.z;
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.x, this.y, this.z, speedModifier);
        if (mob.onGround()) {
            this.mob.setDeltaMovement(mob.getDeltaMovement().add(0.0D, 0.5D, 0.0D));
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }


    protected Vec3 findFlightPos() {
        Vec3 heightAdjusted = mob.position().add(mob.getRandom().nextInt(flightRange * 2) - flightRange, 0, mob.getRandom().nextInt(flightRange * 2) - flightRange);
        Vec3 ground = groundPosition(heightAdjusted);
        if (mob.level().getBlockState(WorldHelpers.fromVec3(ground)).isAir()) {
            if (mob.getY() < this.minY) {
                heightAdjusted = new Vec3(heightAdjusted.x, minY + mob.getRandom().nextInt(6), heightAdjusted.z);
            } else {
                heightAdjusted = new Vec3(heightAdjusted.x, minY - mob.getRandom().nextInt(6), heightAdjusted.z);
            }
        } else {
            heightAdjusted = new Vec3(heightAdjusted.x, ground.y + flightHeight + mob.getRandom().nextInt(6), heightAdjusted.z);
        }
        BlockHitResult result = mob.level().clip(new ClipContext(mob.getEyePosition(), heightAdjusted, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        if (result.getType() == HitResult.Type.MISS) {
            return heightAdjusted;
        } else {
            return result.getLocation();
        }
    }

    public Vec3 groundPosition(Vec3 airPosition) {
        BlockPos.MutableBlockPos ground = new BlockPos.MutableBlockPos();
        ground.set(airPosition.x, airPosition.y, airPosition.z);

        while (ground.getY() > mob.level().getMinBuildHeight() && !mob.level().getBlockState(ground).isSolid() && mob.level().getFluidState(ground).isEmpty()) {
            ground.move(0, -1, 0);
        }
        return Vec3.atCenterOf(ground.below());
    }

}
