package com.hedge.hedges_bestiary.entity.living.ambientfish;

import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.GroupFollowLeaderGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.CustomSwimGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.LeaveGroupGoal;
import com.hedge.hedges_bestiary.entity.types.HBBucketableSchoolingMob;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.HBEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ChubEntity extends HBBucketableSchoolingMob {


    public ChubEntity(EntityType<? extends ChubEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 15, 0.02f, 0.1f);
        this.lookControl = new SmoothSwimmingLookControl(this, 20);
    }



    public static AttributeSupplier.Builder bakeAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 1.1F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GroupFollowLeaderGoal<>(this));
        this.goalSelector.addGoal(1, new CustomSwimGoal(this, 1.0f, 10, 10, 10, true));
        this.goalSelector.addGoal(2, new LeaveGroupGoal<>(this));

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
    protected PathNavigation createNavigation(Level pLevel) {
        return new WaterBoundPathNavigation(this, pLevel);
    }

    public static boolean canSpawn(EntityType<ChubEntity> entity, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entity, level, reason, pos, random);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(HBItems.CHUB_BUCKET.get());
    }


    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        if ((pReason == MobSpawnType.CHUNK_GENERATION || pReason == MobSpawnType.NATURAL)) {
            int groupSize = (int) (this.getMaxGroupSize() * this.getRandom().nextFloat());
            if (groupSize > 0 && !this.level().isClientSide()) {
                for (int i = 0; i < groupSize; i++) {
                    Vec3 rand = EntityHelpers.getRandomVec3(pLevel.getRandom(), 4);
                    ChubEntity entity = new ChubEntity(HBEntities.CHUB.get(), this.level());
                    entity.moveTo(this.getX() + rand.x, this.getY() + rand.y, this.getZ() + rand.z);
                    entity.startFollowing(this);
                    this.level().addFreshEntity(entity);
                }
            }
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);

    }

}
