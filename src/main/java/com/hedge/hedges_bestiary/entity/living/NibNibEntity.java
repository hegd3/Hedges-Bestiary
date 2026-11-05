package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBSchoolingMob;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class NibNibEntity extends HBSchoolingMob implements AttackStateMob {
    public final AnimationState attackAnimationState = new AnimationState();

    private static final Predicate<ZippermouthEntity> CAN_FOLLOW = living -> !living.isTame();

    public NibNibEntity(EntityType<? extends NibNibEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 15, 0.02f, 0.1f);
        this.lookControl = new SmoothSwimmingLookControl(this, 20);

    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.9F);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new GroupFollowLeaderGoal<>(this) {
            @Override
            public boolean canUse() {
                return NibNibEntity.this.getTarget() != null && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return NibNibEntity.this.getTarget() != null && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(i++, new FollowZippermouthGoal());
        this.goalSelector.addGoal(i++, new GenericMeleeGoal<>(this, 1.3));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0f, 10, 6, 10, true));
        this.goalSelector.addGoal(i, new LeaveGroupGoal<>(this));

        this.targetSelector.addGoal(0, new HBHurtByTargetGoal(this, true, NibNibEntity.class));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void clientTick() {
        super.clientTick();
        if (this.tickCount % 5 == 0) {
            Vec3 vec3 = this.getViewVector(0.0F);
            float f = Mth.cos(this.yBodyRot * Mth.DEG_TO_RAD) * 0.3F;
            float f1 = Mth.sin(this.yBodyRot * Mth.DEG_TO_RAD) * 0.3F;
            float f2 = 1.2F - this.random.nextFloat() * 0.7F;

            this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - vec3.x * (double)f2 + (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f2 + (double)f1, -vec3.x, vec3.y, -vec3.z);
            this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - vec3.x * (double)f2 - (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f2 - (double)f1, -vec3.x, vec3.y, -vec3.z);
        }

    }

    @Override
    protected void serverTick() {
        if (this.getAnimState() == 1) {
            this.animTicks++;
            if (this.animTicks == 8) {
                this.addDeltaMovement(this.getLookAngle().scale(0.35F));
            }
            else if (this.animTicks == 10) {
                LivingEntity entity = this.getTarget();
                if (entity != null && this.withinAttackRange(entity, this.getAttackReachSqr(entity), this.distanceTo(entity))) {
                    if (this.doHurtTarget(entity)) {
                        this.heal(5F);
                    }
                }
            } else if (this.animTicks > 23) {
                this.resetAnimState();
            }
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    public int getMaxGroupSize() {
        return 15;
    }

    @Override
    public void aiStep() {
        this.flop();
        super.aiStep();
    }

    @Override
    public void setUpAnimStates() {
        super.setUpAnimStates();
        this.attackAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
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
    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity instanceof NibNibEntity && pEntity.getTeam() == this.getTeam()) {
            return true;
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new WaterBoundPathNavigation(this, pLevel);
    }

    @Override
    public boolean isPushable() {
        return this.getAnimState() != 1 && super.isPushable();
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.getAnimState() == 0 && this.withinAttackRange(entity, attackReach, dist);
    }

    private boolean withinAttackRange(LivingEntity entity, double attackReach, double dist) {
        return this.hasLineOfSight(entity) && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * this.getBbWidth() * 4 + entity.getBbWidth();
    }

    private class FollowZippermouthGoal extends FollowEntityGoal<ZippermouthEntity> {

        public FollowZippermouthGoal() {
            super(NibNibEntity.this, 1.3F, ZippermouthEntity.class, CAN_FOLLOW, 30, 8);
        }

        @Override
        public boolean canUse() {
            if (NibNibEntity.this.isFollower()) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected boolean withinDist() {
            LivingEntity entity = NibNibEntity.this.getTarget();
            if (entity != null && entity.distanceToSqr(this.followTarget) <= 300) {
                return false;
            }
            return super.withinDist();
        }


    }

}
