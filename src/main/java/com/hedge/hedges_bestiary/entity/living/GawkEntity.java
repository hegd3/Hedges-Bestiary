package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.client.particle.SmokeParticleOptions;
import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticLookControl;
import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBAmphibiousPathNavigator;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBGroupMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.util.CommonPredicates;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class GawkEntity extends HBTamableAnimal implements AttackStateMob, HBGroupMob<GawkEntity> {
    private static final EntityDataAccessor<Boolean> LEFT = SynchedEntityData.defineId(GawkEntity.class, EntityDataSerializers.BOOLEAN);

    public final SmoothAnimationState swimIdleAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState airAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState yawnAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState staticYawnAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState vomitAnimationState = new SmoothAnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState spinAnimationState = new AnimationState();
    public float landProgress = 0;
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;
    public float roll = 0.0f;

    @Nullable
    private GawkEntity leader;
    private int groupSize = 1;

    public GawkEntity(EntityType<? extends GawkEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SemiaquaticMoveControl(this, 999, 10, 0.35f);
        this.lookControl = new SemiaquaticLookControl(this, 1);


        this.setPathfindingMalus(PathType.WATER, 0.0f);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0f);

    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 16F)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LEFT, false);
    }

    @Override
    public void tick() {
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRot, this.yHeadRot, 10);
        if (this.level().isClientSide) {
            this.setUpAnimStates();
            this.tickPitch();
            this.tickRoll();
            if (this.isInWater()) {
                if (this.landProgress > 0) {
                    this.landProgress -=0.25f;
                }
            } else if (this.landProgress < 5) {
                this.landProgress +=0.25f;
            }
        } else {
            if (this.getAnimState() > 0) {
                this.animTicks++;
                switch (this.getAnimState()) {
                    case 1 -> {
                        if (this.animTicks == 10) {
                            LivingEntity entity = this.getTarget();
                            if (entity != null && this.canUseAttack(entity, this.getAttackReachSqr(entity), this.distanceToSqr(entity))) {
                                this.doHurtTarget(entity);
                            }
                        } else if (this.animTicks > 17) {
                            this.resetAnimState();
                        }
                    }
                    case 2 -> {
                        if (this.animTicks > 35) {
                            this.resetAnimState();
                        }
                    }
                    case 3 -> {
                        if (this.animTicks > 24) {
                            this.resetAnimState();
                        }
                    }
                    case 4 -> {
                        if (this.animTicks > 39) {
                            this.resetAnimState();
                        }
                    }
                    case 5 -> {
                        if (this.animTicks == 10) {
                            Vec3 v = this.getLookAngle().scale(0.5F);
                            ((ServerLevel) this.level()).sendParticles(
                                    new SmokeParticleOptions(0.75F, 10, 0x8A8166),
                                    this.getX() + v.x, this.getY(0.5), this.getZ() + v.z,
                                    1,
                                    0, 0, 0, 0.02);
                        } else if (this.animTicks > 24) {
                            this.resetAnimState();
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.horizontalCollision && this.level().getBlockState(this.blockPosition().above()).isAir()) {
                final float f1 = this.getYRot() * Mth.DEG_TO_RAD;
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.08f, 0.04D, Mth.cos(f1) * 0.08f));
            }
        } else {
            super.travel(pTravelVector);
        }

    }

    private void tickRoll() {
        this.roll = Mth.rotLerp(0.05F, this.roll, Mth.clamp((this.yBodyRotO - this.getYRot()) * 0.4F, -0.5F, 0.5F));
    }

    private void tickPitch() {
        this.prevPitch = this.pitch;
        float target = (Mth.clamp((float)this.getDeltaMovement().y * 4F, -1.5F, 1.5F)) * -Mth.RAD_TO_DEG;
        this.pitch = Mth.approachDegrees(pitch, target, 2.5F);

    }

    public float getPitch(float partialTick) {
        return (this.prevPitch + (this.pitch - this.prevPitch) * partialTick);
    }



    @Override
    public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
        return false;
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(!this.isInWater(), this.tickCount);
        this.swimIdleAnimationState.animateWhen(this.isInWater(), this.tickCount);
        this.sitAnimationState.animateWhen(!this.isDancing() && (this.isSitting() || this.isNapping()), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
        this.airAnimationState.animateWhen(!this.isInWater() && !this.onGround(), this.tickCount);
        int i = this.getAnimState();
        this.biteAnimationState.animateWhen(i == 1, this.tickCount);
        this.spinAnimationState.animateWhen(i == 2, this.tickCount);
        this.yawnAnimationState.animateWhen(i == 3, this.tickCount);
        this.staticYawnAnimationState.animateWhen(i == 4, this.tickCount);
        this.vomitAnimationState.animateWhen(i == 5, this.tickCount);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new HBAmphibiousPathNavigator(this, level);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.2, 1.3, 4.0f, 2.0f, true));
        this.goalSelector.addGoal(i++, new GenericMeleeGoal<>(this, 1.3));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new NapGoal(this));
        this.goalSelector.addGoal(i++, new GawkFeedBabiesGoal(this));
        this.goalSelector.addGoal(i++, new IdleInPlaceGoal<>(this));
        this.goalSelector.addGoal(i++, new GroupFollowLeaderGoal<>(this, 7F, 3F));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 5));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0, 10, 30, 6, true, true));
        this.goalSelector.addGoal(i++, new SemiaquaticStrollGoal(this, 1.0));
        this.goalSelector.addGoal(i++, new JumpFromWaterGoal(this, 20, 0.7F));
        this.goalSelector.addGoal(i++, new DancingGoal(this));
        this.goalSelector.addGoal(i++, new IdleAnimationGoal<>(this));
        this.goalSelector.addGoal(i++, new LeaveGroupGoal<>(this));

        this.goalSelector.addGoal(i, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));

    }



    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (!this.isTame() && pEntity instanceof GawkEntity gawk && !gawk.isTame()) {
            return true;
        }
        return super.isAlliedTo(pEntity);
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
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void playIdle() {
        if (this.isInWater() && this.getRandom().nextBoolean()) {
            this.setLeft(this.getRandom().nextBoolean());
            this.setAnimState(2);
        } else {
            this.setAnimState(3);
        }
    }

    @Override
    public boolean canPlayStaticIdle() {
        return super.canPlayStaticIdle() && !this.isInFluidType() && !this.isNapping() && !this.isSitting();
    }

    @Override
    public void playStaticIdle() {
        this.setAnimState(4);
    }

    @Override
    public boolean isStaticIdling() {
        return this.getAnimState() == 4;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return super.isFood(pStack) && pStack.is(ItemTags.FISHES);
    }


    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.hasLineOfSight(entity) && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * this.getBbWidth() * 4 + entity.getBbWidth();
    }

    public boolean swingingLeft() {
        return this.entityData.get(LEFT);
    }

    public void setLeft(boolean b) {
        this.entityData.set(LEFT, b);
    }

    @Override
    public GawkEntity getLeader() {
        return this.leader;
    }

    @Override
    public boolean canNeverFollow() {
        return this.isTame();
    }

    @Override
    public boolean canBeFollowed() {
        return !this.isBaby() && !this.isTame() && HBGroupMob.super.canBeFollowed();
    }

    @Override
    public void setLeader(GawkEntity leader) {
        this.leader = leader;
    }

    @Override
    public void pathToLeader() {
        if (this.isFollower()) {
            this.getNavigation().moveTo(this.leader, 1.2D);
        }
    }

    @Override
    public boolean inRangeOfLeader() {
        return this.distanceToSqr(this.leader) <= 400.0D;
    }

    @Override
    public int getGroupSize() {
        return this.groupSize;
    }

    @Override
    public int getMaxGroupSize() {
        return 10;
    }

    @Override
    public void addFollower() {
        this.groupSize++;
    }

    @Override
    public void removeFollower() {
        this.groupSize--;
    }

    public static boolean canSpawn(EntityType<? extends GawkEntity> gawk, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        int i = pLevel.getSeaLevel();
        return pLevel.getBlockState(pPos.below()).is(BlockTags.ICE) && pPos.getY() >= i && pPos.getY() < (i + 5);
    }

    private static class GawkFeedBabiesGoal extends Goal {
        private int interval = 100;
        private final GawkEntity gawk;
        private AgeableMob baby;
        private boolean reached = false;
        private GawkFeedBabiesGoal(GawkEntity gawk) {
            this.gawk = gawk;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.gawk.isBaby() && !this.gawk.isTame() && this.gawk.getAnimState() != 0) {
                return false;
            }
            if (this.interval > 0) {
                this.interval--;
                return false;
            } else {
                this.interval = 100;
                List<AgeableMob> toFeed = this.gawk.level().getEntitiesOfClass(AgeableMob.class, gawk.getBoundingBox().inflate(6F), CommonPredicates.IS_BABY);
                if (!toFeed.isEmpty()) {
                    this.baby = toFeed.get(0);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return (!this.reached && this.baby != null && this.baby.isAlive()) || this.gawk.getAnimState() == 5;
        }

        @Override
        public void tick() {
            if (!this.reached) {
                this.gawk.getLookControl().setLookAt(this.baby);
                this.gawk.lookAt(this.baby, 90, 90);
                if (this.gawk.distanceToSqr(this.baby) <= 4 + this.baby.getBbWidth() || this.gawk.getNavigation().isStuck() || this.gawk.getNavigation().isDone()) {
                    this.reached = true;
                    this.gawk.setAnimState(5);
                    this.baby.ageUp(200);
                    this.gawk.getNavigation().stop();
                }
            }
        }

        @Override
        public void stop() {
            this.baby = null;
            this.reached = false;
        }

        @Override
        public void start() {
            this.gawk.getNavigation().moveTo(this.baby, 1.2F);
        }
    }

}
