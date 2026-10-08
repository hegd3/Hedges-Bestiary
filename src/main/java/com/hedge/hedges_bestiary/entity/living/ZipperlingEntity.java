package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.AquaticFollowOwnerGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.CustomSwimGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.HBSitWhenOrderedGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.MoveToHomePosGoal;
import com.hedge.hedges_bestiary.entity.ai.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBSchoolingMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.util.SegmentHelper;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ZipperlingEntity extends HBTamableAnimal implements AttackStateMob {
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;

    public final AnimationState biteAnimationState = new AnimationState();
    public final SmoothAnimationState beachedAnimationState = new SmoothAnimationState(0.1F);

    public final SegmentHelper segmentHelper = new SegmentHelper(4,0.1F);

    private int requiredFeeds = 10;

    public ZipperlingEntity(EntityType<? extends HBTamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 8, 0.02f, 0.0f);
        this.setPathfindingMalus(PathType.WATER, 0.0f);

    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.MOVEMENT_SPEED, 0.95F);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.4F, 1.8F, 10, 4));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i, new CustomSwimGoal(this, 1.0f, 10, 30, 5, false));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, HBSchoolingMob.class, true, null));

    }

    @Override
    public void tick() {
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, 20);
        if (this.level().isClientSide) {
            this.tickPitch();
            this.setUpAnimStates();
            this.segmentHelper.tick((this.yBodyRotO - this.yBodyRot) * 0.75F, this.prevPitch - this.pitch);
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
    public InteractionResult interactTameCommands(Player player, @NotNull InteractionHand hand) {
        if (this.isTame() && player == this.getOwner()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(HBItems.LUSCIOUS_TREAT)) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                } else {
                    for (int i = 0; i < 4; i++ ) {
                        this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0F), this.getRandomY() + 0.5F, this.getRandomZ(1.0F), 0.0F, 0.0F, 0.0F);
                    }
                }
                if (--this.requiredFeeds == 0) {
                    this.transform();
                }
                this.playSound(SoundEvents.PARROT_EAT);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.interactTameCommands(player, hand);
    }

    private void transform() {
        Level level = this.level();
        if (!EventHooks.canLivingConvert(this, EntityType.FROG, (timer) -> {
        })) {
            return;
        }

        ZippermouthEntity adult = HBEntities.ZIPPERMOUTH.get().create(this.level());
        if (adult != null) {
            EventHooks.onLivingConvert(this, adult);
            adult.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            adult.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
                adult.setCustomName(this.getCustomName());
                adult.setCustomNameVisible(this.isCustomNameVisible());
            }
            adult.setXRot(this.getXRot());
            adult.setYRot(this.getYRot());
            adult.setYHeadRot(this.getYHeadRot());
            adult.setOwnerUUID(this.getOwnerUUID());
            adult.setTame(true, true);
            adult.setCommand(this.getCommand());
            adult.setHasHome(this.hasHome());
            adult.setHomePos(this.getHomePos());
            adult.setAutoTargetType(this.getAutoTargetType());
            adult.setPersistenceRequired();
            adult.fudgePositionAfterSizeChange(this.getDimensions(this.getPose()));
            this.playSound(SoundEvents.TADPOLE_GROW_UP, 0.15F, 1.0F);
            level.addFreshEntity(adult);
            this.discard();
        }
    }

    private void tickPitch() {
        this.prevPitch = this.pitch;
        float target = (Mth.clamp((float)this.getDeltaMovement().y * 2F, -1.5F, 1.5F)) * -Mth.RAD_TO_DEG;
        this.pitch = Mth.approachDegrees(pitch, target, 5F);

    }

    public float getPitch(float partialTick) {
        return (this.prevPitch + (this.pitch - this.prevPitch) * partialTick);
    }


    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.getAnimState() == 0 && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * this.getBbWidth() * 6 + entity.getBbWidth();
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(this.isInFluidType() || !this.onGround(), this.tickCount);
        this.beachedAnimationState.animateWhen(!this.idleAnimationState.isStarted(), this.tickCount);
        this.biteAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
    }

    @Override
    public SleepType getSleepType() {
        return SleepType.RESTLESS;
    }

    @Override
    public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
        return false;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
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
    public void playIdle() {

    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }



    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FluidPathNavigation(this, pLevel);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return false;
    }
}
