package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.CustomSwimGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.JumpFromWaterGoal;
import com.hedge.hedges_bestiary.entity.ai.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBAquaticMob;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.SegmentHelper;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ZippermouthEntity extends HBAquaticMob implements AttackStateMob {
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;
    public final AnimationState biteAnimationState = new AnimationState();
    public final SmoothAnimationState beachedAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState rushAnimationState = new SmoothAnimationState(0.1F);

    public final SegmentHelper segmentHelper = new SegmentHelper(4, 0.1F);

    public ZippermouthEntity(EntityType<? extends ZippermouthEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 3, 0.02f, 0.0f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GenericMeleeGoal<>(this, 2F) {
            @Override
            protected void look(LivingEntity target) {
                ZippermouthEntity.this.lookControl.setLookAt(target, 10.0F, 10.0F);
                ZippermouthEntity.this.lookAt(target, 10F, 10F);
            }
        });
        this.goalSelector.addGoal(1, new CustomSwimGoal(this, 1.0f, 10, 30, 5, 10, true, false));
        this.goalSelector.addGoal(2, new JumpFromWaterGoal(this, 10, 1.1F, 1.4F, false));

        this.targetSelector.addGoal(0, new HBHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) amount *=0.5F;
        return super.hurt(source, amount);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 40.0D)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }



    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance/10F);
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(2.0F);
    }

    public void tick() {
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, 20);
        this.tickPitch();
        if (this.level().isClientSide) {
            this.segmentHelper.tick(this.yBodyRotO - this.yBodyRot, this.prevPitch - this.pitch);
        }
    }

    @Override
    protected void serverTick() {
        if (this.getAnimState() > 0) {
            this.animTicks++;
            switch (this.getAnimState()) {
                case 1 -> {
                    if (this.animTicks == 24) {
                        this.addDeltaMovement(this.getLookAngle().scale(0.3F));
                    }
                    else if (this.animTicks == 28) {
                        List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, this.getLookAngle().scale(0.25F), 3, 3, 3, 10);
                        for (LivingEntity entity : hit) {
                            this.doHurtTarget(entity);
                        }
                    } else if (this.animTicks > 42) {
                        this.resetAnimState();
                    }
                }
            }
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


    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }

    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FluidPathNavigation(this, pLevel);
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
        this.rushAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);
    }
}
