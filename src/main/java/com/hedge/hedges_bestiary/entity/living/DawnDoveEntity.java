package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.blocks.HBBlocks;
import com.hedge.hedges_bestiary.entity.AI.control.FlyingMoveControl;
import com.hedge.hedges_bestiary.entity.AI.goal.*;
import com.hedge.hedges_bestiary.entity.AI.goal.specific.DawnDoveAttackGoal;
import com.hedge.hedges_bestiary.entity.AI.navigation.MMPathNavigatorGround;
import com.hedge.hedges_bestiary.entity.AI.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.AI.targeting.TargetWhenAwakeGoal;
import com.hedge.hedges_bestiary.entity.projectile.DragonFireBall;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.EggLayer;
import com.hedge.hedges_bestiary.entity.types.HUDMount;
import com.hedge.hedges_bestiary.entity.types.TamableFlyer;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.MathHelpers;
import com.hedge.hedges_bestiary.items.TreatItem;
import com.hedge.hedges_bestiary.message.EntityKeyMessage;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBKeyMappings;
import com.hedge.hedges_bestiary.registry.HBTags;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;


public class DawnDoveEntity extends TamableFlyer implements EggLayer, AttackStateMob, HUDMount {
    private static final ResourceLocation SPRITE = new ResourceLocation(HedgesBestiary.MODID, "textures/gui/mount/dawn_dove_hud.png");
    public static final EntityDataAccessor<Integer> GRABBED_ENTITY_ID = SynchedEntityData.defineId(DawnDoveEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(DawnDoveEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Predicate<LivingEntity> DAWN_DOVE_TARGETS = living -> living.getType().is(HBTags.DAWN_DOVE_TARGETS);
    private static final Predicate<ItemEntity> FOOD_ENTITIES = item -> item.getItem().is(HBTags.DAWN_DOVE_FOOD);
    public final SmoothAnimationState glideAnimationState = new SmoothAnimationState(0.1f);
    public final SmoothAnimationState flyUpAnimationState = new SmoothAnimationState(0.1f);
    public final SmoothAnimationState flyForwardAnimationState = new SmoothAnimationState(0.1f);
    public final SmoothAnimationState clawAttackAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState eatAnimationState = new SmoothAnimationState(0.1f);
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState shootAnimationState = new AnimationState();

    private float prevTrail;
    private float trail = 0.0f;

    private float meterAmount = 1.0F;

    private int clawAttackCD;
    private int attackCD;
    private int shootCD;
    private int eatProgress = 0;

    private int tameAttempts = 3;

    @Nullable
    private Entity grabbedEntity;

    public DawnDoveEntity(EntityType<? extends DawnDoveEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 60F)
                .add(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i, new FloatGoal(this));
        this.goalSelector.addGoal(i++, new MountOverrideGoal(this));
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new EggLayerBreedGoal<>(this, 1.0f));
        this.goalSelector.addGoal(i++, new LayEggsGoal<>(this, 100, 1.0f));
        this.goalSelector.addGoal(i++, new FlyerFollowOwnerGoal(this, 1.2D, 1.6D, 8.0f, 8.0f));
        this.goalSelector.addGoal(i++, new DawnDoveAttackGoal(this));
        this.goalSelector.addGoal(i++, new HBTemptGoal(this, 1.1f, Ingredient.of(HBTags.DAWN_DOVE_FOOD), false));
        this.goalSelector.addGoal(i++, new FindAndPickItemGoal(this, FOOD_ENTITIES));
        this.goalSelector.addGoal(i++, new FlyerMoveToHomePosGoal(this, 1.0D, 32, 2d));
        this.goalSelector.addGoal(i++, new NapGoal(this, false));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new SemiFlyerFlyingGoal<>(this, 1.0f, 45, 25, 60, 800));
        this.goalSelector.addGoal(i++, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 10));
        this.goalSelector.addGoal(i++, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(i, new DancingGoal(this));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));
        this.targetSelector.addGoal(5, new TargetWhenAwakeGoal<>(this, LivingEntity.class,DAWN_DOVE_TARGETS)
        );
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GRABBED_ENTITY_ID, -1);
        this.entityData.define(HAS_EGG, false);
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (pKey == GRABBED_ENTITY_ID && this.level().isClientSide() && this.getGrabbedEntityID() == -1) {
            this.grabbedEntity = null;
        }
        super.onSyncedDataUpdated(pKey);
    }



    @Override
    public void travel(Vec3 vec3d) {


        if (isControlledByLocalInstance() && getControllingPassenger() != null && getControllingPassenger() instanceof Player rider) {
            float speed =(float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
            if (Minecraft.getInstance().options.keyJump.isDown() && this.meterAmount > 0) {
                this.meterAmount-= 0.01F;
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.03, 0));
                if (!this.isFlying()) {
                    this.setFlying(true);
                    HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 3));
                }

            } else if (Minecraft.getInstance().options.keySprint.isDown() && this.isFlying()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.03, 0));
            }

            if (this.getAnimState() == 0) {
                if (Minecraft.getInstance().options.keyAttack.isDown()) {
                    HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 4));
                } else if (this.shootCD == 64 && HBKeyMappings.MOUNT_ABILITY_KEY.isDown()) {
                    this.shootCD = 0;
                    HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 5));
                } else if (Minecraft.getInstance().options.keyUse.isDown() && this.isFlying()) {
                    HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 6));
                }
            }


            this.setSpeed(this.isFlying() ? speed * 4f : speed);

        }
        super.travel(vec3d);
    }

    @Override
    protected void tickRidden(Player pPlayer, Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);
        float turnSpeed = 5.0F;
        float currentYaw = this.getYRot();
        float targetYaw = pPlayer.getYRot();
        float deltaYaw = Mth.wrapDegrees(targetYaw - currentYaw);

        float newYaw = currentYaw + Mth.clamp(deltaYaw, -turnSpeed, turnSpeed);
        this.setYRot(newYaw);
        this.setYHeadRot(pPlayer.getYHeadRot());
        this.setXRot(Mth.clamp(pPlayer.getXRot(), -45, 45));
        if (this.isFlying()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.004, 0));
            if (this.hasControllingPassenger() && this.flyProgress == 5 && this.onGround())
                this.setFlying(false);

        }
    }



    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!this.isTame() && this.grabbedEntity == player && this.isTamable()) {
            if (itemStack.getItem() instanceof TreatItem treat && treat.getTier() > 0) {
                if (!this.level().isClientSide) {
                    this.releaseGrab();
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    this.tameAttempts--;
                    if (tameAttempts == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        this.tame(player);
                        this.heal(this.getMaxHealth());
                    } else {
                        this.level().broadcastEntityEvent(this, (byte)6);
                    }
                }
                this.playSound(SoundEvents.GENERIC_EAT);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return type;
    }




    /*
    @Override
    public void push(Entity pEntity) {
        if (this.hasControllingPassenger() && pEntity.getVehicle() == null) {
            pEntity.startRiding(this);
        }
        super.push(pEntity);
    }

     */

    protected Vec3 getRiddenInput(Player pPlayer, Vec3 pTravelVector) {

        float f1 = pPlayer.zza * (this.isFlying() ? 1.5F : 0.5F);
        if (f1 <= 0.0F)
            f1 *= 0.25F;

        return new Vec3(!this.isFlying() ? pPlayer.xxa * 0.5f : 0, 0.0, f1);


    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity && !this.touchingUnloadedChunk()) {
            final float angle = (MathHelpers.STARTING_ANGLE * this.yBodyRot);
            float flight = this.getFlyProgress(1.0F);
            double targetY = this.getY() + passenger.getBbHeight() + 0.35F * flight;
            double extraX;
            double extraZ;
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(passenger);
                if (i == 0) {
                    extraX = 0.5f * Mth.sin(Mth.PI + angle);
                    extraZ = 0.5f * Mth.cos(angle);
                } else {
                    extraX = -0.5f * Mth.sin(Mth.PI + angle);
                    extraZ = -0.5f * Mth.cos(angle);
                }
            } else {
                extraX = 0.5f * Mth.sin(Mth.PI + angle);
                extraZ = 0.5f * Mth.cos(angle);
            }


            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            moveFunc.accept(passenger, this.getX() + extraX, targetY, this.getZ() + extraZ);
        } else {
            super.positionRider(passenger, moveFunc);
        }

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
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() < 2;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity pPassenger) {
        if (this.isFlying() && pPassenger == this.getFirstPassenger()) {
            this.setFlying(false);
        }
        return super.getDismountLocationForPassenger(pPassenger);
    }

    @Override
    protected void removePassenger(Entity pPassenger) {
        if (this.getControllingPassenger() == pPassenger) {
            if (this.isGrabbing()) {
                this.releaseGrab();
            }
            if (this.isOrderedToSit() && this.isFlying()) {
                this.setFlying(false);
            }
        }
        super.removePassenger(pPassenger);
    }

    @Override
    protected boolean canOwnerMount(Player player) {
        return !this.isBaby();
    }

    @Override
    protected boolean canOwnerCommand(Player player) {
        return player.isShiftKeyDown();
    }

    @Override
    public void tick() {
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, 8);
        if (this.getGrabbedEntityID() != -1 && this.grabbedEntity == null) {
            this.grabbedEntity = this.level().getEntity(this.getGrabbedEntityID());
        }
        if (this.isGrabbing()) {
            this.tickGrab();
        }
        if (this.level().isClientSide()) {
            this.setUpAnimStates();
            this.tickTrailYaw();
            this.shootCD = Math.min(this.shootCD + 1, 64);
            if (this.onGround() && this.meterAmount < 1.0F && this.tickCount % 5 == 0) {
                this.meterAmount+=0.02F;
            }
            if (this.eatAnimationState.isStarted() && this.tickCount % 10 == 0) {
                this.addEatingParticles();
            }
            if (this.hasEgg() && this.onGround()) {
                this.tickDig();
            }
        } else {
            this.attackCD = Math.max(this.attackCD - 1, 0);
            this.shootCD = Math.max(this.shootCD - 1, 0);
            this.clawAttackCD = Math.max(this.clawAttackCD - 1, 0);
            if (!this.getMainHandItem().isEmpty()) {
                this.eatProgress++;
                if (this.eatProgress > 20) {
                    this.eatProgress = 0;
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    this.heal(this.getMaxHealth() / 5);
                } else if (this.eatProgress % 5 == 0) {
                    this.playSound(SoundEvents.GENERIC_EAT);
                }
            }
            if (this.tickCount % 200 == 0 && this.getTarget() == null) {
                this.heal(10);
            }
            if (this.getAnimState() > 0) {
                this.animTicks++;
                switch(this.getAnimState()) {
                    case 1 -> {
                        if (animTicks == 8) {
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, this.getLookAngle().scale(1.2), 2, 2, 2, 2);
                            for (LivingEntity e : hit) {
                                this.doHurtTarget(e);
                            }
                        } else if (animTicks > 15) {
                            this.resetAnimState();
                        }
                    } case 2 -> {
                        if (animTicks == 13) {
                            DragonFireBall fireball = HBEntities.DRAGON_FIREBALL.get().create(this.level());
                            fireball.setOwner(this);
                            fireball.moveTo(this.getEyePosition().add(this.getLookAngle().scale(1.2)));
                            fireball.shootFromRotation(this, this.getXRot(), this.getYHeadRot(), 0.0f, 3, 0);
                            this.level().addFreshEntity(fireball);
                        } else if (animTicks > 23) {
                            this.resetAnimState();
                            this.shootCD = 40;
                        }
                    } case 3 -> {
                        if (animTicks == 10) {
                            this.addDeltaMovement(this.getLookAngle().scale(1.2f));
                        } else if (animTicks == 12) {
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, this.getLookAngle().scale(1.2), 2, 2, 2, 5);
                            if (this.hasControllingPassenger()) {
                                for (LivingEntity e : hit) {
                                    if (this.canGrab(e)) {
                                        this.grab(e);
                                        break;
                                    }
                                }
                            } else {
                                for (LivingEntity e : hit) {
                                    if (e == this.getTarget() && this.canGrab(e)) {
                                        if (!AttackHelpers.blockBreak(e)) {
                                            this.grab(e);
                                        }
                                    } else {
                                        AttackHelpers.betterHurt(this, e, 1.7f);
                                    }
                                }
                            }
                        } else if (animTicks > 35) {
                            this.resetAnimState();
                            this.clawAttackCD = 60;
                        } else {
                            this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
                        }
                    }
                }
            }
        }
    }




    @Override
    public boolean isPushable() {
        if (this.isGrabbing() || this.getAnimState() == 3) {
            return false;
        }
        return super.isPushable();
    }

    @Override
    public void resetAnimState() {
        super.resetAnimState();
        this.attackCD +=5;
        this.shootCD +=5;
    }

    private void tickTrailYaw() {
        this.prevTrail = this.trail;
        this.trail = Mth.rotLerp(0.2F, this.trail, yBodyRotO - yBodyRot) * 0.8F;
    }

    public float getTrailYaw(float partialTick) {
        return (this.prevTrail + (this.trail - this.prevTrail) * partialTick);
    }

    @Override
    protected void dive() {

    }

    @Override
    public void setUpAnimStates() {
        boolean flying = this.isFlying();
        Vec3 delta = this.getDeltaMovement();
        this.idleAnimationState.animateWhen(!flying, this.tickCount);
        this.sitAnimationState.animateWhen(this.isSitting() && !this.isDancing(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
        this.napAnimationState.animateWhen(this.isNapping(), this.tickCount);
        this.flyUpAnimationState.animateWhen(flying && (delta.y >= 0 || delta.horizontalDistanceSqr() < 0.002 || this.getAnimState() == 3), this.tickCount);
        this.flyForwardAnimationState.animateWhen(flying && delta.horizontalDistanceSqr() >= 0.002 && flyUpAnimationState.isStarted(), this.tickCount);
        this.glideAnimationState.animateWhen(flying && !flyUpAnimationState.isStarted(), this.tickCount);
        this.eatAnimationState.animateWhen(!this.getMainHandItem().isEmpty(), this.tickCount);
        this.biteAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
        this.shootAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);
        this.clawAttackAnimationState.animateWhen(this.getAnimState() == 3, this.tickCount);
    }

    public boolean canGrab(Entity entity) {
        return entity.getBbHeight() < this.getBbHeight() * 1.2f && entity.getBbWidth() < this.getBbWidth() * 0.5f;
    }

    public void grab(Entity entity) {
        this.setGrabbedEntityID(entity.getId());
        entity.stopRiding();
    }

    public boolean isGrabbing() {
        return this.grabbedEntity != null;
    }

    public Entity getGrabbedEntity() {
        return this.grabbedEntity;
    }

    public int getGrabbedEntityID() {
        return this.entityData.get(GRABBED_ENTITY_ID);
    }

    public void setGrabbedEntityID(int id) {
        this.entityData.set(GRABBED_ENTITY_ID, id);
    }

    private void tickGrab() {
        if (!this.level().isClientSide()) {
            if (!this.grabbedEntity.isAlive() || this.distanceToSqr(grabbedEntity) > 8) {
                this.setGrabbedEntityID(-1);
                this.grabbedEntity = null;
                return;
            }
        }
        this.grabbedEntity.setDeltaMovement(this.getX() - grabbedEntity.getX(), this.getY() - grabbedEntity.getY() - grabbedEntity.getBbHeight(), this.getZ() - grabbedEntity.getZ());
        this.grabbedEntity.fallDistance = 0.0F;
    }

    public void releaseGrab() {
        this.grabbedEntity.setDeltaMovement(this.getDeltaMovement().scale(0.7));
        this.setGrabbedEntityID(-1);
        this.grabbedEntity = null;
    }

    @Override
    public void playIdle() {

    }

    @Override
    public boolean isTamable() {
        return super.isTamable() && HBConfig.DAWN_DOVE_IS_TAMABLE;
    }

    @Override
    protected void switchNav(boolean flying) {
        if (flying) {
            this.moveControl = new FlyingMoveControl(this, 45, 16, 1.6f);
            this.lookControl = new SmoothSwimmingLookControl(this, 30);
            this.navigation = new FlyingPathNavigation(this, this.level());
        } else {
            this.lookControl = new LookControl(this);
            this.moveControl = new MoveControl(this);
            this.navigation = new MMPathNavigatorGround(this, this.level());
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return HBEntities.DAWN_DOVE.get().create(level);
    }

    @Override
    public BlockState getEgg() {
        return HBBlocks.DAWN_DOVE_EGG.get().defaultBlockState();
    }

    @Override
    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    @Override
    public void setHasEgg(boolean b) {
        this.entityData.set(HAS_EGG, b);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return this.isTame() && pStack.is(HBTags.DAWN_DOVE_FOOD);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        if (!this.isFlying()) super.playStepSound(pPos, pState);
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.attackCD == 0 && attackReach >= dist && this.hasLineOfSight(entity);
    }


    public boolean canShoot(LivingEntity entity, double attackReach, double dist) {
        return this.shootCD == 0 && attackReach <= dist && this.hasLineOfSight(entity);
    }

    public boolean canUseClawAttack(LivingEntity entity, double attackReach, double dist) {
        return this.clawAttackCD == 0 && attackReach * 2 >= dist && this.hasLineOfSight(entity);
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * 2 * this.getBbWidth() * 2 + entity.getBbWidth();
    }

    @Override
    public SleepType getSleepType() {
        return SleepType.MATUTINAL;
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        switch (type) {
            case 3 -> this.setFlying(true);
            case 4 -> {
                if (attackCD == 0) this.setAnimState(1);
            }
            case 5 -> this.setAnimState(2);
            case 6 -> {
                if (this.attackCD == 0) {
                    if (!this.isGrabbing()) this.setAnimState(3);
                    else {
                        this.releaseGrab();
                        this.attackCD = 10;
                    }
                }
            }
            default -> super.onKeyPacket(keyPresser, type);
        }
    }

    @Override
    public void renderHUD(GuiGraphics guiGraphics) {
        int screenWidth = guiGraphics.guiWidth(), screenHeight = guiGraphics.guiHeight();
        int imageWidth = 192, imageHeight = 61;
        int x = (screenWidth - imageWidth) / 2;
        guiGraphics.blit(SPRITE, x, screenHeight - 82, 0, 0, imageWidth, imageHeight, 256, 128);
        imageWidth = (int) (192 * this.meterAmount);

        guiGraphics.blit(SPRITE, x, screenHeight - 27, 0, 61, imageWidth, 6, 256, 128);

        imageWidth = (int)(this.shootCD / 64F * 192);

        guiGraphics.blit(SPRITE, x, screenHeight - 31, 0, 67, imageWidth, 5, 256, 128);

        imageHeight = (int) (this.getHealth() / this.getMaxHealth() * 11);
        x = (screenWidth - 13) / 2;

        guiGraphics.blit(SPRITE, x, screenHeight - 31 - imageHeight, 0, 83 - imageHeight, 13, imageHeight, 256, 128);

    }
}
