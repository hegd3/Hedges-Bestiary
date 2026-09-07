package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.entity.AI.control.AdvancedTurner;
import com.hedge.hedges_bestiary.entity.AI.goal.*;
import com.hedge.hedges_bestiary.entity.AI.goal.specific.BurodonAttackGoal;
import com.hedge.hedges_bestiary.entity.AI.control.ATMLookControl;
import com.hedge.hedges_bestiary.entity.AI.control.ATMMoveControl;
import com.hedge.hedges_bestiary.entity.AI.navigation.MMPathNavigatorGround;
import com.hedge.hedges_bestiary.entity.AI.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetWhenAwakeGoal;
import com.hedge.hedges_bestiary.entity.types.HBGroupMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.items.TreatItem;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.registry.HBTags;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class BurodonEntity extends HBTamableAnimal implements AttackStateMob, AdvancedTurner, HBGroupMob<BurodonEntity> {

    private static final Predicate<LivingEntity> BURODON_TARGETS = living -> living.getType().is(HBTags.BURODON_TARGETS);

    public final SmoothAnimationState biteAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState jumpAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState roarAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState yawnAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState sighAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState tailFlickAnimationState = new SmoothAnimationState();

    public final SmoothAnimationState airAnimationState = new SmoothAnimationState();

    private BurodonEntity leader;
    private TurnType turnType;
    private boolean jumpAway = false;
    private Vec3 jumpVector;
    public int inAirTimer = 0;

    public float runProgress = 0;
    private int attackCD = 0;
    private int jumpCD = 0;
    private int roarCD = 0;
    private int groupSize = 1;

    public static final int BITE_ANIM = 1;
    public static final int JUMP_ANIM = 2;
    public static final int ROAR_ANIM = 3;
    public static final int YAWN_ANIM = 4;

    public BurodonEntity(EntityType<? extends BurodonEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new ATMMoveControl<>(this, 90);
        this.lookControl = new ATMLookControl<>(this, 90);
        this.setMaxUpStep(1);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new MMPathNavigatorGround(this, pLevel);
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 25F)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }


    @Override
    public InteractionResult interactTameCommands(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!this.isTame() && itemStack.getItem() instanceof TreatItem treat && treat.getTier() > 0) {
            if (this.getAnimState() == ROAR_ANIM) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    this.level().broadcastEntityEvent(this, (byte) 7);
                    this.tame(player);
                    this.heal(this.getMaxHealth());
                }
                this.playSound(SoundEvents.GENERIC_EAT);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.interactTameCommands(player, hand);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new FloatGoal(this));
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this));
        this.goalSelector.addGoal(i++, new AvoidTargetWhenLowGoal(this, 1.4D, 20, 15, 16, 7));
        this.goalSelector.addGoal(i++, new HBFollowOwnerGoal(this, 1.2D, 1.4D, 7.0f, 10.0f));
        this.goalSelector.addGoal(i++, new BurodonAttackGoal(this));
        this.goalSelector.addGoal(i++, new BreedGoal(this, 1.0f));
        this.goalSelector.addGoal(i++, new HBTemptGoal(this, 1.1f, Ingredient.of(HBTags.DAWN_DOVE_FOOD), false));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new NapGoal(this));
        this.goalSelector.addGoal(i++, new GroupFollowLeaderGoal<>(this, 10F, 5F));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 7));
        this.goalSelector.addGoal(i++, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(i++, new IdleAnimationGoal<>(this));
        this.goalSelector.addGoal(i++, new DancingGoal(this));
        this.goalSelector.addGoal(i++, new LeaveGroupGoal<>(this));
        this.goalSelector.addGoal(i, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));
        this.targetSelector.addGoal(5, new TargetWhenAwakeGoal<>(this, LivingEntity.class, BURODON_TARGETS));
        this.targetSelector.addGoal(6, new TargetWhenAwakeGoal<>(this, Player.class, null));

    }


    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        if (source.is(DamageTypeTags.IS_FREEZING)) {
            pAmount *= 0.25F;
        }
        return super.hurt(source, pAmount);
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (!this.isTame() && pEntity instanceof BurodonEntity burodon && !burodon.isTame()) {
            return true;
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setUpAnimStates();
            if (this.inAir() || this.getAnimState() == JUMP_ANIM) {
                this.inAirTimer++;
            } else {
                this.inAirTimer = 0;
                if (this.getDeltaMovement().horizontalDistanceSqr() > 0.01) {
                    if (this.runProgress < 5) {
                        this.runProgress+=0.25f;
                    }
                } else if (this.runProgress > 0) {
                    this.runProgress-=0.25f;
                }
            }

        } else {
            if (this.tickCount % 200 == 0) {
                this.heal(10);
            }
            this.tickCooldowns();
            this.tickAnimState();
        }
    }

    private void tickCooldowns() {
        this.attackCD = Math.max(this.attackCD - 1, 0);
        this.jumpCD = Math.max(this.jumpCD - 1, 0);
        this.roarCD = Math.max(this.roarCD - 1, 0);
    }

    public boolean inAir() {
        return !this.isInFluidType() && !this.onGround();
    }

    private void tickAnimState() {
        if (this.getAnimState() > 0) {
            animTicks++;
            LivingEntity target = this.getTarget();
            switch (this.getAnimState()) {
                case BITE_ANIM -> {
                    if (this.animTicks == 7 && target != null && this.canHurtTarget(target)) {
                        this.doHurtTarget(target);
                    } else if (this.animTicks >= 15) {
                        this.attackCD = 5;
                        this.jumpAway = this.getRandom().nextBoolean();
                        this.resetAnimState();
                    }
                }
                case JUMP_ANIM-> {
                    if (this.getAnimTicks() < 5) {
                        if (target != null) {
                            if (this.jumpAway && jumpVector != null) {
                                this.getLookControl().setLookAt(jumpVector);
                            } else {
                                this.getLookControl().setLookAt(target, 30f, 30f);
                            }
                        }
                    }
                    else if (this.animTicks == 9) {
                        Vec3 v = this.getLookAngle();
                        this.setDeltaMovement(this.getDeltaMovement().add(v.x, 0.4, v.z).scale(1.5));
                        this.level().broadcastEntityEvent(this, (byte)39);
                    } else if (this.animTicks >= 15) {
                        this.jumpCD = 100;
                        this.jumpVector = null;
                        this.jumpAway = false;
                        this.resetAnimState();
                    }
                }
                case ROAR_ANIM -> {
                    if (target != null)
                        this.getLookControl().setLookAt(target);
                    if (this.animTicks >= 39) {
                        this.roarCD = 300 + this.getRandom().nextInt(100);
                        this.resetAnimState();
                    } else if (this.animTicks == 15) {
                        List<LivingEntity> hit = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0F, 2.0F, 4.0F));
                        for (LivingEntity livingEntity : hit) {
                            if (livingEntity != this) {
                                if (this.isAlliedTo(livingEntity)) {
                                    livingEntity.heal(5);
                                } else if (livingEntity.canFreeze()) {
                                    livingEntity.setTicksFrozen(200);
                                }
                            }
                        }
                        this.level().broadcastEntityEvent(this, (byte)40);
                    }
                }
                case YAWN_ANIM -> {
                    if (this.animTicks >= 45 || target != null) {
                        this.resetAnimState();
                    }
                }
                case 5 -> {
                    if (this.animTicks >= 20 || target != null) {
                        this.resetAnimState();
                    }
                }
                case 6 -> {
                    if (this.animTicks >= 35 || target != null) {
                        this.resetAnimState();
                    }
                }
            }
        }
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void resetAnimState() {
        super.resetAnimState();
        this.turnType = TurnType.NORMAL;
    }

    @Override
    public void playIdle() {
        this.setAnimState(this.getRandom().nextInt(3) + YAWN_ANIM);
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
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return this.getBbHeight()/1.38f;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean isTamable() {

        return super.isTamable() && HBConfig.BURODON_IS_TAMABLE;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(HBTags.DAWN_DOVE_FOOD) && super.isFood(pStack);
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(true, this.tickCount);
        this.sitAnimationState.animateWhen(this.isSitting() && !this.isDancing(), this.tickCount);
        this.napAnimationState.animateWhen(this.isNapping(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
        int animState = this.getAnimState();
        this.airAnimationState.animateWhen(inAirTimer > 5, this.tickCount);
        this.biteAnimationState.animateWhen(animState == 1, this.tickCount);
        this.jumpAnimationState.animateWhen(animState == 2, this.tickCount);
        this.roarAnimationState.animateWhen(animState == 3, this.tickCount);
        this.yawnAnimationState.animateWhen(animState == 4, this.tickCount);
        this.tailFlickAnimationState.animateWhen(animState == 5, this.tickCount);
        this.sighAnimationState.animateWhen(animState == 6, this.tickCount);

    }



    @Override
    public void setAttacking() {
        this.setAnimState(BITE_ANIM);
    }

    public boolean canRoar(double attackReach, double dist) {
        return this.roarCD == 0 && attackReach * 6 >= dist;
    }

    public boolean canJump(LivingEntity entity, double attackReach, double dist) {
        if (this.jumpCD > 0)
            return false;
        if (this.getHealth() < 20) {
            this.jumpAway = true;
        }
        if (this.jumpAway) {
            jumpVector = DefaultRandomPos.getPosAway(this, 10, 7, entity.position());
            return jumpVector != null;
        }
        return attackReach * 24 >= dist && attackReach * 6 <= dist;
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.attackCD == 0 && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * 2.2 * this.getBbWidth() * 2.2 + entity.getBbWidth();

    }

    private boolean canHurtTarget(LivingEntity entity) {
        return this.hasLineOfSight(entity) && this.getAttackReachSqr(entity) >= this.distanceToSqr(entity);
    }


    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return HBEntities.BURODON.get().create(level);
    }

    @Override
    public void setTurnType(TurnType turnType) {
        this.turnType = turnType;
    }

    @Override
    public TurnType getTurnType() {
        return this.turnType;
    }

    @Override
    public boolean canNeverFollow() {
        return this.isTame();
    }

    @Override
    public boolean canBeFollowed() {
        return !this.isBaby() && HBGroupMob.super.canBeFollowed();
    }

    @Override
    public BurodonEntity getLeader() {
        return this.leader;
    }

    @Override
    public void setLeader(BurodonEntity leader) {
        this.leader = leader;
    }

    @Override
    public void pathToLeader() {
        if (this.isFollower()) {
            this.getNavigation().moveTo(this.leader, 1.4D);
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
        return 5;
    }

    @Override
    public void addFollower() {
        this.groupSize++;
    }

    @Override
    public void removeFollower() {
        this.groupSize--;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 39) {
            this.level().addParticle(HBParticles.ICE_SHOCKWAVE.get(), true, this.getX(), this.getY() + 0.25F, this.getZ(), 0, 0, 0);
        } else if (pId == 40) {
            this.level().addParticle(HBParticles.ICE_SHOCKWAVE_BIG.get(), true, this.getX(), this.getY() + 0.25F, this.getZ(), 0, 0, 0);
        } else {
            super.handleEntityEvent(pId);

        }
    }
}
