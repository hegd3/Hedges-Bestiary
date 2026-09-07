package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.AI.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.AI.goal.*;
import com.hedge.hedges_bestiary.entity.AI.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.AI.goal.specific.FerocetusAttackGoal;
import com.hedge.hedges_bestiary.entity.AI.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.types.*;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.CommonPredicates;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.entity.util.MathHelpers;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.message.EntityKeyMessage;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBKeyMappings;
import com.hedge.hedges_bestiary.registry.HBTags;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FerocetusEntity extends HBTamableAnimal implements AttackStateMob, HBGroupMob<FerocetusEntity>, HUDMount {

    private static final ResourceLocation SPRITE = new ResourceLocation(HedgesBestiary.MODID, "textures/gui/mount/ferocetus_hud.png");

    public static final EntityDataAccessor<Boolean> LEFT = SynchedEntityData.defineId(FerocetusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> GRABBED_ENTITY_ID = SynchedEntityData.defineId(FerocetusEntity.class, EntityDataSerializers.INT);
    private static final Predicate<LivingEntity> FEROCETUS_TARGETS = living -> living.getType().is(HBTags.FEROCETUS_TARGETS);

    @Nullable
    private FerocetusEntity leader;
    private int schoolSize = 1;

    private float prevTrail;
    private float trail = 0.0f;


    public int groundTimer = 0;
    public float roll = 0.0f;
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;


    private int jumpCD = 0;
    private int grabTicks = 0;
    private int attackCD = 0;
    private boolean leftWater = false;

    private int tameAttempts  = 2;

    public final SmoothAnimationState biteAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState ramAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState airAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState spinAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState callAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState grabAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState grabbingAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState beachedAnimationState = new SmoothAnimationState(0.1F);

    @Nullable
    private Entity grabbedEntity;

    public FerocetusEntity(EntityType<? extends FerocetusEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 7, 0.02f, 0.0f);
        this.lookControl = new SmoothSwimmingLookControl(this, 0);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0f);
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEFT, false);
        this.entityData.define(GRABBED_ENTITY_ID, -1);

    }


    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (!this.isTame() && pEntity instanceof FerocetusEntity e && !e.isTame()) {
            return true;
        }
        return super.isAlliedTo(pEntity);
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
    public boolean hurt(DamageSource source, float pAmount) {
        if (this.grabbedEntity != null) {
            if (source.getEntity() == this.grabbedEntity) {
                pAmount *= 0.5f;
            }
            boolean hurt = super.hurt(source, pAmount);
            if (hurt && this.getRandom().nextInt(5) == 0) {
                this.releaseGrab();
            }
            return hurt;
        }
        return super.hurt(source, pAmount);
    }

    @Override
    public InteractionResult interactTameCommands(Player player, @NotNull InteractionHand hand) {
        InteractionResult result = super.interactTameCommands(player, hand);
        if (result == InteractionResult.PASS && !this.isTame() && this.isGrabbing() && this.getAnimState() == 4) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(HBItems.HEARTY_TREAT.get())) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (this.tameAttempts-- > 0) {
                        this.level().broadcastEntityEvent(this, (byte) 6);

                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        this.tame(player);
                        this.heal(this.getMaxHealth());
                    }
                    this.resetAnimState();
                    this.releaseGrab();
                    this.playSound(SoundEvents.PARROT_EAT);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return result;
    }

    @Override
    protected boolean canOwnerMount(Player player) {
        if (this.isBaby()) return false;
        return this.isInWater() || !this.onGround();
    }

    @Override
    protected boolean canOwnerCommand(Player player) {
        return player.isShiftKeyDown();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (pKey == GRABBED_ENTITY_ID && this.level().isClientSide() && this.getGrabbedEntityID() == -1) {
            this.grabbedEntity = null;
        }
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public boolean isFollower() {
        return this.leader != null && this.leader.isAlive();
    }

    @Override
    public FerocetusEntity getLeader() {
        return this.leader;
    }

    @Override
    public void setLeader(FerocetusEntity leader) {
        this.leader = leader;
    }

    @Override
    public FerocetusEntity startFollowing(FerocetusEntity pLeader) {
        this.leader = pLeader;
        pLeader.addFollower();
        return pLeader;
    }

    @Override
    public void stopFollowing() {
        this.leader.removeFollower();
        this.leader = null;
    }

    @Override
    public void addFollower() {
        ++this.schoolSize;
    }

    @Override
    public void removeFollower() {
        --this.schoolSize;
    }

    @Override
    public boolean canBeFollowed() {
        return !this.isBaby() && !this.isTame() && this.hasFollowers() && this.schoolSize < this.getMaxGroupSize();
    }

    @Override
    public int getMaxGroupSize() {
        return 4;
    }

    @Override
    public boolean inRangeOfLeader() {
        return this.distanceToSqr(this.leader) <= 400.0D;
    }

    public boolean hasFollowers() {
        return this.schoolSize > 1;
    }

    @Override
    public int getGroupSize() {
        return this.schoolSize;
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.4D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.FOLLOW_RANGE, 35F)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }

    @Override
    protected void registerGoals() {

        int i = 0;
        this.goalSelector.addGoal(i++, new MountOverrideGoal(this));
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.2, 1.6, 7.0f, 4.0f));
        this.goalSelector.addGoal(i++, new FerocetusAttackGoal(this));
        this.goalSelector.addGoal(i++, new BreedGoal(this, 1.0f));
        this.goalSelector.addGoal(i++, new HBTemptGoal(this, 1.1f, Ingredient.of(HBItems.RAW_URKMEAT.get()), false));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new FindAndPickItemGoal(this, CommonPredicates.EATS_FISH));
        this.goalSelector.addGoal(i++, new GroupFollowLeaderGoal<>(this,10F, 7F));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0f, 25, 10, 2, true));
        this.goalSelector.addGoal(i++, new JumpFromWaterGoal(this, 10, 0.7));
        this.goalSelector.addGoal(i++, new IdleAnimationGoal<>(this));
        this.goalSelector.addGoal(i, new LeaveGroupGoal<>(this));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));
        this.targetSelector.addGoal(3, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, FEROCETUS_TARGETS));


    }

    @Override
    public void tick() {
        super.tick();
        boolean mount = this.hasControllingPassenger();
        if (this.level().isClientSide()) {
            this.setUpAnimStates();
            this.tickTrailYaw();
            this.tickPitch();
            this.tickRoll();
        } else {
            this.attackCD = Math.max(this.attackCD - 1, 0);
            this.jumpCD = Math.max(this.jumpCD - 1, 0);
            if (this.tickCount % 200 == 0) {
                this.heal(10);
            }
            if (!this.getMainHandItem().isEmpty()) {
                this.heal(10);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.playSound(SoundEvents.GENERIC_EAT);
            }
            if (this.getAnimState() > 0) {
                this.animTicks++;
                switch (this.getAnimState()) {
                    case 1 -> {
                        if (this.animTicks == 9) {
                            for (LivingEntity entity : AttackHelpers.zoneHitbox(this, EntityHelpers.bodyAngle(this, this.getXRot()).scale(1.3), 2.5, 2.5, 2.5, 5)) {
                                this.doHurtTarget(entity);
                            }
                        } else if (this.animTicks > 22) {
                            this.resetAnimState();
                        }
                    }
                    case 2 -> {
                        this.getNavigation().stop();
                        if (this.animTicks <= 5) {
                            LivingEntity target = this.getTarget();
                            if (target != null) {
                                this.getLookControl().setLookAt(target, 15f, 0f);
                                this.xRotO = 0;
                                this.setXRot(0);
                            }
                        }
                        else if (this.animTicks == 10) {
                            this.addDeltaMovement(EntityHelpers.bodyAngle(this).scale(1.1));
                        }
                        else if (this.animTicks == 15) {
                            for (LivingEntity entity : AttackHelpers.zoneHitbox(this, EntityHelpers.bodyAngle(this, this.getXRot()).scale(1.4), 3, 3, 3, 10)) {
                                if (AttackHelpers.betterHurt(this, entity, 1.8f, 1.8f)) {
                                    EntityHelpers.knockUp(entity, 1.5);
                                }
                            }
                        } else if (this.animTicks >= 30) {
                            this.resetAnimState();
                        } else {
                            this.setXRot(Mth.approachDegrees(this.getXRot(), 0, 20));
                        }
                    }
                    case 3 -> {
                        if (!this.leftWater && !this.isInFluidType()) {
                            this.leftWater = true;
                        } else if (this.leftWater && this.isInFluidType()) {


                            for (LivingEntity entity : AttackHelpers.zoneHitbox(this, Vec3.ZERO, 4, 4, 4, 10)) {
                                if (AttackHelpers.betterHurt(this, entity, 1.3f, 1.2f)) {
                                    EntityHelpers.knockUp(entity, 0.7);
                                }
                            }
                            this.jumpCD = 200;
                            this.resetAnimState();
                        } else if (this.onGround() || (this.animTicks >= 60 && this.isInFluidType())) {
                            this.jumpCD = 200;
                            this.resetAnimState();
                        }
                    }
                    case 4 -> {
                        if (this.isGrabbing()) {
                            if (this.animTicks == 10) {
                                AttackHelpers.betterHurt(this, (LivingEntity)this.grabbedEntity, 0.5F);
                            } else if (this.animTicks > 22) {
                                this.resetAnimState();
                            }
                        } else {
                            if (this.animTicks == 10) {
                                if (mount) {
                                    for (LivingEntity entity : AttackHelpers.zoneHitbox(this, EntityHelpers.bodyAngle(this, this.getXRot()).scale(1.3), 2.5, 2.5, 2.5, 5)) {
                                        if (this.smallEnoughToGrab(entity)) {
                                            this.grab(entity);
                                            this.resetAnimState();
                                            break;
                                        }
                                    }
                                } else if (this.getTarget() != null && this.canGrab(this.getTarget())) {
                                    this.grab(this.getTarget());
                                    this.getNavigation().stop();
                                    this.resetAnimState();
                                }
                            } else if (this.animTicks > 22) {
                                this.resetAnimState();
                            }
                        }
                    }
                    case 5 -> {
                        if (this.animTicks >= 78) {
                            this.setLeft(!this.swingingLeft());
                            this.resetAnimState();
                        }
                    }
                    case 6 -> {
                        if (this.animTicks >= 38) {
                            this.resetAnimState();
                        }
                    }
                }
            }
        }
        if (this.getGrabbedEntityID() != -1 && this.grabbedEntity == null) {
            this.grabbedEntity = this.level().getEntity(this.getGrabbedEntityID());
        }
        if (this.isGrabbing()) {
            this.tickGrab();
        }
        if (this.onGround()) {
            this.groundTimer = 20;

        } else {
            this.groundTimer = Math.max(groundTimer - 1, 0);
        }


    }

    private void tickGrab() {
        if (!this.level().isClientSide()) {

            if (!this.grabbedEntity.isAlive() || this.distanceToSqr(grabbedEntity) > this.getAttackReachSqr((LivingEntity)grabbedEntity) || !this.isInWater() && this.onGround()) {
                this.releaseGrab();
                this.grabTicks = 0;
                return;
            }
            if (!this.hasControllingPassenger()) {
                if (this.grabTicks++ > 100) {
                    this.releaseGrab();
                    this.grabTicks = 0;
                    return;
                } else if (this.getAnimState() == 0 && this.attackCD == 0) {
                    this.setAnimState(4);
                }
            }
        }
        Vec3 v = this.getLookAngle().scale(2F);
        this.grabbedEntity.setDeltaMovement(this.getX() + v.x - grabbedEntity.getX(), this.getY() + v.y - grabbedEntity.getY() - grabbedEntity.getBbHeight(), this.getZ() + v.z - grabbedEntity.getZ());
        this.grabbedEntity.fallDistance = 0.0F;
    }

    private void tickPitch() {
        this.prevPitch = this.pitch;
        float target = (Mth.clamp((float)this.getDeltaMovement().y * 2F, -1F, 2F)) * -Mth.RAD_TO_DEG;
        this.pitch = Mth.approachDegrees(pitch, target, 5F);

    }

    public float getPitch(float partialTick) {
        return (this.prevPitch + (this.pitch - this.prevPitch) * partialTick);
    }


    private void tickTrailYaw() {
        this.prevTrail = this.trail;
        this.trail = Mth.rotLerp(0.2F, this.trail, yBodyRotO - yBodyRot) * 0.8F;
    }

    public float getTrailYaw(float partialTick) {
        return (this.prevTrail + (this.trail - this.prevTrail) * partialTick);
    }

    private void tickRoll() {
        float prevRoll = this.roll;
        float targetRoll = Math.max(-0.45F, Math.min(0.45F, (this.getYRot() - this.yRotO) * 0.1F));
        targetRoll = -targetRoll;
        this.roll = prevRoll + (targetRoll - prevRoll) * 0.05F;
    }

    public void travel(Vec3 pTravelVector) {

        if (isControlledByLocalInstance() && getControllingPassenger() instanceof Player rider) {
            if (this.isInWater()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.005, 0));
                if (this.leftWater) {
                    this.leftWater = false;
                }
                float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
                if (Minecraft.getInstance().options.keyJump.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.06, 0));
                } else if (Minecraft.getInstance().options.keySprint.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.06, 0));
                }

                if (this.getAnimState() == 0) {

                    if (Minecraft.getInstance().options.keyAttack.isDown()) {
                        HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 4));
                    } else if (HBKeyMappings.MOUNT_ABILITY_KEY.isDown()) {
                        HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 5));
                    }


                }
                this.setSpeed(speed);
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            } else if (!this.leftWater && this.groundTimer < 20 && this.pitch < -5F) {
                this.leftWater = true;
                Vec3 horizontal = EntityHelpers.bodyAngle(this, this.pitch * 1.2F).scale(this.getDeltaMovement().length() * 7);
                this.setDeltaMovement(this.getDeltaMovement().add(horizontal.x, horizontal.y * 2, horizontal.z));
            }
        }
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.horizontalCollision && this.level().getFluidState(blockPosition().above()).is(FluidTags.WATER)) {
                final float f1 = this.getYRot() * Mth.DEG_TO_RAD;

                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.1f, 0.025F, Mth.cos(f1) * 0.1f));
            }
        } else {
            super.travel(pTravelVector);
        }

    }



    @Override
    public boolean dismountsUnderwater() {
        return false;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunc) {
        final float angle = (MathHelpers.STARTING_ANGLE * this.yBodyRot);
        double targetY = this.getY() + passenger.getBbHeight();
        double extraX = Mth.sin(Mth.PI + angle) * 0.25;
        double extraZ = Mth.cos(angle) * 0.25;

        moveFunc.accept(passenger, this.getX() + extraX, targetY, this.getZ() + extraZ);
    }

    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);
        if (this.isInWater() && (pPlayer.zza != 0 || this.yya != 0)) {
            float newYaw = Mth.rotLerp(0.1F, this.getYRot(), pPlayer.getYRot());
            this.setYRot(newYaw);
            this.setYHeadRot(pPlayer.getYHeadRot());
            this.setXRot(Mth.clamp(pPlayer.getXRot(), -30, 30));
        } else if (this.onGround()) {
            this.ejectPassengers();
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player pPlayer, Vec3 pTravelVector) {

        if (this.isInWater()) {
            float f1;
            f1 = pPlayer.zza * 0.03F;
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


    @Override
    public boolean canNeverFollow() {
        return this.isTame();
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(this.isInWater(), this.tickCount);
        this.beachedAnimationState.animateWhen(!this.isInWater() && this.onGround(), this.tickCount);
        this.airAnimationState.animateWhen(this.groundTimer == 0 && !this.isInFluidType(), this.tickCount);

        this.biteAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
        this.ramAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);
        this.grabAnimationState.animateWhen(this.getAnimState() == 4, this.tickCount);
        this.grabbingAnimationState.animateWhen(this.isGrabbing(), this.tickCount);
        this.spinAnimationState.animateWhen(this.getAnimState() == 5, this.tickCount);
        this.callAnimationState.animateWhen(this.getAnimState() == 6, this.tickCount);
    }


    @Override
    public boolean isPushable() {
        if (this.isGrabbing() || this.getAnimState() >= 2) {
            return false;
        }
        return super.isPushable();
    }

    @Override
    public void resetAnimState() {
        super.resetAnimState();
        this.attackCD = 5;
    }

    @Override
    public void setAttacking() {
        this.setAnimState(this.getRandom().nextInt(2) + 1);
    }

    public boolean canJump(double attackReach, double dist) {
        if (this.jumpCD == 0 && attackReach * 5 >= dist && EntityHelpers.closeToSurface(this, 5)) {
            this.leftWater = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.attackCD == 0 && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * this.getBbWidth() * 3 + entity.getBbWidth();
    }


    @Override
    public void pathToLeader() {
        if (this.isFollower()) {
            Vec3 pos = this.leader.position().add(0, 5 * this.getRandom().nextDouble() - 5 * this.getRandom().nextDouble(), 0);
            this.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.2f);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FluidPathNavigation(this, pLevel);
    }

    public static boolean canSpawn(EntityType<? extends FerocetusEntity> ferocetus, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        int i = pLevel.getSeaLevel();
        int j = i - 20;
        return pLevel.getFluidState(pPos).is(FluidTags.WATER) && pPos.getY() >= j && pPos.getY() <= i + 1;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }


    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if ((pReason == MobSpawnType.CHUNK_GENERATION || pReason == MobSpawnType.NATURAL)) {
            int groupSize = (int) (this.getMaxGroupSize() * this.getRandom().nextFloat());
            if (groupSize > 0 && !this.level().isClientSide()) {
                for (int i = 0; i < groupSize; i++) {
                    Vec3 rand = EntityHelpers.getRandomVec3(pLevel.getRandom(), 6);
                    FerocetusEntity entity = new FerocetusEntity(HBEntities.FEROCETUS.get(), this.level());
                    entity.moveTo(this.getX() + rand.x, this.getY() + rand.y, this.getZ() + rand.z);
                    entity.startFollowing(this);
                    this.level().addFreshEntity(entity);
                }
            }
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

    }

    @Override
    public @org.jetbrains.annotations.Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return HBEntities.FEROCETUS.get().create(pLevel);
    }

    public boolean swingingLeft() {
        return this.entityData.get(LEFT);
    }

    private void setLeft(boolean b) {
        this.entityData.set(LEFT, b);
    }

    public boolean canGrab(LivingEntity entity) {
        return this.hasLineOfSight(entity) && this.distanceToSqr(entity) <= this.getAttackReachSqr(entity) && this.smallEnoughToGrab(entity);
    }

    public boolean smallEnoughToGrab(LivingEntity entity) {
        return !entity.isVehicle() && !entity.isPassenger() && entity.getBbHeight() < this.getBbHeight() * 1.2f && entity.getBbWidth() < this.getBbWidth() * 0.5f;
    }



    public void grab(Entity entity) {
        this.setGrabbedEntityID(entity.getId());
    }

    public boolean isGrabbing() {
        return this.grabbedEntity != null;
    }

    public Entity getGrabbedEntity() {
        return this.grabbedEntity;
    }

    public void releaseGrab() {
        this.setGrabbedEntityID(-1);
        this.grabbedEntity = null;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return HBSounds.FEROCETUS_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return HBSounds.FEROCETUS_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return HBSounds.FEROCETUS_DIE.get();
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return super.isFood(pStack) && pStack.is(HBItems.RAW_URKMEAT.get());
    }

    @Override
    public void playIdle() {
        if (!this.isVehicle() && this.getRandom().nextBoolean()) {
            this.setAnimState(5);
        } else {
            this.setAnimState(6);
        }

    }

    @Override
    public SleepType getSleepType() {
        return SleepType.RESTLESS;
    }

    @Override
    public boolean canPlayIdle() {
        return this.getTarget() == null && this.getAnimState() == 0 && this.isInWater();
    }

    @Override
    public void playStaticIdle() {

    }

    @Override
    public boolean canPlayStaticIdle() {
        return false;
    }

    @Override
    public boolean isStaticIdling() {
        return false;
    }

    public int getGrabbedEntityID() {
        return this.entityData.get(GRABBED_ENTITY_ID);
    }

    public void setGrabbedEntityID(int i) {
        this.entityData.set(GRABBED_ENTITY_ID, i);
    }

    @Override
    public void renderHUD(GuiGraphics guiGraphics) {
        int screenWidth = guiGraphics.guiWidth(), screenHeight = guiGraphics.guiHeight();
        int imageWidth = 192, imageHeight = 62;
        int x = (screenWidth - imageWidth) / 2;
        guiGraphics.blit(SPRITE, x, screenHeight - 83, 0, 0, imageWidth, imageHeight, 256, 128);
        imageWidth = Math.min((int)(192 * this.getDeltaMovement().length()), 192);

        guiGraphics.blit(SPRITE, x, screenHeight - 31, 0, imageWidth < 95 ? 61 : 70, imageWidth, 10, 256, 128);


        imageHeight = (int) (this.getHealth() / this.getMaxHealth() * 11);
        x = (screenWidth - 13) / 2;

        guiGraphics.blit(SPRITE, x, screenHeight - 31 - imageHeight, 0, 91 - imageHeight, 13, imageHeight, 256, 128);

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
    public boolean canBreatheUnderwater() {
        return true;
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

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (type == 4) {
            this.setAnimState(this.isGrabbing() ? 4 : 1);
        } else if (type == 5) {
            if (!this.isGrabbing()) {
                this.setAnimState(4);
            } else {
                this.releaseGrab();
            }
        } else {
            super.onKeyPacket(keyPresser, type);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {

    }



    @Override
    public void onAboveBubbleCol(boolean pDownwards) {

    }

    @Override
    public void onInsideBubbleColumn(boolean pDownwards) {
    }
}
