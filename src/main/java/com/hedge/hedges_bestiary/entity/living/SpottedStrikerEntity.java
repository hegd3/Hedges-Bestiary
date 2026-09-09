package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.AI.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.AI.goal.AvoidTargetWhenLowGoal;
import com.hedge.hedges_bestiary.entity.AI.goal.CustomSwimGoal;
import com.hedge.hedges_bestiary.entity.AI.goal.FindAndPickItemGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.AI.goal.specific.SpottedStrikerAttackGoal;
import com.hedge.hedges_bestiary.entity.AI.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBAquaticMob;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.CommonPredicates;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.registry.HBTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class SpottedStrikerEntity extends HBAquaticMob implements AttackStateMob {

    private static final Predicate<LivingEntity> SPOTTED_STRIKER_TARGETS = living -> living.getType().is(HBTags.SPOTTED_STRIKER_TARGETS);

    private static final EntityDataAccessor<Boolean> CLOAKED = SynchedEntityData.defineId(SpottedStrikerEntity.class, EntityDataSerializers.BOOLEAN);

    private float prevCloakProgress = 0.0f;
    private float cloakProgress = 0.0f;


    private int attackCD = 0;
    private int superBiteCD = 0;
    private int cloakCD = 0;
    private float prevTrail;
    private float trail = 0.0f;
    private int pulseCD = 100;
    private int eatTicks = 0;
    private boolean pulse = false;

    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState superBiteAnimationState = new AnimationState();

    public SpottedStrikerEntity(EntityType<? extends SpottedStrikerEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 5, 0.02f, 0.0f);
        this.lookControl = new SmoothSwimmingLookControl(this, 5);
    }


    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.FOLLOW_RANGE, 25F)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLOAKED, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey == CLOAKED && this.level().isClientSide()) {
            this.pulse = false;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SpottedStrikerFleeGoal(this));
        this.goalSelector.addGoal(1, new FindAndPickItemGoal(this, CommonPredicates.EATS_FISH));

        this.goalSelector.addGoal(2, new SpottedStrikerAttackGoal(this));
        this.goalSelector.addGoal(4, new CustomSwimGoal(this, 1.0f, 30, 10, 5, false));

        this.targetSelector.addGoal(0, new HBHurtByTargetGoal(this, false, null));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, SPOTTED_STRIKER_TARGETS));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return this.getBbHeight() * 0.4f;
    }

    @Override
    public boolean isInvisible() {
        if (!this.level().isClientSide && this.isCloaked()) {
            return true;
        }
        if (this.cloakProgress == 5.0F) {
            return true;
        }
        return super.isInvisible();
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (this.isCloaked()) {
            this.setCloaked(false);
        }
    }

    @Override
    public void aiStep() {
        this.flop();
        super.aiStep();
    }

    @Override
    public void serverTick() {
        this.attackCD = Math.max(attackCD - 1, 0);
        this.superBiteCD = Math.max(superBiteCD - 1, 0);
        if (!this.getMainHandItem().isEmpty()) {
            this.eatTicks++;
            if (this.eatTicks >= 17) {
                this.heal(10);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.playSound(SoundEvents.GENERIC_EAT);
            }
        }
        if (!this.isCloaked()) {
            this.cloakCD = Math.max(cloakCD - 1, 0);
        }
        int animState = this.getAnimState();
        if (animState > 0) {
            animTicks++;
            LivingEntity target = this.getTarget();
            switch (animState) {
                case 1 -> {
                    if (this.animTicks == 8 && target != null) {
                        if (AttackHelpers.singleTargetHitbox(this, target, this.getLookAngle().scale(1.4), 1.4, 1.4, 1.4)) {
                            this.doHurtTarget(target);
                        }
                    } else if (this.animTicks >= 17) {
                        this.attackCD = 5;
                        this.resetAnimState();
                    }
                }
                case 2 -> {
                        if (this.animTicks == 22) {
                        Vec3 v = EntityHelpers.bodyAngle(this);
                        this.addDeltaMovement(v.scale(0.6));
                        List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, v.scale(1.5), 2, 2, 2, 5);
                        for (LivingEntity entity : hit) {
                            if (!AttackHelpers.blockBreak(entity)) {
                                AttackHelpers.betterHurt(this, entity, 2f, 1.4f);
                            }
                        }
                    }  else if (this.animTicks >= 29) {
                        this.resetAnimState();
                        this.superBiteCD = 200;
                    }
                }
            }
        }
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }

    }




    @Override
    protected void clientTick() {
        super.clientTick();
        this.tickTrailYaw();
        this.tickCloak();
    }

    private void tickCloak() {
        this.prevCloakProgress = this.cloakProgress;
        if (this.isCloaked()) {
            if (!this.pulse) {
                if (this.cloakProgress < 5.0F) {
                    this.cloakProgress += 0.5f;
                } else {
                    this.pulse = true;
                }
            } else {
                if (this.pulseCD <= 0) {
                    if (this.cloakProgress > 4.0F) {
                        this.cloakProgress -= 0.1f;
                    } else {
                        this.pulseCD = 100;
                    }
                } else {
                    if (this.cloakProgress < 5.0F) {
                        this.cloakProgress += 0.1f;
                    } else {
                        this.pulseCD--;
                    }
                }
            }
        }
        else {
            if (cloakProgress > 0F) {
                this.cloakProgress = Math.max(this.cloakProgress - 0.5f, 0);
            }
        }
    }

    public float getCloakProgress(float partialTicks) {
        return (prevCloakProgress + (cloakProgress - prevCloakProgress) * partialTicks) * 0.2F;
    }


    private void tickTrailYaw() {
        this.prevTrail = this.trail;
        this.trail = Mth.rotLerp(0.2F, this.trail, yBodyRotO - yBodyRot) * 0.8F;
    }

    public float getTrailYaw(float partialTick) {
        return (this.prevTrail + (this.trail - this.prevTrail) * partialTick);
    }

    @Override
    public void setUpAnimStates() {
        super.setUpAnimStates();
        this.biteAnimationState.animateWhen(this.getAnimState() == 1 || !this.getMainHandItem().isEmpty(), this.tickCount);
        this.superBiteAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);

    }

    public boolean canSuperBite(double attackReach, double dist) {
        return this.superBiteCD == 0 && attackReach * 5 >= dist;
    }

    public boolean canCloak(double attackReach, double dist) {
        if (!this.isCloaked() && this.cloakCD == 0 && attackReach * 2 <= dist) {
            this.cloakCD = 100;
            return true;
        }
        return false;
    }

    public boolean isCloaked() {
        return this.entityData.get(CLOAKED);
    }

    public void setCloaked(boolean cloaked) {
        if (cloaked) {
            List<PathfinderMob> mobs = this.level().getEntitiesOfClass(PathfinderMob.class, this.getBoundingBox().inflate(10.0D));
            for (PathfinderMob entity : mobs) {
                if (entity.getLastHurtByMob() == this) {
                    entity.setLastHurtByMob(null);
                }
                if (entity.getTarget() == this) {
                    entity.setTarget(null);
                }
            }
        }
        this.entityData.set(CLOAKED, cloaked);
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        if (this.attackCD > 0)
            return false;
        return attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * 2.2 * this.getBbWidth() * 2.2 + entity.getBbWidth();
    }

    public static boolean canSpawn(EntityType<SpottedStrikerEntity> entity, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entity, level, reason, pos, random);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FluidPathNavigation(this, pLevel);
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    private static class SpottedStrikerFleeGoal extends AvoidTargetWhenLowGoal {

        private final SpottedStrikerEntity mob;

        public SpottedStrikerFleeGoal(SpottedStrikerEntity mob) {
            super(mob, 1.4, 20, 20, 16, 6);
            this.mob = mob;
        }

        @Override
        public void start() {
            if (!this.mob.isCloaked()) {
                this.mob.setCloaked(true);
            }
        }

        @Override
        public void stop() {
            super.stop();
            if (this.mob.isCloaked()) {
                this.mob.setCloaked(false);
            }
        }
    }
}
