package com.hedge.hedges_bestiary.entity.living.ambientfish;

import com.hedge.hedges_bestiary.entity.ai.control.SwimmingMoveControl;
import com.hedge.hedges_bestiary.entity.ai.goal.GroupFollowLeaderGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.CustomSwimGoal;
import com.hedge.hedges_bestiary.entity.ai.goal.JumpFromWaterGoal;
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

public class GildGliderEntity extends HBBucketableSchoolingMob {
    public int groundTimer = 0;
    private boolean canJump = false;

    public GildGliderEntity(EntityType<? extends GildGliderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SwimmingMoveControl(this, 999, 5, 0.02f, 0.1f);
        this.lookControl = new SmoothSwimmingLookControl(this, 5);

    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 1.2F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GroupFollowLeaderGoal<>(this));
        this.goalSelector.addGoal(1, new CustomSwimGoal(this, 1.0f, 10, 10, 5, true));
        this.goalSelector.addGoal(2, new LeaveGroupGoal<>(this));
        this.goalSelector.addGoal(3, new GildGliderJumpGoal(this));
    }

    @Override
    public int getMaxGroupSize() {
        return 15;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isInFluidType() && this.onGround()) {
            this.groundTimer = 20;
        } else {
            this.groundTimer = Math.max(groundTimer - 1, 0);
        }
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
    public boolean canBeFollowed() {
        return super.canBeFollowed();
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new WaterBoundPathNavigation(this, pLevel);
    }

    public boolean canJump() {
        return this.canJump;
    }

    public void setCanJump(boolean b) {
        this.canJump = b;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(HBItems.GILD_GLIDER_BUCKET.get());
    }

    public static boolean canSpawn(EntityType<GildGliderEntity> entity, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entity, level, reason, pos, random);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        if ((pReason == MobSpawnType.CHUNK_GENERATION || pReason == MobSpawnType.NATURAL)) {
            int groupSize = (int) (this.getMaxGroupSize() * this.getRandom().nextFloat());
            if (groupSize > 0 && !this.level().isClientSide()) {
                for (int i = 0; i < groupSize; i++) {
                    Vec3 rand = EntityHelpers.getRandomVec3(pLevel.getRandom(), 4);
                    GildGliderEntity entity = new GildGliderEntity(HBEntities.GILD_GLIDER.get(), this.level());
                    entity.moveTo(this.getX() + rand.x, this.getY() + rand.y, this.getZ() + rand.z);
                    entity.startFollowing(this);
                    this.level().addFreshEntity(entity);
                }
            }
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);

    }

    static class GildGliderJumpGoal extends JumpFromWaterGoal {

        private final GildGliderEntity mob;

        public GildGliderJumpGoal(GildGliderEntity mob) {
            super(mob, 10, 0.8);
            this.mob = mob;
        }

        @Override
        public boolean canUse() {

            if (!this.mob.isFollower()) {
                if (this.mob.getRandom().nextInt(this.interval) != 0) {
                    return false;
                }
                return this.canJump();
            } else if (((GildGliderEntity)this.mob.getLeader()).canJump()) {
                return true;
            }
            return super.canUse();

        }

        @Override
        public void start() {
            super.start();
            this.mob.setCanJump(true);
        }

        @Override
        public void stop() {
            super.stop();
            this.mob.setCanJump(false);
        }
    }
}
