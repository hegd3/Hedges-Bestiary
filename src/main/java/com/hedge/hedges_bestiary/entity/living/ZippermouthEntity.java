package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.goal.specific.ZippermouthAttackGoal;
import com.hedge.hedges_bestiary.entity.ai.navigation.FluidPathNavigation;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.types.HUDMount;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.entity.util.SegmentHelper;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.networking.packet.EntityKeyPacket;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBKeyMappings;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZippermouthEntity extends HBTamableAnimal implements AttackStateMob, HUDMount {

    private static final ResourceLocation SPRITE = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/gui/mount/zippermouth_hud.png");
    private static final EntityDataAccessor<Boolean> DROPPED_SCALE = SynchedEntityData.defineId(ZippermouthEntity.class, EntityDataSerializers.BOOLEAN);

    public float prevPitch = 0.0F;
    public float pitch = 0.0F;

    private int jumpCD = 0;
    private int suckMeter = 200;
    private int suckCD = 0;
    private int multiBiteCD = 0;
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState multiBiteAnimationState = new AnimationState();

    public final SmoothAnimationState beachedAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState suckAnimationState = new SmoothAnimationState(0.1F);

    public final SegmentHelper segmentHelper = new SegmentHelper(4,0.15F);

    private final ZippermouthPartEntity segment1;
    private final ZippermouthPartEntity segment2;
    private final ZippermouthPartEntity segment3;
    private final ZippermouthPartEntity[] segments;

    public ZippermouthEntity(EntityType<? extends ZippermouthEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 6, 0.02f, 0.0f);
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
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
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
                .add(Attributes.ARMOR, 8)
                .add(Attributes.ATTACK_DAMAGE, 30.0D)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DROPPED_SCALE, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setDroppedScale(compound.getBoolean("DroppedScale"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("DroppedScale", this.droppedScale());
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

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.tickCount % 40 == 0 && this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            this.breakIce();
        }
    }

    public void tick() {
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, 20);
        this.tickPitch();
        if (this.level().isClientSide) {
            this.setUpAnimStates();
            if (this.suckCD == 0 && this.suckMeter < 200 && this.getAnimState() != 2) {
                this.suckMeter += 10;
                this.suckCD = 40;
            }
        } else {
            if (this.tickCount % 100 == 0 && this.getTarget() == null) {
                this.heal(20);
            }
        }
        if (!this.level().isClientSide && this.multiBiteCD > 0) {
            this.multiBiteCD--;
        }
        if (this.suckCD > 0) this.suckCD--;
        this.segmentHelper.tick((this.yBodyRotO - this.yBodyRot) * 0.5F, (this.prevPitch - this.pitch));
        this.tickMultiPart();
        if (this.getAnimState() > 0) {
            if (!this.isAlive()) {
                if (this.level().isClientSide) {
                    this.resetAnimState();
                }
                return;
            }
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
                            this.playSound(HBSounds.ZIPPERMOUTH_BITE.get(), 1.8F, 1.0F);
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
                            if (this.getControllingPassenger() == null && (this.animTicks > 60 || target == null || target.distanceToSqr(this) <= this.getAttackReachSqr(target) / 2)) {
                                this.resetAnimState();
                                this.suckCD = 200;
                                break;
                            }
                        } else {
                            for (int i = 1; i < 6; i++) {
                                Vec3 pos = Vec3.directionFromRotation(this.getXRot(), this.yHeadRot).scale(i * 1.5F).yRot((Mth.wrapDegrees(this.getRandom().nextFloat() * 2 - this.getRandom().nextFloat() * 2)) * Mth.DEG_TO_RAD);

                                this.level().addParticle(HBParticles.WATER_SUCK.get(), true, this.getX() + pos.x, this.getY() + pos.y + this.getRandom().nextFloat() * 2F - this.getRandom().nextFloat() * 2F, this.getZ() + pos.z, -pos.x, -pos.y, -pos.z);
                            }
                        }

                        if (this.animTicks % 5 == 0 && this.isInWater()) {
                            Vec3 pos = Vec3.directionFromRotation(this.getXRot(), this.yHeadRot).scale(2.5);
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, pos, 7, 5, 7, 10);
                            for (LivingEntity entity : hit) {
                                if (entity.isInWater() && !entity.isPassenger()) {
                                    Vec3 v = this.position().add(pos).subtract(entity.position());
                                    entity.setDeltaMovement(entity.getDeltaMovement().lerp(v, Math.max(0.1F - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) / 10, 0.00F)));
                                }
                            }
                        }
                    }
                }
                case 3 -> {
                    switch (this.animTicks) {
                        case 18, 31 -> this.addDeltaMovement(this.getLookAngle().scale(0.3F));
                        case 22 -> this.multiBite(0.5F);
                        case 35 ->  this.multiBite(0.75F);
                        case 46 -> this.addDeltaMovement(this.getLookAngle().scale(0.4F));
                        case 50 -> this.multiBite(0.8F);
                        default -> {
                            if (this.animTicks > 64 && !this.level().isClientSide) {
                                this.resetAnimState();
                                this.multiBiteCD = 140;
                            }
                        }
                    }
                }
            }
        }

    }

    private void multiBite(float damageMultiplier) {
        if (!this.level().isClientSide) {
            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, this.getLookAngle().scale(0.25F), 3, 3, 3, 10);
            for (LivingEntity entity : hit) {
                if (!AttackHelpers.blockBreak(entity)) {
                    AttackHelpers.betterHurt(this, entity, damageMultiplier);
                }
            }
            this.playSound(HBSounds.ZIPPERMOUTH_BITE.get(), 1.8F, 1.8F - damageMultiplier);
            this.level().broadcastEntityEvent(this, (byte) 49);
        }
    }

    private void breakIce() {
        for (int a = (int) Math.round(this.getBoundingBox().minX); a <= (int) Math.round(this.getBoundingBox().maxX); a++) {
            for (int b = (int) Math.round(this.getBoundingBox().minY) - 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 1) && (b <= 127); b++) {
                for (int c = (int) Math.round(this.getBoundingBox().minZ); c <= (int) Math.round(this.getBoundingBox().maxZ); c++) {
                    BlockPos pos = new BlockPos(a, b, c);
                    BlockState state = level().getBlockState(pos);
                    FluidState fluidState = level().getFluidState(pos);
                    if (!state.getShape(level(), pos).isEmpty() && fluidState.isEmpty() && state.is(BlockTags.ICE)) {
                        level().destroyBlock(pos, false);
                        level().setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());

                    }
                }
            }
        }
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isTame() && !this.droppedScale()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(HBItems.LUSCIOUS_TREAT)) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    ItemEntity scales = this.spawnAtLocation(new ItemStack(HBItems.ZIPPERMOUTH_SCALES.get()), 1);
                    scales.setGlowingTag(true);
                    this.setDroppedScale(true);

                } else {
                    for (int i = 0; i < 4; i++ ) {
                        this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(1.0F), this.getRandomY() + 0.5F, this.getRandomZ(1.0F), 0.0F, 0.0F, 0.0F);
                    }
                }
                this.playSound(SoundEvents.AMETHYST_CLUSTER_HIT, 2.0F, 1.0F);
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }


    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            Vec3 offset = new Vec3(0.8F, passenger.getBbHeight() - 0.5F, 1.4F).yRot(-Mth.wrapDegrees(this.getYRot() + this.segmentHelper.getYawAtIndex(0, 1.0F)) * Mth.DEG_TO_RAD);
            passenger.setYBodyRot(this.yBodyRot);
            clampRotation(living, 105);


            passenger.fallDistance = 0.0F;
            callback.accept(passenger, this.segment1.getX() + offset.x, this.segment1.getY() + offset.y, this.segment1.getZ() + offset.z);
        } else {
            super.positionRider(passenger, callback);
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
            f1 = pPlayer.zza * 0.022F;
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
                if (this.jumpCD > 0) {
                    this.jumpCD--;
                }
                if (Minecraft.getInstance().options.keyJump.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.03, 0));
                } else if (Minecraft.getInstance().options.keySprint.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.03, 0));
                }

                if (this.getAnimState() == 0) {

                    if (Minecraft.getInstance().options.keyAttack.isDown()) {
                        PacketDistributor.sendToServer(new EntityKeyPacket(this.getId(), rider.getId(), 5));
                    } else if (HBKeyMappings.MOUNT_ABILITY_KEY.isDown()) {
                        if (this.suckMeter > 0) {
                            PacketDistributor.sendToServer(new EntityKeyPacket(this.getId(), rider.getId(), 6));
                        }
                    }


                } else if (this.getAnimState() == 2) {
                    if (this.suckMeter-- <= 0 || !HBKeyMappings.MOUNT_ABILITY_KEY.isDown()) {
                        PacketDistributor.sendToServer(new EntityKeyPacket(this.getId(), rider.getId(), 7));
                    }
                }
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D).add(0, 0.002425F, 0));
            }  else if (this.jumpCD == 0 && !this.onGround() && !this.horizontalCollision && this.pitch < -5F) {
                this.jumpCD = 60;
                Vec3 horizontal = EntityHelpers.bodyAngle(this, this.pitch * 2F).scale(this.getDeltaMovement().length() * 16);
                this.setDeltaMovement(this.getDeltaMovement().add(horizontal.x, Math.min(horizontal.y * 2F, 1), horizontal.z));
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

    public boolean canSuck(double attackReach, double dist) {
        return this.suckCD == 0 && attackReach * 10 >= dist;
    }

    public boolean canMultiBite(double attackReach, double dist) {
        return this.multiBiteCD == 0 && attackReach * 3 >= dist;
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return attackReach >= dist;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (!this.isTame() && target instanceof NibNibEntity) {
            return false;
        }
        return super.canAttack(target);
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
        this.multiBiteAnimationState.animateWhen(this.getAnimState() == 3, this.tickCount);
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
    public void playAmbientSound() {
        this.playSound(HBSounds.ZIPPERMOUTH_AMBIENT.get(), 1.5F, 1.0F - this.getRandom().nextFloat() * 0.25F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(HBSounds.ZIPPERMOUTH_HURT.get(), 1.5F, 1.0F - this.getRandom().nextFloat() * 0.25F);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return HBSounds.ZIPPERMOUTH_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return HBSounds.ZIPPERMOUTH_DIE.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return HBSounds.ZIPPERMOUTH_HURT.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            int groupSize = (int) (15 * this.getRandom().nextFloat());
            Vec3 rand = EntityHelpers.getRandomVec3(level.getRandom(), 6);
            NibNibEntity leader = new NibNibEntity(HBEntities.NIB_NIB.get(), this.level());
            leader.moveTo(this.getX() + rand.x, this.getY() + rand.y, this.getZ() + rand.z);
            this.level().addFreshEntity(leader);
            for (int i = 0; i < groupSize; i++) {
                rand = EntityHelpers.getRandomVec3(level.getRandom(), 6);
                NibNibEntity entity = new NibNibEntity(HBEntities.NIB_NIB.get(), this.level());
                entity.moveTo(this.getX() + rand.x, this.getY() + rand.y, this.getZ() + rand.z);
                entity.startFollowing(leader);
                this.level().addFreshEntity(entity);
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
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
    public void onKeyPacket(Entity keyPresser, int type) {
        switch (type) {
            case 5 -> this.setAnimState(this.multiBiteCD == 0 ? 3 : 1);
            case 6 -> this.setAnimState(2);
            case 7 -> this.resetAnimState();
            default -> super.onKeyPacket(keyPresser, type);
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public boolean droppedScale() {
        return this.entityData.get(DROPPED_SCALE);
    }

    public void setDroppedScale(boolean b) {
        this.entityData.set(DROPPED_SCALE, b);
    }

    @Override
    public void onAboveBubbleCol(boolean downwards) {
    }

    @Override
    public void onInsideBubbleColumn(boolean downwards) {
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public static boolean canSpawn(EntityType<? extends ZippermouthEntity> zippermouth, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        int i = pLevel.getSeaLevel();
        int j = i - 13;
        return pLevel.getFluidState(pPos).is(FluidTags.WATER) && pPos.getY() >= j && pPos.getY() <= i + 1;
    }

    @Override
    public float getCameraOffsetY() {
        return 1F;
    }

    @Override
    public float getCameraOffsetZ() {
        return -3.5F;
    }

    @Override
    public void renderHUD(GuiGraphics guiGraphics) {
        int screenWidth = guiGraphics.guiWidth(), screenHeight = guiGraphics.guiHeight();
        int imageWidth = 192, imageHeight = 70;
        int x = (screenWidth - imageWidth) / 2;
        guiGraphics.blit(SPRITE, x, screenHeight - 75, 0, 0, imageWidth, imageHeight, 256, 128);
        imageWidth = (int)(192 * (this.suckMeter / 200F));

        guiGraphics.blit(SPRITE, x, screenHeight - 31, 0, 70, imageWidth, 10, 256, 128);


        imageHeight = (int) (this.getHealth() / this.getMaxHealth() * 11);
        x = (screenWidth - 13) / 2;

        guiGraphics.blit(SPRITE, x, screenHeight - 31 - imageHeight, 0, 91 - imageHeight, 13, imageHeight, 256, 128);

    }
}
