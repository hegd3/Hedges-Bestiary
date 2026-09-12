package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.client.particle.EndgelScreamParticleOptions;
import com.hedge.hedges_bestiary.entity.ai.control.FlyingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.FlyingWanderGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.specific.EndgelAttackGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetBelowGoal;
import com.hedge.hedges_bestiary.entity.projectile.EndgelBlast;
import com.hedge.hedges_bestiary.entity.projectile.EndgelBullet;
import com.hedge.hedges_bestiary.entity.types.HBMonster;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


public class EndgelEntity extends HBMonster {
    private static final EntityDataAccessor<Boolean> LEFT = SynchedEntityData.defineId(EndgelEntity.class, EntityDataSerializers.BOOLEAN);


    public float roll = 0.0f;
    private float prevTrail;
    private float trail = 0.0f;
    private int spinCD = 0;
    private int attackCD = 0;
    private int screamCD = 0;
    private int projAngle = 0;

    public final AnimationState spinAnimationState = new AnimationState();
    public final AnimationState shootAnimationState = new AnimationState();
    public final SmoothAnimationState screamWindupAnimationState = new SmoothAnimationState(0.1f);
    public final SmoothAnimationState screamAnimationState = new SmoothAnimationState();

    public EndgelEntity(EntityType<? extends EndgelEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 999, 6, 1.0f);
        this.lookControl = new SmoothSwimmingLookControl(this, 6);
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 70F)
                .add(Attributes.MOVEMENT_SPEED, 0.18F);
    }

    /*
    public int getMaxHeadXRot() {
        return 1;
    }

     */

    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EndgelAttackGoal(this));
        this.goalSelector.addGoal(1, new FlyingWanderGoal(this, 1.0f, 35, 25, 60));

        this.targetSelector.addGoal(0, new HBHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new TargetBelowGoal<>(this, Player.class, null));

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEFT, false);
    }



    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FlyingPathNavigation(this, pLevel);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.getAnimState() > 2) {
            super.travel(Vec3.ZERO);
            this.setDeltaMovement(Vec3.ZERO);
        } else if (this.isEffectiveAi()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }



    @Override
    protected void serverTick() {
        if (this.getAnimState() > 0) {
            this.animTicks++;
            switch (this.getAnimState()) {

                case 1 -> {
                    if (this.animTicks == 25) {
                        this.level().broadcastEntityEvent(this, (byte)39);
                        for (int i = -45; i <= 45; i+=45) {
                            for (int j = -25; j <= 25; j+=25) {
                                EndgelBullet scream = new EndgelBullet(HBEntities.ENDGEL_BULLET.get(), this.level());
                                scream.moveTo(this.position());
                                scream.shootFromRotation(this, this.getXRot() + j, this.getYRot() + i, 0.0f, 1.5F, 0);
                                scream.setTarget(this.getTarget());
                                level().addFreshEntity(scream);
                            }
                        }
                        this.playSound(HBSounds.ENDGEL_SHOOT.get(), 2.5F, this.getRandom().nextFloat() * 0.5F + 1.0F);
                    } else if (this.animTicks > 34) {
                        this.resetAnimState();
                        this.attackCD = 200;
                    }
                }
                case 2 -> {
                    if (this.animTicks > 69) {
                        this.resetAnimState();
                        this.setLeft(this.getRandom().nextBoolean());
                        this.spinCD = 100 + this.getRandom().nextInt(60);
                    } else if (this.animTicks < 50 && this.animTicks % 5 == 0) {
                        this.playSound(HBSounds.ENDGEL_SHOOT.get(), 2.5F, this.getRandom().nextFloat() * 0.5F + 0.75F);
                        EndgelBullet scream = new EndgelBullet(HBEntities.ENDGEL_BULLET.get(), this.level());
                        Vec3 v = new Vec3(Mth.sin(projAngle * Mth.DEG_TO_RAD) * 2, Mth.cos(projAngle * Mth.DEG_TO_RAD) * 2, 0);

                        v = v.yRot(-(this.getYRot() + (this.getLeft() ? -90 : 90)) * Mth.DEG_TO_RAD);

                        scream.moveTo(this.position().add(v));
                        scream.shootFromRotation(this, this.projAngle, this.getLeft() ? -90 + this.getYRot() : 90 + this.getYRot(), 0.0f, 2, 0);
                        scream.setTarget(this.getTarget());
                        level().addFreshEntity(scream);
                        this.projAngle+= 36;
                    }
                }
                case 3 -> {

                    if (this.animTicks > 40) {
                        this.animTicks = 0;
                        this.setAnimState(4);
                    } else if (this.animTicks % 10 == 0) {
                        this.level().broadcastEntityEvent(this, (byte)39);
                        this.playSound(HBSounds.ENDGEL_EXPLOSION.get(), 2.5F, 0.1F + animTicks / 40F);
                    }
                    if (this.getTarget() != null && this.animTicks % 2 == 0) {
                        this.lookAt(this.getTarget(), 60F, 60F);
                        this.getLookControl().setLookAt(this.getTarget(), 60F, 60F);
                    }
                }
                case 4 -> {
                    if (this.animTicks < 10 && this.getTarget() != null && this.animTicks % 2 == 0) {
                        this.lookAt(this.getTarget(), 60F, 60F);
                        this.getLookControl().setLookAt(this.getTarget(), 60F, 60F);
                    } else if (this.animTicks == 10) {

                        EndgelBlast scream = new EndgelBlast(HBEntities.ENDGEL_BLAST.get(), this.level());
                        scream.moveTo(this.getEyePosition().add(this.getLookAngle().scale(2F)));
                        scream.shootFromRotation(this, this.getXRot() * 0.92F, this.getYRot(), 0.0f, 1, 0);
                        scream.setTarget(this.getTarget());
                        level().addFreshEntity(scream);

                        this.playSound(HBSounds.ENDGEL_SHOOT.get(), 2.5F, this.getRandom().nextFloat() * 0.25F + 0.75F);
                        this.level().broadcastEntityEvent(this, (byte)39);
                    }
                    else if (this.animTicks > 23) {
                        this.resetAnimState();
                        this.screamCD = 200;
                    }
                }
            }
        }
        this.attackCD = Math.max(this.attackCD - 1, 0);
        this.spinCD = Math.max(this.spinCD - 1, 0);
        this.screamCD = Math.max(this.screamCD - 1, 0);

    }



    @Override
    public void setUpAnimStates() {
        int state = this.getAnimState();
        this.idleAnimationState.animateWhen(true, this.tickCount);
        this.shootAnimationState.animateWhen(state == 1, this.tickCount);
        this.spinAnimationState.animateWhen(state == 2, this.tickCount);
        this.screamWindupAnimationState.animateWhen(state == 3, this.tickCount);
        this.screamAnimationState.animateWhen(state == 4, this.tickCount);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void clientTick() {
        super.clientTick();
        this.tickTrailYaw();
        this.tickRoll();
        for (int i =0; i < 2; i++) {
        Vec3 rand = EntityHelpers.getRandomVec3(this.getRandom(), 1.5);
        this.level().addParticle(HBParticles.ENDGEL_TRAIL.get(), this.getX() + rand.x + rand.x,
                this.getY() + rand.y + 1, this.getZ() + rand.z, rand.x, rand.y + 0.2, rand.z);
        }

    }

    private void tickTrailYaw() {
        this.prevTrail = this.trail;
        this.trail = Mth.rotLerp(0.2F, this.trail, yBodyRotO - yBodyRot) * 0.8F;
    }

    public float getTrailYaw(float partialTick) {
        return (this.prevTrail + (this.trail - this.prevTrail) * partialTick);
    }


    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    public boolean getLeft() {
        return this.entityData.get(LEFT);
    }

    public void setLeft(boolean b) {
        this.entityData.set(LEFT, b);
    }

    public boolean canSpin() {
        return this.spinCD == 0;
    }

    public void setSpin() {
        this.setAnimState(2);
        this.projAngle = -180;
    }

    @Override
    public void playAmbientSound() {
        this.playSound(HBSounds.ENDGEL_AMBIENT.get(), 2.5F, 1.0F - this.getRandom().nextFloat() * 0.5F);
    }

    @Override
    protected void playHurtSound(DamageSource pSource) {
        this.playSound(HBSounds.ENDGEL_AMBIENT.get(), 2.5F, 0.5F - this.getRandom().nextFloat() * 0.5F);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return HBSounds.ENDGEL_AMBIENT.get();
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    public boolean canScream(double attackReach, double dist) {
        return this.screamCD == 0 && attackReach * 0.75F > dist;
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.attackCD == 0 && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * 15 * this.getBbWidth() * 15 + entity.getBbWidth();
    }

    private void tickRoll() {
        this.roll = Mth.rotLerp(0.05F, this.roll, Mth.clamp((this.yRotO - this.getYRot()) * 0.1F, -0.45F, 0.45F));
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 39) {
            Vec3 v = this.getEyePosition().add(this.getLookAngle().scale(3.5F));
            this.level().addParticle(new EndgelScreamParticleOptions(Mth.clamp(-this.getXRot(), -35, 35), this.getYRot(), 3F), true, v.x, v.y, v.z, 0, 0, 0);

        } else {
            super.handleEntityEvent(pId);
        }
    }

    public static boolean canSpawn(EntityType<? extends EndgelEntity> endgel, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {

        return pLevel.getBlockState(pPos).isAir() && pPos.getY() >= 64 && pPos.getY() <= 72;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

}
