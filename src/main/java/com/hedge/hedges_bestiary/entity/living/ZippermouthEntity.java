package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.goal.specific.ZippermouthAttackGoal;
import com.hedge.hedges_bestiary.entity.ai.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.entity.util.SegmentHelper;
import com.hedge.hedges_bestiary.networking.packet.EntityKeyPacket;
import com.hedge.hedges_bestiary.registry.HBKeyMappings;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZippermouthEntity extends HBTamableAnimal implements AttackStateMob {
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;

    private int suckCD = 0;
    public final AnimationState biteAnimationState = new AnimationState();
    public final SmoothAnimationState beachedAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState suckAnimationState = new SmoothAnimationState(0.1F);

    public final SegmentHelper segmentHelper = new SegmentHelper(4,0.1F);

    private final ZippermouthPartEntity segment1;
    private final ZippermouthPartEntity segment2;
    private final ZippermouthPartEntity segment3;
    private final ZippermouthPartEntity[] segments;

    public ZippermouthEntity(EntityType<? extends ZippermouthEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 3, 0.02f, 0.0f);
        this.setPathfindingMalus(PathType.WATER, 0.0f);
        this.segment1 = new ZippermouthPartEntity(this);
        this.segment2 = new ZippermouthPartEntity(this);
        this.segment3 = new ZippermouthPartEntity(this);

        this.segments = new ZippermouthPartEntity[]{
            segment1, segment2, segment3
        };
    }

    @Override
    protected void registerGoals() {
        int i =0;
        this.goalSelector.addGoal(i++, new MountOverrideGoal(this));
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.4F, 1.8F, 10, 4));
        this.goalSelector.addGoal(i++, new ZippermouthAttackGoal(this));

        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0f, 10, 30, 5, true));
        this.goalSelector.addGoal(i, new JumpFromWaterGoal(this, 10, 1.1F, 1.4F, false));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, FerocetusEntity.class, true, null));
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
                .add(Attributes.MAX_HEALTH, 350.0D)
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

    @Override
    protected boolean canOwnerMount(Player player) {
        return true;
    }

    @Override
    protected boolean shouldPassengersInheritMalus() {
        return true;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.99F;
    }


    @Override
    protected boolean canOwnerCommand(Player player) {
        return player.isShiftKeyDown();
    }

    public void tick() {
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, 20);
        this.tickPitch();
        if (this.level().isClientSide) {
            this.setUpAnimStates();
        }
        this.segmentHelper.tick((this.yBodyRotO - this.yBodyRot) * 0.75F, (this.prevPitch - this.pitch) * 1.5F);
        this.tickMultiPart();
        if (this.suckCD > 0) this.suckCD--;
        if (this.getAnimState() > 0) {
            this.animTicks++;
            switch (this.getAnimState()) {
                case 1 -> {
                    if (!this.level().isClientSide) {
                        if (this.animTicks == 24) {
                            this.addDeltaMovement(this.getLookAngle().scale(0.3F));
                        }
                        else if (this.animTicks == 28) {
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, this.getLookAngle().scale(0.25F), 3, 3, 3, 10);
                            for (LivingEntity entity : hit) {
                                if (!AttackHelpers.blockBreak(entity)) {
                                    this.doHurtTarget(entity);
                                }
                            }
                            this.level().broadcastEntityEvent(this, (byte) 49);
                        } else if (this.animTicks > 42) {
                            this.resetAnimState();
                        }
                    }
                }
                case 2 -> {
                    if (this.animTicks > 20) {
                        if (!this.level().isClientSide) {
                            LivingEntity target = this.getTarget();
                            if (this.animTicks > 60 || target == null || target.distanceToSqr(this) <= this.getAttackReachSqr(target) / 2) {
                                this.resetAnimState();
                                this.suckCD = 200;
                                break;
                            }
                        } else {
                            for (int i = 1; i < 6; i++) {
                                Vec3 pos = this.position().add(Vec3.directionFromRotation(this.getXRot(), this.yHeadRot).scale(i)).yRot((Mth.wrapDegrees(this.getRandom().nextFloat() * 4 - this.getRandom().nextFloat() * 4)) * Mth.DEG_TO_RAD);
                                this.level().addParticle(HBParticles.WATER_SUCK.get(), true, pos.x, pos.y + this.getRandom().nextFloat() * 2F - this.getRandom().nextFloat() * 2F, pos.z, 0, 0, 0);
                            }
                        }

                        if (this.animTicks % 5 == 0 && this.isInWater()) {
                            Vec3 pos = Vec3.directionFromRotation(this.getXRot(), this.yHeadRot).scale(3);
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, pos, 7, 5, 7, 10);
                            for (LivingEntity entity : hit) {
                                if (entity.isInWater()) {
                                    Vec3 v = this.position().add(pos).subtract(entity.position());
                                    entity.setDeltaMovement(entity.getDeltaMovement().lerp(v, Math.max(0.05F - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) / 2, 0.00F)));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);
        if (this.isInWater() && (pPlayer.zza != 0 || this.yya != 0)) {
            float newYaw = Mth.rotLerp(0.07F, this.getYRot(), pPlayer.getYHeadRot());
            this.setRot(newYaw, Mth.clamp(pPlayer.getXRot(), -10, 10));
            this.setYHeadRot(pPlayer.getYHeadRot());
        } else if (this.onGround()) {
            this.ejectPassengers();
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player pPlayer, Vec3 pTravelVector) {

        if (this.isInWater()) {
            float f1;
            f1 = pPlayer.zza * 0.015F;
            if (f1 < 0) f1 = 0;
            return new Vec3(0, 0, f1);

        }
        return Vec3.ZERO;

    }


    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        super.remove(removalReason);
        if (this.segments != null) {
            for (PartEntity part : segments) {
                part.remove(RemovalReason.KILLED);
            }
        }
    }


    private void tickMultiPart() {

        Vec3[] avector3d = new Vec3[this.segments.length];
        for (int j = 0; j < this.segments.length; j++) {
            avector3d[j] = new Vec3(this.segments[j].getX(), this.segments[j].getY(), this.segments[j].getZ());
        }
        Vec3 center = this.position().add(0, this.getBbHeight() * 0.5F, 0);
        this.segment1.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, -3.56), pitch, this.segmentHelper.getYawAtIndex(0, 1.0F) * 20 + this.yBodyRot).add(center));
        this.segment2.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, -3.56), pitch, this.segmentHelper.getYawAtIndex(1, 1.0F) * 20 + this.yBodyRot).add(this.segment1.centeredPosition()));
        this.segment3.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, -3.56), pitch, this.segmentHelper.getYawAtIndex(2, 1.0F) * 20 + this.yBodyRot).add(this.segment2.centeredPosition()));

        for (int l = 0; l < this.segments.length; l++) {
            this.segments[l].xo = avector3d[l].x;
            this.segments[l].yo = avector3d[l].y;
            this.segments[l].zo = avector3d[l].z;
            this.segments[l].xOld = avector3d[l].x;
            this.segments[l].yOld = avector3d[l].y;
            this.segments[l].zOld = avector3d[l].z;
        }
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.segments;
    }


    private Vec3 rotateOffsetVec(Vec3 offset, float xRot, float yRot) {
        return offset.xRot(-xRot * Mth.DEG_TO_RAD).yRot(-yRot * Mth.DEG_TO_RAD);
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

        if (isControlledByLocalInstance() && getControllingPassenger() instanceof Player rider) {
            if (this.isInWater()) {
                if (Minecraft.getInstance().options.keyJump.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.03, 0));
                } else if (Minecraft.getInstance().options.keySprint.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.03, 0));
                }

                if (this.getAnimState() == 0) {

                    if (Minecraft.getInstance().options.keyAttack.isDown()) {
                        PacketDistributor.sendToServer(new EntityKeyPacket(this.getId(), rider.getId(), 4));
                    } else if (HBKeyMappings.MOUNT_ABILITY_KEY.isDown()) {
                        PacketDistributor.sendToServer(new EntityKeyPacket(this.getId(), rider.getId(), 5));
                    }


                }
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D).add(0, 0.002425F, 0));
            }
        }

        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }

    }

    public int getMaxHeadXRot() {
        return 5;
    }

    public int getMaxHeadYRot() {
        return 5;
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FluidPathNavigation(this, pLevel);
    }

    public boolean canRush(double attackReach, double dist) {
        return this.suckCD == 0 && attackReach * 10 >= dist;
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
        this.suckAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);
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
    public boolean isFood(ItemStack pStack) {
        return false;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public void playIdle() {

    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 49) {
            Vec3 v = this.getLookAngle().scale(1.5);
            this.level().addParticle(HBParticles.WATER_EXPLODE.get(), true, this.getX() + v.x, this.getY() + v.y, this.getZ() + v.z, 0, 0, 0);
        }
        super.handleEntityEvent(id);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}
