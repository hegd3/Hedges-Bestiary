package com.hedge.hedges_bestiary.entity.types;

import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HBAquaticMob extends WaterAnimal implements AnimStateMob {

    public float roll = 0.0f;

    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(HBAquaticMob.class, EntityDataSerializers.INT);

    public final SmoothAnimationState idleAnimationState = new SmoothAnimationState();
    protected int animTicks;

    public HBAquaticMob(EntityType<? extends HBAquaticMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIM_STATE, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.clientTick();
        } else {
            this.serverTick();
        }
    }


    protected void clientTick() {
        this.setUpAnimStates();
        this.tickRoll();
    }


    protected void flop() {
        if (!this.isInWater() && this.onGround() && this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), (double)0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
            this.setOnGround(false);
            this.hasImpulse = true;
            this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }


    protected void serverTick() {

    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6f, 1f);
        } else {
            f = 0;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {

    }



    @Override
    public void onAboveBubbleCol(boolean pDownwards) {

    }

    @Override
    public void onInsideBubbleColumn(boolean pDownwards) {
    }

    @Override
    public void setAnimState(int i) {
        this.entityData.set(ANIM_STATE, i);
    }

    @Override
    public int getAnimState() {
        return this.entityData.get(ANIM_STATE);
    }

    @Override
    public int getAnimTicks() {
        return this.animTicks;
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(this.isAlive(), this.tickCount);
    }

    public void resetAnimState() {
        this.animTicks = 0;
        this.setAnimState(0);
    }

    protected void tickRoll() {
        this.roll = Mth.rotLerp(0.05F, this.roll, Mth.clamp((this.yRotO - this.getYRot()) * 0.1F, -0.45F, 0.45F));
    }

}
