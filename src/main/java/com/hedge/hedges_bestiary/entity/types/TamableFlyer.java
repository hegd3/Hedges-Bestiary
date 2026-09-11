package com.hedge.hedges_bestiary.entity.types;

import com.hedge.hedges_bestiary.entity.ai.control.FlyingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBPathNavigatorGround;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class TamableFlyer extends HBTamableAnimal implements SemiFlyer {

    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(TamableFlyer.class, EntityDataSerializers.BOOLEAN);

    protected float flyProgress;
    private float prevFlyProgress;
    private float flightPitch = 0;
    private float prevFlightPitch = 0;
    private float flightRoll = 0;
    private float prevFlightRoll = 0;

    public TamableFlyer(EntityType<? extends TamableFlyer> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Flying", this.isFlying());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey == FLYING) {
            switchNav(this.isFlying());
        }
    }

    @Override
    public void setSitting(boolean b) {
        super.setSitting(b);
        if (b) this.setFlying(false);
    }

    @Override
    public void tick() {
        super.tick();

        this.prevFlyProgress = this.flyProgress;
        this.prevFlightPitch = this.flightPitch;
        this.prevFlightRoll = this.flightRoll;

        this.tickFlight();
        this.tickRotation((float) this.getDeltaMovement().y * 2 * -(float) (180F / (float) Math.PI));

    }
/*
    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.isFlying() && !this.onGround() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }
    }

 */

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.isFlying() && this.getXRot() > 0) {
            f = 0;
        }
        else if (this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6f, 1f);
        } else {
            f = 0;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isFlying()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.dive();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }

    protected abstract void dive();

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    @Override
    public void setFlying(boolean b) {
        this.entityData.set(FLYING, b);
    }

    @Override
    public boolean isLandNav() {
        return this.navigation instanceof HBPathNavigatorGround;
    }


    public void tickFlight() {
        if (this.isFlying() && flyProgress < 5F) this.flyProgress++;
        if (!this.isFlying() && flyProgress > 0F) this.flyProgress--;

        if (this.isFlying()) {
            this.setNoGravity(true);
            if (this.isLandNav()) {
                this.switchNav(false);
            }
        } else {
            this.setNoGravity(false);
            if (!this.isLandNav()) {
                this.switchNav(true);
            }
        }


        if (!level().isClientSide) {
            if (this.isFlying() && this.isAlive()) {
                if (horizontalCollision && !this.isInWater()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.05D, 0));
                }
            }
        }
    }

    protected void switchNav(boolean flying) {
        if (flying) {
            //his.setDeltaMovement(this.getDeltaMovement().add(0, 0.15, 0));
            this.moveControl = new FlyingMoveControl(this, 45, 20, 1.1f);
            this.lookControl = new SmoothSwimmingLookControl(this, 30);
            this.navigation = new FlyingPathNavigation(this, this.level());
        } else {
            this.lookControl = new LookControl(this);
            this.moveControl = new MoveControl(this);
            this.navigation = new HBPathNavigatorGround(this, this.level());
        }
    }

    public void tickRotation(float yMov) {
        this.flightPitch = yMov;
        float threshold = 1F;
        boolean flag = false;
        if (isFlying() && this.yRotO - this.getYRot() > threshold) {
            flightRoll += 10;
            flag = true;
        }
        if (isFlying() && this.yRotO - this.getYRot() < -threshold) {
            flightRoll -= 10;
            flag = true;
        }
        if (!flag) {
            if (flightRoll > 0) this.flightRoll = Math.max(flightRoll - 5, 0);
            if (flightRoll < 0) this.flightRoll = Math.min(flightRoll + 5, 0);
        }
        this.flightRoll = Mth.clamp(flightRoll, -60, 60);
    }

    public float getFlightPitch(float partialTick) {
        return (prevFlightPitch + (flightPitch - prevFlightPitch) * partialTick);
    }

    public float getFlightRoll(float partialTick) {
        return (prevFlightRoll + (flightRoll - prevFlightRoll) * partialTick);
    }

    public float getFlyProgress(float partialTick) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTick) * 0.2F;
    }

}
