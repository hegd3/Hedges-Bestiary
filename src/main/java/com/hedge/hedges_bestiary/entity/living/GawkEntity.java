package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticLookControl;
import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBAmphibiousPathNavigator;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GawkEntity extends HBTamableAnimal implements AttackStateMob {
    public final SmoothAnimationState swimIdleAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState airAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState yawnAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState staticYawnAnimationState = new SmoothAnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState spinAnimationState = new AnimationState();
    public float landProgress = 0;
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;
    public float roll = 0.0f;

    public GawkEntity(EntityType<? extends HBTamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SemiaquaticMoveControl(this, 999, 10, 0.35f);
        this.lookControl = new SemiaquaticLookControl(this, 1);


        this.setPathfindingMalus(PathType.WATER, 0.0f);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0f);

    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FOLLOW_RANGE, 16F)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Override
    public void tick() {
        super.tick();
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
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new NapGoal(this));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 5));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0, 10, 30, 6, true, true));
        this.goalSelector.addGoal(i++, new SemiaquaticStrollGoal(this, 1.0));
        this.goalSelector.addGoal(i++, new JumpFromWaterGoal(this, 20, 0.7F));
        this.goalSelector.addGoal(i++, new DancingGoal(this));
        this.goalSelector.addGoal(i, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HBHurtByTargetGoal(this, true, TamableAnimal.class));

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
    public void playIdle() {

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

    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return false;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * this.getBbWidth() * 4 + entity.getBbWidth();
    }
}
