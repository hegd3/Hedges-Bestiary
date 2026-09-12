package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.blocks.HBBlocks;
import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.ai.control.FlyingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBPathNavigatorGround;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.types.*;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.registry.HBTags;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ZappetEntity extends TamableFlyer implements HBGroupMob<ZappetEntity>, EggLayer {

    private static final EntityDataAccessor<Optional<BlockPos>> TARGETED_BLOCK_POS = SynchedEntityData.defineId(ZappetEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(ZappetEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(ZappetEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final SmoothAnimationState shootAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState callAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState rideAnimationState = new SmoothAnimationState(0.1F);

    @Nullable
    private ZappetEntity leader;

    private int chargeTicks = 0;
    private int groupSize = 1;
    private float prevGlowProgress = 1.0f;
    private float glowProgress = 1.0f;
    private boolean pulse = false;





    public ZappetEntity(EntityType<? extends ZappetEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(Attributes.FOLLOW_RANGE, 35F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }



    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult type = super.mobInteract(player, hand);
        if (type == InteractionResult.PASS && this.isTamable()) {
            if (this.isTame()) {
                if (Objects.equals(this.getOwnerUUID(), player.getUUID())) {
                    if (this.isFlying() && !this.level().isClientSide) {
                        this.setFlying(false);
                    }
                    this.startRiding(player);
                    return InteractionResult.SUCCESS;
                }
            } else {
                ItemStack itemStack = player.getItemInHand(hand);
                if (itemStack.is(HBTags.TREATS)) {
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
        }
        return type;
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGETED_BLOCK_POS, Optional.empty());
        builder.define(HAS_EGG, false);
        builder.define(CHARGED, false);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new FloatGoal(this));
        this.goalSelector.addGoal(i++, new ZappetOnHeadOverrideGoal());
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new FlyerFollowOwnerGoal(this, 1.2D, 1.6D, 8.0f, 5.0f));
        this.goalSelector.addGoal(i++, new ZappetProjectileShieldGoal(this));
        this.goalSelector.addGoal(i++, new FlockingGoal<>(this) {
            @Override
            public boolean canUse() {
                return !this.mob.isTame() &&  super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !this.mob.isTame() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(i++, new FlyerMoveToHomePosGoal(this, 1.0D, 32, 2d));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new SemiFlyerFlyingGoal<>(this, 1.0f, 25, 10, 20, 1600));
        this.goalSelector.addGoal(i++, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 10));
        this.goalSelector.addGoal(i++, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(i++, new IdleAnimationGoal<>(this));
        this.goalSelector.addGoal(i++, new LeaveGroupGoal<>(this));
        this.goalSelector.addGoal(i, new DancingGoal(this));

        this.targetSelector.addGoal(2, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
    }

    @Override
    public boolean hurt(DamageSource source, float pAmount) {

        if (source.is(DamageTypeTags.IS_LIGHTNING) || checkTridentThrow(source.getDirectEntity())) {
            if (!this.isCharged() && !this.level().isClientSide) {
                this.setCharged(true);
                this.chargeTicks = 400;
            }
            return false;
        }
        return super.hurt(source, pAmount);
    }

    private boolean checkTridentThrow(Entity entity) {
        return entity instanceof ThrownTrident trident && trident.isChanneling() && this.level().isThundering() && this.level().canSeeSky(this.blockPosition());
    }


    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity instanceof ZappetEntity zappet && Objects.equals(zappet.getOwnerUUID(), this.getOwnerUUID())) {
            return true;
        }
        return super.isAlliedTo(pEntity);
    }



    @Override
    public void tick() {
        super.tick();
        this.tickOnHead();
        if (this.level().isClientSide()) {
            this.setUpAnimStates();
            this.tickGlow();
            if (this.hasEgg() && this.onGround()) {
                this.tickDig();
            }
        } else {
            if (this.tickCount % 200 == 0) {
                this.heal(5);
            }
            if (this.isCharged()) {
                if (--this.chargeTicks <= 0) {
                    this.setCharged(false);
                }
            }
            if (this.getAnimState() > 0) {
                this.animTicks++;
                switch (this.getAnimState()) {
                    case 1 -> {
                        if (this.animTicks > 20) {
                            this.resetAnimState();
                        }
                    }
                    case 2 -> {
                        if (this.animTicks > 10) {
                            this.resetAnimState();
                        } else if (this.animTicks == 3) {
                            if (this.getTargetedPos() != null) {
                                this.setTargetedPos(null);
                            }
                        }
                    }
                }
            }
        }
    }

    private void tickOnHead() {
        if (this.isPassenger()) {
            Entity entity = this.getVehicle();
            if (!this.isInFluidType() && !entity.isCrouching()) {
                this.setXRot(entity.getXRot() / 2);
                this.setYRot(Mth.lerp(0.1F, this.getYRot(), entity.getYRot()));
                this.setYHeadRot(entity.getYHeadRot());
                this.yBodyRot = Mth.lerp(1F, yBodyRot, yHeadRot);
                if (entity.getDeltaMovement().y < 0) {
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0.7, 1));
                }
                entity.fallDistance = 0;
            } else {
                this.stopRiding();
            }
        }
    }


    private void tickGlow() {
        this.prevGlowProgress = this.glowProgress;
        if (this.isCharged()) {
            Vec3 rand = EntityHelpers.getRandomVec3(this.getRandom(), 0.6);
            this.level().addParticle(HBParticles.ELECTRIC_SPARKS.get(), true, this.getX() + rand.x + rand.x,
                    this.getY() + rand.y + 0.5, this.getZ() + rand.z, rand.x, rand.y + 0.2, rand.z);
        }
        else if (this.getAnimState() > 0) {
            if (!this.pulse) {

                if (this.glowProgress < 5.0F) {
                    Vec3 rand = EntityHelpers.getRandomVec3(this.getRandom(), 0.6);
                    this.level().addParticle(HBParticles.ELECTRIC_SPARKS.get(), true, this.getX() + rand.x + rand.x,
                            this.getY() + rand.y + 0.5, this.getZ() + rand.z, rand.x, rand.y + 0.2, rand.z);
                    this.glowProgress += 0.5f;
                } else {
                    this.pulse = true;
                }
            } else {

                if (this.glowProgress > 1.0F) {
                    this.glowProgress -= 0.5f;
                }
                this.pulse = false;
            }
        } else {
            if (this.glowProgress > 1.0F) {
                this.glowProgress -= 0.5f;
            } else if (this.pulse) {
                this.pulse = false;
            }
        }
    }

    public float getGlowProgress(float partialTicks) {
        return (prevGlowProgress + (glowProgress - prevGlowProgress) * partialTicks) * 0.2F;
    }



    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (pKey == TARGETED_BLOCK_POS && this.level().isClientSide()) {
            BlockPos pos = this.getTargetedPos();
            if (pos != null) {
                this.level().addParticle(HBParticles.LIGHTNING_EXPLODE.get(), true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
            }
        } else if (pKey == CHARGED) {
            if (this.isCharged() && this.level().isClientSide()) {
                this.glowProgress = 5.0F;
                this.pulse = false;
            }
        }
        else {
            super.onSyncedDataUpdated(pKey);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.isFlying() && !this.onGround() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.isFlying() && this.getDeltaMovement().y < 0 && this.getXRot() > 0) {
            f = 0;
        }
        else if (this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6f, 1f);
        } else {
            f = 0;
        }

        this.walkAnimation.update(f, 0.2f);
    }



    @Override
    protected void dive() {

    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(this.isAlive(), this.tickCount);
        this.sitAnimationState.animateWhen((this.isSitting() || this.isPassenger()) && !this.isDancing(), this.tickCount);
        this.rideAnimationState.animateWhen(this.isPassenger() && !this.getVehicle().onGround(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
        this.callAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
        this.shootAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);
    }

    @Override
    public void switchNav(boolean flying) {
        if (flying) {
            this.moveControl = new FlyingMoveControl(this, 45, 20, 1.1f);
            this.lookControl = new SmoothSwimmingLookControl(this, 30);
            this.navigation = new FlyingPathNavigation(this, this.level());
        } else {
            this.lookControl = new LookControl(this);
            this.moveControl = new MoveControl(this);
            this.navigation = new HBPathNavigatorGround(this, this.level());
        }
    }

    @Override
    public boolean canNeverFollow() {
        return this.isTame();
    }

    @Override
    public ZappetEntity getLeader() {
        return this.leader;
    }

    @Override
    public void setLeader(ZappetEntity leader) {
        this.leader = leader;
    }

    @Override
    public void pathToLeader() {
        if (this.isFollower()) {
            this.getNavigation().moveTo(this.leader, 1.2f);
        }
    }

    @Override
    public boolean canBeFollowed() {
        if (this.isTame() || this.isBaby()) {
            return false;
        }
        return HBGroupMob.super.canBeFollowed();
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
        return 20;
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
    public void playIdle() {
        this.setAnimState(1);
        this.playSound(HBSounds.ZAP.get(), 2.0F, this.getRandom().nextFloat() * 0.5F + 1F);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return HBEntities.ZAPPET.get().create(level);
    }



    @Override
    public BlockState getEgg() {
        return HBBlocks.ZAPPET_EGG.get().defaultBlockState();
    }

    @Override
    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    @Override
    public void setHasEgg(boolean b) {
        this.entityData.set(HAS_EGG, b);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean b) {
        this.entityData.set(CHARGED, b);
    }

    @Override
    public boolean laysMultipleEggs() {
        return true;
    }

    public void setTargetedPos(@Nullable BlockPos pos) {
        this.entityData.set(TARGETED_BLOCK_POS, Optional.ofNullable(pos));
    }

    public BlockPos getTargetedPos() {
        return this.entityData.get(TARGETED_BLOCK_POS).orElse(null);
    }

    @Override
    public SleepType getSleepType() {
        return SleepType.RESTLESS;
    }

    @Override
    public boolean isTamable() {
        return super.isTamable() && HBConfig.ZAPPET_IS_TAMABLE;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return super.isFood(pStack) && pStack.is(Items.RAW_COPPER);
    }

    private static class ZappetProjectileShieldGoal extends Goal {
        private final ZappetEntity zappet;
        private Projectile found;
        public ZappetProjectileShieldGoal(ZappetEntity zappet) {
            this.zappet = zappet;
        }
        @Override
        public boolean canUse() {
            if (zappet.isBaby() || zappet.getAnimState() == 2) return false;
            List<Projectile> list = this.zappet.level().getEntitiesOfClass(Projectile.class, this.zappet.getBoundingBox().inflate(5.0D));
            if (list.isEmpty()) {
                found = null;
                return false;
            } else {
                for (Projectile proj : list) {
                    if (proj.getType().is(HBTags.BYPASSES_ZAPPET_SHIELD)) {
                        continue;
                    }
                    if (proj.getOwner() == null || !zappet.isAlliedTo(proj.getOwner())) {
                        found = proj;
                        return true;
                    }
                }
            }

            return false;
        }


        @Override
        public void start() {
            this.zappet.setTargetedPos(found.blockPosition());
            this.zappet.resetAnimState();
            this.zappet.lookAt(found, 90, 30);
            this.zappet.getLookControl().setLookAt(found, 90, 30);
            this.zappet.setAnimState(2);
            this.zappet.playSound(HBSounds.ZAP.get(), 2.0F, zappet.getRandom().nextFloat() * 0.5F + 1F);
            found.discard();
            found = null;
        }
    }

    private class ZappetOnHeadOverrideGoal extends Goal {

        public ZappetOnHeadOverrideGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return ZappetEntity.this.isPassenger();
        }
        @Override
        public boolean canContinueToUse() {
            return ZappetEntity.this.isPassenger();

        }
    }
}
