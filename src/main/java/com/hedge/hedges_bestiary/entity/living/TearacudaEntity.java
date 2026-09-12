package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.client.particle.SmokeParticleOptions;
import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.CustomSwimGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.FindAndPickItemGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.GroupFollowLeaderGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.LeaveGroupGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.specific.TearacudaAttackGoal;
import com.hedge.hedges_bestiary.entity.ai.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.types.HBBucketableSchoolingMob;
import com.hedge.hedges_bestiary.entity.types.HBSchoolingMob;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.util.CommonPredicates;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class TearacudaEntity extends HBSchoolingMob implements AttackStateMob {


    private static final Predicate<LivingEntity> TEARACUDA_AVOIDS = living -> living.getType().is(HBTags.TEARACUDA_AVOIDS);

    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState frenzyAnimationState = new AnimationState();
    private int frenzyCD = 0;
    private int attackCD = 0;
    private int jumpCD = 0;
    public int groundTimer = 0;

    private float prevTrail;
    private float trail = 0.0f;

    public TearacudaEntity(EntityType<? extends TearacudaEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 10, 0.02f, 0.1f);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(Attributes.FOLLOW_RANGE, 35F)
                .add(Attributes.MOVEMENT_SPEED, 1.8F);
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new AvoidEntityGoal<>(this, LivingEntity.class,10.0F, 1.0D, 1.0D, TEARACUDA_AVOIDS));
        this.goalSelector.addGoal(i++, new FindAndPickItemGoal(this, CommonPredicates.EATS_FISH));
        this.goalSelector.addGoal(i++, new TearacudaAttackGoal(this));
        this.goalSelector.addGoal(i++, new GroupFollowLeaderGoal<>(this));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0f, 10, 15, 5, true));
        this.goalSelector.addGoal(i, new LeaveGroupGoal<>(this));

        this.targetSelector.addGoal(0, new HBHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractSchoolingFish.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, HBBucketableSchoolingMob.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

    }

    @Override
    protected void clientTick() {
        super.clientTick();
        if (!this.isInFluidType() && this.onGround()) {
            this.groundTimer = 20;
        } else {
            this.groundTimer = Math.max(groundTimer - 1, 0);
        }
        this.tickTrailYaw();

    }


    private void tickTrailYaw() {
        this.prevTrail = this.trail;
        this.trail = Mth.rotLerp(0.2F, this.trail, yBodyRotO - yBodyRot) * 0.8F;
    }

    public float getTrailYaw(float partialTick) {
        return (this.prevTrail + (this.trail - this.prevTrail) * partialTick);
    }

    @Override
    protected void serverTick() {
        super.serverTick();
        this.attackCD = Math.max(this.attackCD - 1, 0);
        this.frenzyCD = Math.max(this.frenzyCD - 1, 0);
        this.jumpCD = Math.max(this.jumpCD - 1, 0);
        if (!this.getMainHandItem().isEmpty()) {
            this.heal(5);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.playSound(SoundEvents.GENERIC_EAT);
        }
        if (!this.isInFluidType() && !this.onGround()) {
            Vec3 vec3 = this.getDeltaMovement();
            if (vec3.y * vec3.y < (double)0.03F && this.getXRot() != 0.0F) {
                this.setXRot(Mth.rotLerp(0.2F, this.getXRot(), 0.0F));
            } else if (vec3.length() > (double)1.0E-5F) {
                double d0 = vec3.horizontalDistance();
                double d1 = Math.atan2(-vec3.y, d0) * (double)(180F / (float)Math.PI);
                this.setXRot((float)d1);
            }
        }
        if (this.getAnimState() > 0) {
            animTicks++;
            LivingEntity target = this.getTarget();
            switch (this.getAnimState()) {
                case 1 -> {
                    if (this.animTicks == 8) {
                        if (target != null && AttackHelpers.singleTargetHitbox(this, target, this.getLookAngle(), 1, 1, 2)) {
                            this.doHurtTarget(target);
                        }
                    } else if (this.animTicks >= 13) {
                        this.resetAnimState();
                        this.attackCD = 5;
                    }
                }
                case 2 -> {

                    if (this.animTicks >= 100 || target == null) {
                        this.resetAnimState();
                        this.frenzyCD = 200;
                    } else if (this.animTicks % 5 == 0 && this.isInFluidType()) {
                        Vec3 v = this.getLookAngle();
                        this.setDeltaMovement(v.scale(0.6));
                        List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, v, 2, 2, 2, 5);
                        for (LivingEntity entity : hit) {
                            if (AttackHelpers.betterHurt(this, entity, 0.5f)) {
                                Vec3 rand = EntityHelpers.getRandomVec3(this.getRandom(), 0.5);
                                for (int i = 0; i < 3; i++) {
                                    ((ServerLevel) this.level()).sendParticles(
                                            new SmokeParticleOptions(1 + (float)rand.x, 30, 0xE8675D),
                                            entity.getX(), entity.getY(), entity.getZ(),
                                            1,
                                            rand.x, rand.y, rand.z, 0.01);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
    protected PathNavigation createNavigation(Level pLevel) {
        return new FluidPathNavigation(this, pLevel);
    }

    @Override
    public void setUpAnimStates() {
        super.setUpAnimStates();
        this.biteAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
        this.frenzyAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);

    }

    @Override
    public int getMaxGroupSize() {
        return 8;
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity instanceof TearacudaEntity && pEntity.getTeam() == this.getTeam()) {
            return true;
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public void pathToLeader() {
        if (this.isFollower()) {
            Vec3 pos = this.leader.position().add(0, 5 * this.getRandom().nextDouble() - 5 * this.getRandom().nextDouble(), 0);
            this.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.2f);
        }
    }

    @Override
    public void aiStep() {
        this.flop();
        super.aiStep();
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    public boolean canJump(double attackReach, double dist) {
        if (this.jumpCD == 0 && (!this.isInFluidType() || EntityHelpers.closeToSurface(this, 3)) && attackReach * 5 <= dist) {
            this.jumpCD = 60;
            return true;
        }
        return false;
    }

    public boolean canFrenzy(double attackReach, double dist) {
        if (this.isFollower()) {
            return this.getLeader().getAnimState() == 2;
        } else {
            return this.frenzyCD == 0 && attackReach * 10 >= dist;
        }
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.attackCD == 0 && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * 2.2 * this.getBbWidth() * 2.2 + entity.getBbWidth();
    }

    public static boolean canSpawn(EntityType<TearacudaEntity> entity, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entity, level, reason, pos, random);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        if ((pReason == MobSpawnType.CHUNK_GENERATION || pReason == MobSpawnType.NATURAL)) {
            int groupSize = (int) (this.getMaxGroupSize() * this.getRandom().nextFloat());
            if (groupSize > 0 && !this.level().isClientSide()) {
                for (int i = 0; i < groupSize; i++) {
                    Vec3 rand = EntityHelpers.getRandomVec3(pLevel.getRandom(), 4);
                    TearacudaEntity entity = new TearacudaEntity(HBEntities.TEARACUDA.get(), this.level());
                    entity.moveTo(this.getX() + rand.x, this.getY() + rand.y, this.getZ() + rand.z);
                    entity.startFollowing(this);
                    this.level().addFreshEntity(entity);
                }
            }
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);

    }
}
