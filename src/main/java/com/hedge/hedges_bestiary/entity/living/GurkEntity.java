package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.blocks.HBBlocks;
import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticLookControl;
import com.hedge.hedges_bestiary.entity.ai.control.SemiaquaticMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBAmphibiousPathNavigator;
import com.hedge.hedges_bestiary.entity.types.EggLayer;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.types.VariantMob;
import com.hedge.hedges_bestiary.items.TreatItem;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class GurkEntity extends HBTamableAnimal implements VariantMob, EggLayer {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(GurkEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(GurkEntity.class, EntityDataSerializers.BOOLEAN);


    public final SmoothAnimationState standAnimationState = new SmoothAnimationState();
    public GurkEntity(EntityType<? extends GurkEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);


        this.moveControl = new SemiaquaticMoveControl(this, 999, 10, 0.25f);
        this.lookControl = new SemiaquaticLookControl(this, 30);


        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0f);

    }

    @Override
    protected boolean canOwnerMount(Player player) {
        return false;
    }

    @Override
    protected boolean canOwnerCommand(Player player) {
        return true;
    }

    public static boolean canSpawn(EntityType<? extends GurkEntity> gurk, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pSpawnType == MobSpawnType.SPAWNER || pLevel.getFluidState(pPos).is(FluidTags.WATER) && pPos.getY() > pLevel.getSeaLevel() - 10;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(HAS_EGG, false);

    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new HBAmphibiousPathNavigator(this, pLevel);
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 8F)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }



    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this));
        this.goalSelector.addGoal(i++, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(i++, new EggLayerBreedGoal<>(this, 1.0f));
        this.goalSelector.addGoal(i++, new LayEggsGoal<>(this, 100, 1.0f));
        this.goalSelector.addGoal(i++, new HBTemptGoal(this, 1.1f, Ingredient.of(Blocks.SEAGRASS.asItem()), false));
        this.goalSelector.addGoal(i++, new AquaticFollowOwnerGoal(this, 1.2, 1.3, 4.0f, 2.0f, true));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new NapGoal(this));
        this.goalSelector.addGoal(i++, new IdleInPlaceGoal<>(this));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 5));
        this.goalSelector.addGoal(i++, new CustomSwimGoal(this, 1.0, 10, 10, 4, true, true));
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
        this.goalSelector.addGoal(i++, new DancingGoal(this));
        this.goalSelector.addGoal(i, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setUpAnimStates();
            if (this.hasEgg() && this.onGround()) {
                this.tickDig();
            }
        } else {
            if (this.tickCount % 200 == 0) {
                this.heal(5);
            }
            if (this.getAnimState() == 1) {
                this.animTicks++;
                if (this.animTicks == 65 || this.getNavigation().isInProgress() || this.isNapping()) {
                    this.resetAnimState();
                }
            }
        }
    }

    @Override
    public void setUpAnimStates() {
        super.setUpAnimStates();
        this.standAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!this.isTame() && this.isTamable() && itemStack.getItem() instanceof TreatItem treat && treat.getTier() >= 0) {
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
        return type;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {

        if (this.getRandom().nextInt(40) == 0) {
            this.setVariant(3);
        } else {
            Holder<Biome> biome = pLevel.getBiome(this.blockPosition());
            if (biome.is(BiomeTags.IS_RIVER)) {
                this.setVariant(1);
            } else if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_BEACH)) {
                this.setVariant(2);
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void setVariant(int i) {
        this.entityData.set(VARIANT, i);
    }

    @Override
    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    @Override
    public void playIdle() {
    }

    @Override
    public boolean isTamable() {
        return super.isTamable() && HBConfig.GURK_IS_TAMABLE;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return HBEntities.GURK.get().create(level);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return super.isFood(stack) && stack.is(Blocks.SEAGRASS.asItem());
    }

    @Override
    public BlockState getEgg() {
        return HBBlocks.GURK_EGG.get().defaultBlockState();
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
    public boolean laysMultipleEggs() {
        return true;
    }

    @Override
    public boolean canPlayStaticIdle() {
        return super.canPlayStaticIdle() && !this.isInFluidType() && !this.isNapping() && !this.isSitting();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return HBSounds.GURK_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return HBSounds.GURK_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return HBSounds.GURK_DIE.get();
    }
}
