package com.hedge.hedges_bestiary.entity.living.ambientfish;

import com.hedge.hedges_bestiary.entity.AI.goal.IdleInPlaceGoal;
import com.hedge.hedges_bestiary.entity.types.HBAquaticMob;
import com.hedge.hedges_bestiary.entity.types.IdleAnimMob;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class SkibEntity extends HBAquaticMob implements IdleAnimMob, Bucketable {

    public static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(SkibEntity.class, EntityDataSerializers.BOOLEAN);

    private float prevGlowProgress = 0.0f;
    public float glowProgress = 0.0f;

    public final SmoothAnimationState scratchAnimationState = new SmoothAnimationState(0.25F);
    public final SmoothAnimationState hideAnimationState = new SmoothAnimationState(0.1F);

    public SkibEntity(EntityType<? extends SkibEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 20, 0.8f, 1.0f, true);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.setMaxUpStep(1.25f);
    }



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new IdleInPlaceGoal<>(this));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        return Bucketable.bucketMobPickup(pPlayer, pHand, this).orElse(super.mobInteract(pPlayer, pHand));
    }


    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setFromBucket(pCompound.getBoolean("FromBucket"));
    }

    public void saveToBucketTag(ItemStack pStack) {
        Bucketable.saveDefaultDataToBucketTag(this, pStack);
    }

    public void loadFromBucketTag(CompoundTag pTag) {
        Bucketable.loadDefaultDataFromBucketTag(this, pTag);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(HBItems.SKIB_BUCKET.get());
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean pFromBucket) {
        this.entityData.set(FROM_BUCKET, pFromBucket);
    }

    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 8F)
                .add(Attributes.MOVEMENT_SPEED, 0.18F);


    }

    @Override
    protected void clientTick() {
        super.clientTick();
        this.prevGlowProgress = this.glowProgress;
        this.glowProgress+=0.01F;
        if (this.glowProgress >= 1) {
            this.glowProgress = 0.0F;
        }
        if (this.getAnimState() == 1) {
            float radius = this.getBbWidth() * 0.55F;
            float particleCount = (2 + random.nextInt(2)) * radius;
            for (int i1 = 0; i1 < particleCount; i1++) {
                double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
                double motionY = getRandom().nextFloat() * 0.7D + 0.8F;
                double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
                float angle = (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraZ = radius * Mth.cos(angle);
                BlockState groundState = this.level().getBlockState(this.blockPosition().below());
                if (groundState.isSolid()) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, this.getX() + extraX, this.getY(), this.getZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        }
    }

    @Override
    public void setUpAnimStates() {
        super.setUpAnimStates();
        this.scratchAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
        this.hideAnimationState.animateWhen(this.getAnimState() == 2, this.tickCount);

    }

    @Override
    protected void serverTick() {
        super.serverTick();
        if (this.getAnimState() > 0) {
            animTicks++;
            switch (this.getAnimState()) {
                case 1 -> {
                    if (this.animTicks >= 200) {
                        if (this.getRandom().nextBoolean()) {
                            this.setAnimState(2);
                        } else {
                            this.resetAnimState();
                        }
                    } else if (this.getLastHurtMob() != null) {
                        this.resetAnimState();
                    }
                }
                case 2 -> {
                    if (this.animTicks >= 400 || this.getLastHurtMob() != null) {
                        this.resetAnimState();
                    }
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!pSource.is(DamageTypeTags.IS_PROJECTILE)) {
            if (pSource.getEntity() != null) {
                if (this.level().isClientSide()) {
                    for (int i = 0; i < 10; i++) {
                        Vec3 rand = EntityHelpers.getRandomVec3(this.getRandom(), 1);
                        this.level().addParticle(HBParticles.MURK_CHARGE.get(), this.getX() + rand.x,
                                this.getY() + rand.y / 2 + 0.7, this.getZ() + rand.z, rand.x, rand.y + 0.2, rand.z);
                    }
                    this.glowProgress = 0.5F;
                }
                pSource.getEntity().hurt(this.damageSources().thorns(this), 1.5F);
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.getAnimState() > 0) {
            super.travel(this.getDeltaMovement().multiply(0, 1, 0));
        } else if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.6F));
            if (this.jumping) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.3D, 0));
            } else {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.06D, 0));

            }
        } else {
            super.travel(pTravelVector);
        }
    }

    public float getGlowProgress(float partialTicks) {
        return (prevGlowProgress + (glowProgress - prevGlowProgress) * partialTicks);
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    protected void handleAirSupply(int pAirSupply) {

    }

    @Override
    public void playIdle() {
    }

    @Override
    public boolean canPlayIdle() {
        return false;
    }

    @Override
    public void playStaticIdle() {
        this.setAnimState(1);

    }

    @Override
    public boolean canPlayStaticIdle() {
        return this.getAnimState() == 0 && this.getLastHurtMob() == null && this.getBlockStateOn().is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public boolean isStaticIdling() {
        return this.getAnimState() > 0;
    }

    public static boolean canSpawn(EntityType<? extends SkibEntity> skib, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getFluidState(pPos).is(FluidTags.WATER) && pPos.getY() < pLevel.getSeaLevel() - 5;
    }
}
