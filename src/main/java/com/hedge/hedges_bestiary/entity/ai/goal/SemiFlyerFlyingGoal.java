package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.SemiFlyer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SemiFlyerFlyingGoal<E extends PathfinderMob & SemiFlyer> extends Goal {

    private final E mob;
    protected final float speedModifier;
    private final int flightRange;
    private final int flightHeight;
    private final int interval;
    private int flyTicks;
    private boolean isLanding;
    protected final int maxTimeFlying;
    protected Vec3 pos;

    public SemiFlyerFlyingGoal(E mob, float speedModifier, int flightRange, int flightHeight, int interval, int maxTimeFlying) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.flightRange = flightRange;
        this.flightHeight = flightHeight;
        this.maxTimeFlying = maxTimeFlying;
        this.speedModifier = speedModifier;
        this.interval = interval;
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.isPassenger() || mob.isBaby()) {
            return false;
        }
        if (!mob.isFlying() && mob.getRandom().nextInt(interval) != 0) {
            return false;
        }
        this.pos = this.findFlightPos();
        return true;
    }

    @Override
    public void start() {
        this.flyTicks = 0;
        this.isLanding = false;
        this.mob.setFlying(true);
        this.mob.getNavigation().moveTo(this.pos.x, this.pos.y, this.pos.z, speedModifier);
        if (mob.onGround()) {
            this.mob.setDeltaMovement(mob.getDeltaMovement().add(0.0D, 0.5D, 0.0D));
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.pos = null;
    }

    @Override
    public void tick() {
        this.flyTicks++;
        if (this.mob.onGround() && this.flyTicks > 40) {
            this.mob.setFlying(false);
        } else if (this.isLanding) {
            this.mob.getDeltaMovement().add(0, -0.01, 0);
        }
        else if (this.mob.isFlying()) {
            this.mob.getDeltaMovement().add(0, -0.004, 0);
            if (this.flyTicks % maxTimeFlying == 0 && !this.isOverWaterOrVoid()) {
                this.isLanding = true;
                this.pos = this.groundPosition(this.pos);
                this.mob.getNavigation().moveTo(this.pos.x, this.pos.y, this.pos.z, speedModifier);
            }
        }
        else if (this.isOverWaterOrVoid() || this.mob.isInFluidType()) {
            this.mob.setFlying(true);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.isLanding) {
            return !this.mob.getNavigation().isDone() && !this.mob.onGround();
        } else {
            return this.mob.isFlying() && !this.mob.getNavigation().isDone();
        }
    }

    protected Vec3 findFlightPos() {
        Vec3 heightAdjusted = mob.position().add(mob.getRandom().nextInt(flightRange * 2) - flightRange, 0, mob.getRandom().nextInt(flightRange * 2) - flightRange);
        Vec3 ground = groundPosition(heightAdjusted);
        heightAdjusted = new Vec3(heightAdjusted.x, ground.y + flightHeight + mob.getRandom().nextInt(6), heightAdjusted.z);
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

    protected boolean isOverWaterOrVoid() {
        BlockPos position = mob.blockPosition();
        while (position.getY() > mob.level().getMinBuildHeight() && mob.level().isEmptyBlock(position) && mob.level().getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return !mob.level().getFluidState(position).isEmpty() || mob.level().getBlockState(position).is(Blocks.VINE) || position.getY() <= mob.level().getMinBuildHeight();
    }
}
