package com.hedge.hedges_bestiary.entity.types;

import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class HBMonster extends Monster implements AnimStateMob, AttackStateMob {
    protected static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(HBMonster.class, EntityDataSerializers.INT);

    public final SmoothAnimationState idleAnimationState = new SmoothAnimationState();

    protected int animTicks = 0;

    public HBMonster(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIM_STATE, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey == ANIM_STATE) {
            if (this.level().isClientSide() && this.getAnimState() == 0) {
                this.animTicks = 0;
            }
        }
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
        setUpAnimStates();
        if (this.getAnimState() > 0) {
            animTicks++;
        }
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
        this.idleAnimationState.animateWhen(this.isAlive() && keepsIdle(), this.tickCount);

    }

    public boolean keepsIdle() {
        return true;
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * 2.2 * this.getBbWidth() * 2.2 + entity.getBbWidth();
    }

    public void resetAnimState() {
        this.animTicks = 0;
        this.setAnimState(0);
    }
}
