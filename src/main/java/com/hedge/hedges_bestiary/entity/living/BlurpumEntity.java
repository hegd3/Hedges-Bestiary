package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticLookControl;
import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBAmphibiousPathNavigator;
import com.hedge.hedges_bestiary.entity.ai.targeting.HBHurtByTargetGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetMonstersGoal;
import com.hedge.hedges_bestiary.entity.ai.targeting.TargetPlayersGoal;
import com.hedge.hedges_bestiary.entity.types.AttackStateMob;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.entity.util.CommonPredicates;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.networking.packet.EntityKeyPacket;
import com.hedge.hedges_bestiary.registry.HBKeyMappings;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlurpumEntity extends HBTamableAnimal implements AttackStateMob {
    public final SmoothAnimationState swimIdleAnimationState = new SmoothAnimationState(0.1F);
    public final AnimationState biteAnimationState = new AnimationState();
    public float landProgress = 0;
    public float prevPitch = 0.0F;
    public float pitch = 0.0F;
    private float prevTrail = 0.0F;
    private float trail = 0.0F;

    private int tameAttempts = 3;
    public BlurpumEntity(EntityType<? extends BlurpumEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.moveControl = new SemiaquaticMoveControl(this, 999, 10, 0.25f);
        this.lookControl = new SemiaquaticLookControl(this, 20);


        this.setPathfindingMalus(PathType.WATER, 0.0f);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0f);

    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.7D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.FOLLOW_RANGE, 20F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.STEP_HEIGHT, 1.0F);
    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new MountOverrideGoal(this));
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.2, 1.4, 4.0f, 2.0f, true));
        this.goalSelector.addGoal(i++, new GenericMeleeGoal<>(this, 1.4F));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0, 40, 15, 4, true, true));
        this.goalSelector.addGoal(i, new SemiaquaticStrollGoal(this, 1.0));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 5));
        this.goalSelector.addGoal(i++, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(i, new DancingGoal(this));

        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HBHurtByTargetGoal(this, true, TamableAnimal.class));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TargetPlayersGoal(this));
        this.targetSelector.addGoal(4, new TargetMonstersGoal(this));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Player.class, true, CommonPredicates.TARGET_UNCROUCHED));

    }



    @Override
    protected boolean canRide(Entity vehicle) {
        if (vehicle instanceof Boat) {
            return false;
        }
        return super.canRide(vehicle);
    }

    @Override
    public boolean isPushable() {
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
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, 10);
        if (this.level().isClientSide) {
            this.setUpAnimStates();
            this.tickTrailYaw();
            this.tickPitch();
            if (this.isInWater()) {
                if (this.landProgress > 0) {
                    this.landProgress -=0.25f;
                }
            } else if (this.landProgress < 5) {
                this.landProgress +=0.25f;
            }
        } else {
            if (this.getAnimState() > 0) {
                if (!this.isAlive()) {
                    this.resetAnimState();
                    return;
                }
                this.animTicks++;
                switch (this.getAnimState()) {
                    case 1 -> {
                        if (this.animTicks == 10) {
                            List<LivingEntity> hit = AttackHelpers.zoneHitbox(this, this.getLookAngle(), 2, 2, 2, 4);
                            for (LivingEntity entity : hit) {
                                this.doHurtTarget(entity);
                            }
                        } else if (this.animTicks > 17) {
                            this.resetAnimState();
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult interactTameCommands(Player player, @NotNull InteractionHand hand) {
        InteractionResult result = super.interactTameCommands(player, hand);
        if (result == InteractionResult.PASS) {
            if (!this.isTame() && this.isSitting() && player.isCrouching() && player.getItemInHand(hand).is(HBItems.SEASONED_TREAT.get())) {
                if (!this.level().isClientSide()) {
                    if (!player.getAbilities().instabuild) {
                        player.getItemInHand(hand).shrink(1);
                    }
                    if (this.tameAttempts > 0) {
                        this.tameAttempts--;
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        this.tame(player);
                        this.heal(this.getMaxHealth());
                    }
                    this.playSound(SoundEvents.PARROT_EAT);

                }
                return InteractionResult.SUCCESS;
            }
        }
        return result;
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

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(!this.isInWater(), this.tickCount);
        this.swimIdleAnimationState.animateWhen(this.isInWater(), this.tickCount);
        this.sitAnimationState.animateWhen(this.isSitting() && !this.isDancing(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
        this.biteAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (isControlledByLocalInstance() && getControllingPassenger() instanceof Player rider) {

            if (this.getAnimState() == 0) {

                if (Minecraft.getInstance().options.keyAttack.isDown()) {
                    PacketDistributor.sendToServer(new EntityKeyPacket(this.getId(), rider.getId(), 5));
                }

            }

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

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        float newYaw = Mth.rotLerp(0.3F, this.getYRot(), player.getYHeadRot());
        this.setRot(newYaw, Mth.clamp(player.getXRot(), -15, 15));
        this.setYHeadRot(player.getYHeadRot());
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float f1;
        float f2;
        if (this.isInWater()) {
            f1 = player.zza * 0.1F;
            f2 = 0;
        } else {
            f1 = player.zza * 0.5F;
            f2 = player.xxa * 0.2F;
        }
        if (f1 < 0.0F)
            f1 *= 0.25F;
        return new Vec3(f2, 0, f1);
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
    public void setAttacking() {
        this.setAnimState(1);
    }

    @Override
    public boolean canUseAttack(LivingEntity entity, double attackReach, double dist) {
        return this.getAnimState() == 0 && attackReach >= dist;
    }

    @Override
    public double getAttackReachSqr(LivingEntity entity) {
        return this.getBbWidth() * this.getBbWidth() * 4 + entity.getBbWidth();
    }

    @Override
    protected boolean canOwnerMount(Player player) {
        return true;
    }

    @Override
    protected boolean canOwnerCommand(Player player) {
        return player.isShiftKeyDown();
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return super.isFood(pStack) && pStack.is(ItemTags.FISHES);
    }

    @Override
    public void playIdle() {

    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public SleepType getSleepType() {
        return SleepType.RESTLESS;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new HBAmphibiousPathNavigator(this, level);
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (type == 5) {
            this.setAnimState(1);
        } else {
            super.onKeyPacket(keyPresser, type);
        }
    }
}
