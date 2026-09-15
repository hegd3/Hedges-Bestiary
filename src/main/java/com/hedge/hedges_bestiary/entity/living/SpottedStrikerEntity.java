package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.specific.SpottedStrikerAttackGoal;
import com.hedge.hedges_bestiary.entity.ai.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBAquaticMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.CommonPredicates;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.HBTags;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class SpottedStrikerEntity extends HBTamableAnimal implements AttackStateMob {

    private static final Predicate<LivingEntity> SPOTTED_STRIKER_TARGETS = living -> living.getType().is(HBTags.SPOTTED_STRIKER_TARGETS);

    private static final EntityDataAccessor<Boolean> CLOAKED = SynchedEntityData.defineId(SpottedStrikerEntity.class, EntityDataSerializers.BOOLEAN);

    private float prevCloakProgress = 0.0f;
    private float cloakProgress = 0.0f;
    public float roll = 0.0f;

    private int attackCD = 0;
    private int superBiteCD = 0;
    private int cloakCD = 0;
    private float prevTrail;
    private float trail = 0.0f;
    private int eatTicks = 0;

    public final SmoothAnimationState beachedAnimationState = new SmoothAnimationState(0.1F);
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState superBiteAnimationState = new AnimationState();

    public SpottedStrikerEntity(EntityType<? extends SpottedStrikerEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 5, 0.02f, 0.0f);
        this.lookControl = new SmoothSwimmingLookControl(this, 5);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0f);

    }


    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.FOLLOW_RANGE, 25F)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLOAKED, false);
    }


    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.2, 1.6, 7.0f, 4.0f));
        this.goalSelector.addGoal(i++, new SpottedStrikerFleeGoal(this));
        this.goalSelector.addGoal(i++, new FindAndPickItemGoal(this, CommonPredicates.EATS_FISH));
        this.goalSelector.addGoal(i++, new SpottedStrikerAttackGoal(this));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0f, 30, 10, 5, false));
        this.goalSelector.addGoal(i, new DancingGoal(this, false));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, SPOTTED_STRIKER_TARGETS));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Player.class, true, null));

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
    protected boolean canOwnerMount(Player player) {
        return false;
    }

    @Override
    protected boolean canOwnerCommand(Player player) {
        return player.isShiftKeyDown();
    }

    @Override
    public void aiStep() {
        if (!this.isInWater() && this.onGround() && this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add(((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4F, ((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
            this.setOnGround(false);
            this.hasImpulse = true;
            this.playSound(SoundEvents.TROPICAL_FISH_FLOP, this.getSoundVolume(), this.getVoicePitch());
        }
        super.aiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setUpAnimStates();
            this.tickTrailYaw();
            this.tickCloak();
            this.tickRoll();
        } else {
            if (this.tickCount % 100 == 0 && this.getLastHurtByMob() == null) {
                this.heal(5F);
            }
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
                        } else if (this.animTicks >= 29) {
                            this.resetAnimState();
                            this.superBiteCD = 200;
                        }
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
    public boolean isFood(ItemStack pStack) {
        return super.isFood(pStack) && pStack.is(ItemTags.FISHES);
    }

    private void tickRoll() {
        this.roll = Mth.rotLerp(0.05F, this.roll, Mth.clamp((this.yRotO - this.getYRot()) * 0.1F, -0.45F, 0.45F));
    }

    private void tickCloak() {
        this.prevCloakProgress = this.cloakProgress;
        if (this.isCloaked()) {
            if (this.getOwner() == Minecraft.getInstance().player) {
                if (this.cloakProgress < 4.5F) {
                    this.cloakProgress += 0.5F;
                }
            } else {
                if (this.cloakProgress < 5.0F) {
                    this.cloakProgress += 0.5F;
                }
            }
        }
        else {
            if (cloakProgress > 0F) {
                this.cloakProgress -= 0.5F;
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
        this.idleAnimationState.animateWhen(this.isInWater() || !this.onGround(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
        this.beachedAnimationState.animateWhen(!this.isInWater() && this.onGround(), this.tickCount);
        this.biteAnimationState.animateWhen(this.getAnimState() == 1 || !this.getMainHandItem().isEmpty(), this.tickCount);
        this.superBiteAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);

    }

    public boolean canSuperBite(double attackReach, double dist) {
        return this.superBiteCD == 0 && attackReach * 5 >= dist;
    }

    public boolean canCloak(double attackReach, double dist) {
        return !this.isCloaked() && this.cloakCD == 0 && attackReach * 2 <= dist;
    }

    public boolean isCloaked() {
        return this.entityData.get(CLOAKED);
    }

    public void setCloaked(boolean cloaked) {
        this.entityData.set(CLOAKED, cloaked);
        if (cloaked) {
            this.cloakCD = 100;
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
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isCloaked() && super.canBeSeenAsEnemy();
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return !this.isCloaked() && super.canBeSeenByAnyone();
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

    @Override
    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(i);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    private void handleAirSupply(int pAirSupply) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(pAirSupply - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }

    }

    public static boolean canSpawn(EntityType<SpottedStrikerEntity> entity, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        int i = level.getSeaLevel();
        int j = i - 20;
        return level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() >= j && pos.getY() <= i + 1;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
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

    @Override
    public void playIdle() {

    }

    @Override
    public SleepType getSleepType() {
        return SleepType.RESTLESS;
    }

    private static class SpottedStrikerFleeGoal extends AvoidTargetWhenLowGoal {

        private final SpottedStrikerEntity mob;

        public SpottedStrikerFleeGoal(SpottedStrikerEntity mob) {
            super(mob, 1.4, 20, 10, 26, 6);
            this.mob = mob;
        }

        @Override
        public void start() {
            super.start();
            if (!this.mob.isCloaked()) {
                this.mob.setCloaked(true);
            }
        }


        @Override
        public void tick() {
            super.tick();
            if (this.mob.tickCount % 20 == 0) {
                this.mob.heal(5.0F);
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
