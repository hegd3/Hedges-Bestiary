package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.blocks.HBBlocks;
import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.ai.control.ATMMoveControl;
import com.hedge.hedges_bestiary.entity.ai.control.ATMSwimLookControl;
import com.hedge.hedges_bestiary.entity.ai.control.ATMSwimMoveControl;
import com.hedge.hedges_bestiary.entity.ai.control.AdvancedTurner;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.goal.specific.MurkAttackGoal;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBAmphibiousPathNavigator;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBPathNavigatorGround;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetWhenAwakeGoal;
import com.hedge.hedges_bestiary.entity.living.ambientfish.SkibEntity;
import com.hedge.hedges_bestiary.entity.projectile.MurkSmoke;
import com.hedge.hedges_bestiary.entity.types.*;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.entity.util.MathHelpers;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.items.TreatItem;
import com.hedge.hedges_bestiary.message.EntityKeyMessage;
import com.hedge.hedges_bestiary.registry.HBEffects;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBKeyMappings;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class MurkEntity extends HBTamableAnimal implements AttackStateMob, AdvancedTurner, EggLayer, HUDMount{

    private static final ResourceLocation SPRITE = new ResourceLocation(HedgesBestiary.MODID, "textures/gui/mount/murk_hud.png");

    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(MurkEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEFT = SynchedEntityData.defineId(MurkEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(MurkEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<ItemEntity> FOOD = item -> item.getItem().is(HBItems.SKIB.get());

    public final SmoothAnimationState swimIdleAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState eatAnimationState = new SmoothAnimationState(0.1F);
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState sideSlamAnimationState = new AnimationState();
    public final AnimationState breathAnimationState = new AnimationState();
    public final AnimationState multiBiteAnimationState = new AnimationState();

    public final SmoothAnimationState clicksAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState yawnAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState sitAnimationState = new SmoothAnimationState();

    public float landProgress = 0;
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;

    private TurnType turnType = TurnType.NORMAL;
    private int attackCD = 0;
    private int multiBiteCD = 0;
    private float roarCD = 0.0F;
    private int projCD = 0;
    private float chargeProgress = 0.0F;
    private int projectileRot = 0;

    private float prevTrail;
    private float trail = 0.0F;
    private int eatProgress = 0;
    private int tameAttempts = 3;
    private boolean ateSkib = false;


    public MurkEntity(EntityType<? extends MurkEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lookControl = new ATMSwimLookControl<>(this, 25, 90);
        this.moveControl = new ATMSwimMoveControl<>(this, 45, 0.4f, 1f, 15);

        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0f);
        this.setMaxUpStep(1.0F);

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_PROJECTILE)) {
            pAmount *= 0.5f;
        }
        if (this.getAnimState() == 5) {
            pAmount *= 0.75f;
        }
        return super.hurt(pSource, pAmount);
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGED, false);
        this.entityData.define(LEFT, false);
        this.entityData.define(HAS_EGG, false);
    }

    @Override
    public int getMaxHeadYRot() {
        return 25;
    }

    @Override
    public int getMaxHeadXRot() {
        return 45;
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.7D)
                .add(Attributes.ARMOR, 14)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.FOLLOW_RANGE, 64F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new MountOverrideGoal(this));
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new EggLayerBreedGoal<>(this, 1.0f));
        this.goalSelector.addGoal(i++, new LayEggsGoal<>(this, 100, 1.0f));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.2, 1.6, 7.0f, 4.0f, true));
        this.goalSelector.addGoal(i++, new MurkAttackGoal(this));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new FindAndPickItemGoal(this, FOOD));
        this.goalSelector.addGoal(i++, new HBTemptGoal(this, 1.1, Ingredient.of(HBItems.SKIB.get()), false));
        this.goalSelector.addGoal(i++, new NapGoal(this, false));
        this.goalSelector.addGoal(i, new CustomSwimGoal(this, 1.0, 10, 10, 7, true, true));
        this.goalSelector.addGoal(i++, new RandomStrollGoal(this, 1.0) {
            @Override
            public boolean canUse() {
                return !this.mob.isInWaterOrBubble() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !this.mob.isInWaterOrBubble() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 5));
        this.goalSelector.addGoal(i++, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(i++, new IdleAnimationGoal<>(this));
        this.goalSelector.addGoal(i, new DancingGoal(this));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));
        this.targetSelector.addGoal(5, new TargetWhenAwakeGoal<>(this, SkibEntity.class, null));

    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return this.getBbHeight() * 0.82f;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
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
    public void travel(Vec3 pTravelVector) {


        if (isControlledByLocalInstance() && getControllingPassenger() instanceof Player rider) {
            float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);

            if (this.getAnimState() == 0) {

                if (Minecraft.getInstance().options.keyAttack.isDown()) {
                    HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 4));
                } else if (HBKeyMappings.MOUNT_ABILITY_KEY.isDown()) {
                    if (!this.isCharged() && this.chargeProgress >= 1F) {
                        this.chargeProgress = 1F;
                        HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 5));
                        this.roarCD = 0F;
                    } else {
                        HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.getId(), rider.getId(), 6));
                    }
                }


            }

            this.setSpeed(speed);
            if (this.isInWater()) {
                if (Minecraft.getInstance().options.keyJump.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.03, 0));
                } else if (Minecraft.getInstance().options.keySprint.isDown()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.03, 0));
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
            if (this.horizontalCollision) { //&& this.level().getBlockState(this.blockPosition().above()).isAir()) {
                final float f1 = this.getYRot() * Mth.DEG_TO_RAD;
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.1f, 0.05D, Mth.cos(f1) * 0.1f));
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
    protected Vec3 getRiddenInput(Player pPlayer, @NotNull Vec3 pTravelVector) {

        return switch (this.getAnimState()) {
            case 4 -> Vec3.ZERO;
            case 3, 5 -> new Vec3(0, 0, this.isInWater() ? 0.1F : 0.3f);
            default -> {
                float f1;
                float f2;
                if (this.isInWater()) {
                    f1 = pPlayer.zza * 0.1F;
                    f2 = 0;
                } else {
                    f1 = pPlayer.zza * 0.5F;
                    f2 = pPlayer.xxa * 0.2F;
                }
                if (f1 < 0.0F)
                    f1 *= 0.25F;

                yield new Vec3(f2, 0, f1);
            }
        };
    }


    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);
        float newYaw = Mth.rotLerp(0.15F, this.getYRot(), pPlayer.getYHeadRot());
        this.setRot(newYaw, Mth.clamp(pPlayer.getXRot(), -10, 10));
        this.setYHeadRot(pPlayer.getYHeadRot());

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
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(new ItemStack(HBItems.MURK_SPIKE.get(), this.getRandom().nextInt(4) + 1), 1);
        }

    }



    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new HBAmphibiousPathNavigator(this, pLevel);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = super.mobInteract(player, hand);
        if (result == InteractionResult.PASS && this.isTamable() && !this.isTame() && this.isCharged() && this.getMainHandItem().isEmpty()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(HBItems.SKIB_BUCKET.get())) {
                if (!this.level().isClientSide && !ateSkib) {
                    if (stack.is(HBItems.SKIB_BUCKET.get())) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
                        if (!player.getAbilities().instabuild) {
                            player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WATER_BUCKET));
                        }
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (stack.getItem() instanceof TreatItem treat && treat.getTier() > 0) {
                if (!this.level().isClientSide && ateSkib) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (this.tameAttempts-- <= 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        this.tame(player);
                        player.startRiding(this);
                        this.heal(this.getMaxHealth());
                    } else {
                        this.level().broadcastEntityEvent(this, (byte)6);
                        this.ateSkib = false;
                    }
                    this.playSound(SoundEvents.GENERIC_EAT);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

        }
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getAnimState() > 1 && this.getAnimState() < 6) {
            this.yBodyRot = Mth.lerp(1F, this.yBodyRot, this.yHeadRot);
        } else {
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, 10);
        }
        final boolean landNav = this.navigation instanceof HBPathNavigatorGround;
        if (this.isInWater()) {
            if (landNav) {
                this.switchNav(true);
            }
            if (this.landProgress > 0) {
                this.landProgress -=0.25f;
            }
            this.tickPitch();

        } else {

            if (this.landProgress < 5) {
                this.landProgress +=0.25f;
            }
            if (!landNav) {
                this.switchNav(false);
            }
        }
        if (this.level().isClientSide()) {
            this.setUpAnimStates();
            if (this.isCharged()) {
                for (int i = 0; i < 3; i++) {
                    Vec3 rand = EntityHelpers.getRandomVec3(this.getRandom(), 2);
                    this.level().addParticle(HBParticles.MURK_CHARGE.get(), this.getX() + rand.x,
                            this.getY() + rand.y / 2 + 2, this.getZ() + rand.z, rand.x, rand.y + 0.2, rand.z);
                }
                if (this.chargeProgress > 0F) {
                    this.chargeProgress = Math.max(chargeProgress - 1/600F, 0F);
                }
            } else if (this.roarCD < 1F) {
                this.roarCD = roarCD + 0.001F;
            } else if (this.chargeProgress < 1F) {
                this.chargeProgress = chargeProgress + 0.02F;
            }
            if (this.eatAnimationState.isStarted() && this.tickCount % 5 == 0) {
                this.playSound(SoundEvents.GENERIC_EAT);
                this.addEatingParticles();
            }
            if (this.hasEgg() && this.onGround()) {
                this.tickDig();
            }
            this.tickTrailYaw();
        } else {
            this.attackCD = Math.max(this.attackCD - 1, 0);
            this.projCD = Math.max(this.projCD - 1, 0);
            this.multiBiteCD = Math.max(this.multiBiteCD - 1, 0);
            if (this.isCharged()) {
                this.chargeProgress-= 1/600F;
                if (this.chargeProgress <= 0) {
                    this.setCharged(false);
                }
            } else {
                this.roarCD = Math.max(this.roarCD  - 1, 0);
            }
            LivingEntity target = this.getTarget();
            if (this.tickCount % 200 == 0 && target == null) {
                this.heal(10);
            }
            if (!this.getMainHandItem().isEmpty()) {
                this.eatProgress++;
                if (this.eatProgress > 20) {
                    this.eatProgress = 0;
                    if (this.getMainHandItem().is(HBItems.SKIB_BUCKET.get())) {
                        this.ateSkib = true;
                    }
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    this.heal(this.getMaxHealth() / 5);
                } else if (this.eatProgress % 5 == 0) {
                    this.playSound(SoundEvents.GENERIC_EAT);
                }
            }
            if (this.getAnimState() > 0) {
                this.animTicks++;
                switch (this.getAnimState()) {
                    case 1 -> {
                        if (this.animTicks == 5) {
                            this.turnType = TurnType.WHOLE_BODY;
                        }
                        else if (this.animTicks == 10) {
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, this.getLookAngle().scale(2.2), 1.8, 1.5, 1.8, 3);
                            for (LivingEntity entity : hit) {
                                this.doHurtTarget(entity);
                            }
                        } else if (this.animTicks >= 19) {
                            this.resetAnimState();
                        }
                    }
                    case 2 -> {
                        if (this.animTicks < 10 && target != null) {
                       //     this.lookAt(target, 30f, 30f);
                            this.getLookControl().setLookAt(target, 30f, 30f);
                        }
                        else if (this.animTicks >= 16 && this.animTicks <= 24 && this.animTicks % 2 == 0) {
                            MurkSmoke projectile = HBEntities.MURK_SMOKE.get().create(this.level());
                            if (projectile != null) {
                                projectile.setCharged(this.isCharged());
                                Vec3 v = (EntityHelpers.bodyAngle(this).scale(1.3f)).cross(EntityHelpers.UP).scale(this.projectileRot * 0.1);
                                projectile.moveTo(this.getEyePosition().add(v));
                                projectile.shootFromRotation(this, Mth.clamp(this.getXRot(), -25, 25), this.getYHeadRot() + this.projectileRot, 0.0f, 3, 0);
                                this.level().addFreshEntity(projectile);
                                this.projectileRot += this.swingingLeft() ? 5 : -5;
                            }

                        } else if (this.animTicks >= 39) {
                            this.resetAnimState();
                            this.projCD = 100;
                        }
                    }
                    case 3 -> {
                        if (this.animTicks % 5 == 0 && this.animTicks < 18) {
                            if (target != null) {
                                this.getLookControl().setLookAt(target, 15f, 30f);
                            }
                            this.addDeltaMovement(EntityHelpers.bodyAngle(this).scale(this.isInFluidType() ? 0.1 : 0.25));
                        } else if (this.animTicks >= 75){
                            this.resetAnimState();
                            this.multiBiteCD = 120;
                        } else {
                            switch (this.animTicks) {
                                case 18 -> this.turnType = TurnType.LOCK;
                                case 19, 36, 54 -> {
                                    this.powerBite();
                                    this.addDeltaMovement(this.getLookAngle().scale(0.4));
                                }
                            }
                        }
                    }
                    case 4 -> {
                        if (this.animTicks == 23) {
                            this.setCharged(true);
                            this.chargedExplode();
                            this.chargeProgress = 1;
                        }
                        else if (this.animTicks >= 65) {
                            this.resetAnimState();
                            this.roarCD = 340;
                        }
                        if (target != null) {
                            this.getLookControl().setLookAt(target.position().add(0, 0.5, 0));
                        }
                    }
                    case 5 -> {
                        if (this.animTicks < 15 && target != null) {
                            this.getLookControl().setLookAt(target, 30f, 30f);

                        } else if (this.animTicks == 20) {
                            this.turnType = TurnType.LOCK;
                            Vec3 v = EntityHelpers.bodyAngle(this);
                            this.addDeltaMovement(v.scale(0.8));
                            if (this.isCharged()) {
                                this.chargedExplode();
                            }
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, v, 3, 3, 3, 8);
                            for (LivingEntity entity : hit) {
                                if (!AttackHelpers.blockBreak(entity)) {
                                    AttackHelpers.betterHurt(this, entity, 1.2f, 1.4f);
                                }
                            }
                        } else if (this.animTicks >= 44) {
                            this.resetAnimState();
                        }

                    }
                    case 6 -> {
                        if (this.animTicks > 20) {
                            this.resetAnimState();
                        }
                    }
                    case 7 -> {
                        if (this.animTicks > 32) {
                            this.resetAnimState();
                        }
                    }
                }
            }
        }
    }

    private void tickPitch() {
        this.prevPitch = this.pitch;
        float target = (Mth.clamp((float)this.getDeltaMovement().y * 4F, -1F, 1F)) * -Mth.RAD_TO_DEG;
        this.pitch = Mth.approachDegrees(pitch, target, 2.5F);

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

    private void powerBite() {
        Vec3 v = EntityHelpers.bodyAngle(this).scale(1.5);
        if (this.isCharged()) {
            this.level().broadcastEntityEvent(this, (byte)40);
            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, v, 2, 2, 2, 8);

            for (LivingEntity entity : hit) {
                if (!AttackHelpers.blockBreak(entity)) {
                    AttackHelpers.betterHurt(this, entity, 1.8f, 1.5f);
                    entity.addEffect(new MobEffectInstance(HBEffects.VOLATILITY.get(), 40, 1));
                } else {
                    AttackHelpers.betterHurt(this, entity, 0.8f, 0.8f);
                }
            }
        } else {
            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, v, 2, 2, 2, 8);
            for (LivingEntity entity : hit) {
                if (!AttackHelpers.blockBreak(entity)) {
                    AttackHelpers.betterHurt(this, entity, 1.8f, 1.5f);
                }
            }
        }
    }


    private void spawnImpactParticle() {
        Vec3 v = this.getEyePosition().add(this.getLookAngle().scale(3));
        this.level().addParticle(HBParticles.MURK_IMPACT.get(),
                v.x, v.y, v.z, 0, 0, 0);
    }

    private void chargedExplode() {
        this.level().broadcastEntityEvent(this, (byte)39);
        for (int i = -180; i <= 180; i += 60) {
            MurkSmoke projectile = HBEntities.MURK_SMOKE.get().create(this.level());
            if (projectile != null) {
                projectile.setCharged(true);
                projectile.moveTo(this.position().add(0, 0.4, 0));
                projectile.shootFromRotation(this, 0, this.getYRot() + i, 0.0f, 3, 0);
                this.level().addFreshEntity(projectile);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        switch (pId) {
            case 39 -> this.level().addParticle(HBParticles.MURK_EXPLODE.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            case 40 -> this.spawnImpactParticle();
            default -> super.handleEntityEvent(pId);
        }
    }

    @Override
    public void resetAnimState() {
        super.resetAnimState();
        this.attackCD += 5;
        this.roarCD+=5;
        this.projCD+=5;
        this.multiBiteCD +=5;
        if (this.turnType != TurnType.NORMAL) {
            this.turnType = TurnType.NORMAL;
        }
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(!this.isInWater(), this.tickCount);
        this.swimIdleAnimationState.animateWhen(this.isInWater(), this.tickCount);
        int animState = this.getAnimState();
        this.biteAnimationState.animateWhen(animState == 1, this.tickCount);
        this.breathAnimationState.animateWhen(animState == 2, this.tickCount);
        this.multiBiteAnimationState.animateWhen(animState == 3, this.tickCount);

        this.roarAnimationState.animateWhen(animState == 4, this.tickCount);
        this.sideSlamAnimationState.animateWhen(animState == 5, this.tickCount);

        this.clicksAnimationState.animateWhen(animState == 6, this.tickCount);
        this.yawnAnimationState.animateWhen(animState == 7, this.tickCount);
        this.napAnimationState.animateWhen(this.isNapping(), this.tickCount);
        this.eatAnimationState.animateWhen(!this.getMainHandItem().isEmpty(), this.tickCount);
        this.sitAnimationState.animateWhen(this.isSitting() && !this.isDancing(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean b) {
        this.entityData.set(CHARGED, b);
    }

    public boolean canRoar(double attackReach, double dist) {
        return this.roarCD == 0 && !this.isCharged() && attackReach * 20 >= dist;
    }

    public boolean canMultiBite(double attackReach, double dist) {
        return this.multiBiteCD == 0 && attackReach * 1.4 >= dist;
    }

    public void setSlam() {
        this.setLeft(!this.swingingLeft());
        this.setAnimState(5);
    }

    @Override
    public void setAttacking() {
        this.setAnimState(1);
    }

    public void setShooting() {
        this.setLeft(!this.swingingLeft());
        this.projectileRot = this.swingingLeft() ? -10 : 10;
        this.setAnimState(2);
        this.setTurnType(TurnType.WHOLE_BODY);
    }


    public boolean swingingLeft() {
        return this.entityData.get(LEFT);
    }

    public void setLeft(boolean b) {
        this.entityData.set(LEFT, b);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.attackCD == 0 && attackReach >= dist;
    }

    public boolean canShoot(double attackReach, double dist) {
        return this.projCD == 0 && attackReach * 10 >= dist;
    }

    public int getProjCD() {
        return this.projCD;
    }


    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * 2.0 * this.getBbWidth() * 2.0 + entity.getBbWidth();

    }




    @Override
    public void playIdle() {
        if (this.getRandom().nextBoolean()) {
            this.setAnimState(6);
            this.playSound(HBSounds.MURK_CLICKS.get(), 1.5f, (this.isBaby() ? 1.5F : 1) - (this.getRandom().nextFloat() / 4));
        } else {
            this.setAnimState(7);
            this.playSound(HBSounds.MURK_YAWN.get(), 1.5f, (this.isBaby() ? 1.5F : 1) - (this.getRandom().nextFloat() / 4));
        }
    }

    @Override
    protected void playSwimSound(float pVolume) {

    }



    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        if (!this.isInWater()) {
            this.playSound(HBSounds.MURK_STOMP.get(), 0.8F, 1.0F - this.getRandom().nextFloat() / 4);

        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return HBEntities.MURK.get().create(level);
    }

    @Override
    public BlockState getEgg() {
        return HBBlocks.MURK_EGG.get().defaultBlockState();
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
    public void setTurnType(TurnType turnType) {
        this.turnType = turnType;
    }

    @Override
    public TurnType getTurnType() {
        return this.turnType;
    }

    @Override
    public SleepType getSleepType() {
        return SleepType.CATHERMAL;
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        switch (type) {
            case 4 -> {
                if (this.multiBiteCD == 0) {
                    this.setAnimState(3);
                    this.turnType = TurnType.WHOLE_BODY;
                } else if (this.attackCD == 0) {
                    if (this.isInWater() && this.getRandom().nextInt(5) == 0) {
                        this.setSlam();
                        this.turnType = TurnType.WHOLE_BODY;
                    } else {
                        this.setAttacking();

                    }
                }
            }
            case 5 -> {
                this.setAnimState(4);
                this.playSound(HBSounds.MURK_ROAR.get(), 1.5F, 1F);
                this.turnType = TurnType.WHOLE_BODY;
            }
            case 6 -> {
                if (this.projCD == 0) this.setShooting();
            }

        }
        super.onKeyPacket(keyPresser, type);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunc) {
        final float angle = (MathHelpers.STARTING_ANGLE * this.yBodyRot);
        passenger.setYBodyRot(this.yBodyRot);
        if (passenger instanceof LivingEntity living) {
            clampRotation(living, 105);
        }

        double targetY = this.getY() + passenger.getBbHeight();
        double extraX = -Mth.sin(Mth.PI + angle);
        double extraZ = -Mth.cos(angle);

        moveFunc.accept(passenger, this.getX() + extraX, targetY, this.getZ() + extraZ);

    }

    private void switchNav(boolean inWater) {
        if (inWater) {
            this.moveControl = new ATMSwimMoveControl<>(this, 45, 0.4f, 1f, 15);
            this.navigation = this.createNavigation(this.level());
        } else {
            this.moveControl = new ATMMoveControl<>(this, 90);
            this.navigation = new HBPathNavigatorGround(this, this.level());

        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        if (pEffectInstance.getEffect() == HBEffects.VOLATILITY.get()) {
            return false;
        }
        return super.canBeAffected(pEffectInstance);
    }

    @Override
    public boolean isTamable() {
        return super.isTamable() && HBConfig.MURK_IS_TAMABLE;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return super.isFood(pStack) && pStack.is(HBItems.SKIB.get());
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return HBSounds.MURK_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return HBSounds.MURK_DIE.get();
    }

    @Override
    public void renderHUD(GuiGraphics guiGraphics) {
        int screenWidth = guiGraphics.guiWidth(), screenHeight = guiGraphics.guiHeight();
        int imageWidth = 192, imageHeight = 45;
        int x = (screenWidth - imageWidth) / 2;
        if (this.isCharged()) {
            guiGraphics.blit(SPRITE, x, screenHeight - 66, 0, 55, imageWidth, imageHeight, 256, 256);

            imageWidth = (int) (192 * this.chargeProgress);

            guiGraphics.blit(SPRITE, x, screenHeight - 31, 0, 45, imageWidth, 10, 256, 256);

            guiGraphics.blit(SPRITE, x, screenHeight - 31, 0, 100, imageWidth, 10, 256, 256);


        } else {
            guiGraphics.blit(SPRITE, x, screenHeight - 66, 0, 0, imageWidth, imageHeight, 256, 256);

            imageWidth = (int) (192 * (this.getAnimState() == 4 ? this.chargeProgress : this.roarCD));

            guiGraphics.blit(SPRITE, x, screenHeight - 31, 0, 45, imageWidth, 10, 256, 256);

            imageWidth = (int) (192 * this.chargeProgress);

            guiGraphics.blit(SPRITE, x, screenHeight - 31, 0, 100, imageWidth, 10, 256, 256);

        }
        imageHeight = (int) (this.getHealth() / this.getMaxHealth() * 11);
        x = (screenWidth - 13) / 2;

        guiGraphics.blit(SPRITE, x, screenHeight - 31 - imageHeight, 0, 122 - imageHeight, 13, imageHeight, 256, 256);




    }

    public static boolean canSpawn(EntityType<? extends MurkEntity> murk, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        int i = pLevel.getSeaLevel();
        int j = i - 13;
        return pLevel.getFluidState(pPos).is(FluidTags.WATER) && pPos.getY() >= j && pPos.getY() <= i + 1;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }



}

