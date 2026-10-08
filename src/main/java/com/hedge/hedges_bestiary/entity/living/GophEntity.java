package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.entity.ai.goal.*;
import com.hedge.hedges_bestiary.entity.ai.navigation.HBPathNavigatorGround;
import com.hedge.hedges_bestiary.entity.types.EggLayer;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.items.TreatItem;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GophEntity extends HBTamableAnimal implements EggLayer {
    public final SmoothAnimationState digAnimationState = new SmoothAnimationState(0.1F);
    public final SmoothAnimationState stretchAnimationState = new SmoothAnimationState();

    public GophEntity(EntityType<? extends GophEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder bakeAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 8F)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setUpAnimStates();
            if (this.getAnimState() == 2 || this.hasEgg()) {
                this.tickDig();
            }
        } else {
            if (this.getAnimState() > 0) {
                this.animTicks++;
                switch (this.getAnimState()) {
                    case 1 -> {
                        if (this.animTicks > 39) {
                            this.resetAnimState();
                        }
                    }
                    case 2 -> {
                        if (this.animTicks > 30) {
                            this.setNapping(true);
                            this.resetAnimState();
                        } else if (this.animTicks % 10 == 0) {
                            this.playSound(level().getBlockState(this.blockPosition().below()).getSoundType().getBreakSound());
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult interactTameCommands(Player player, @NotNull InteractionHand hand) {

        InteractionResult result = super.interactTameCommands(player, hand);
        if (result == InteractionResult.PASS && !this.isTame() && !this.isNapping()) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (!this.level().isClientSide && itemStack.getItem() instanceof TreatItem treat && treat.getTier() >= 0) {
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
        return result;
    }

    @Override
    public void setUpAnimStates() {
        super.setUpAnimStates();
        this.stretchAnimationState.animateWhen(this.getAnimState() == 1, this.tickCount);
        this.digAnimationState.animateWhen(this.getAnimState() == 2 || this.hasEgg(), this.tickCount);
    }

    @Override
    public void tickDig() {
        float radius = getBbWidth() * 0.55F;
        float particleCount = (5 + getRandom().nextInt(5)) * radius;
        for (int i1 = 0; i1 < particleCount; i1++) {
            double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
            double motionY = getRandom().nextFloat() * 0.7D + 0.8F;
            double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
            float angle = (0.01745329251F * (yBodyRot + (i1 / particleCount) * 360F));
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockState groundState = level().getBlockState(blockPosition().below());
            if (groundState.isSolid()) {
                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, getX() + extraX, getY(), getZ() + extraZ, motionX, motionY, motionZ);
            }
        }

    }

    @Override
    protected void registerGoals() {
        int i = 0;
        this.goalSelector.addGoal(i++, new FloatGoal(this));
        this.goalSelector.addGoal(i++, new HBSitWhenOrderedGoal(this, false));
        this.goalSelector.addGoal(i++, new HBFollowOwnerGoal(this, 1.2D, 1.4D, 7.0f, 10.0f));
        this.goalSelector.addGoal(i++, new MoveToHomePosGoal(this));
        this.goalSelector.addGoal(i++, new NapGoal(this));
        this.goalSelector.addGoal(i++, new RandomlySitGoal(this));
        this.goalSelector.addGoal(i++, new LookAtPlayerGoal(this, LivingEntity.class, 5));
        this.goalSelector.addGoal(i++, new WaterAvoidingRandomStrollGoal(this, 1.0) {
            @Override
            public boolean canUse() {
                return GophEntity.this.getAnimState() != 2 && super.canUse();
            }
        });
        this.goalSelector.addGoal(i++, new DancingGoal(this));
        this.goalSelector.addGoal(i++, new IdleAnimationGoal<>(this));
        this.goalSelector.addGoal(i, new RandomLookAroundGoal(this));

    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.getAnimState() == 2) {
            super.travel(Vec3.ZERO);
        }
        super.travel(travelVector);
    }

    @Override
    public void setNapping(boolean b) {
        if (this.getAnimState() == 0 && b) {
            this.getNavigation().stop();
            this.setAnimState(2);
        } else {
            super.setNapping(b);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new HBPathNavigatorGround(this, level);
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
    public void playIdle() {
        this.setAnimState(1);
    }

    @Override
    public boolean canPlayIdle() {
        return super.canPlayIdle() && !this.isNapping();
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return super.isFood(pStack) && pStack.is(Items.FROGSPAWN);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public SleepType getSleepType() {
        return SleepType.VESPERTINE;
    }

    @Override
    public BlockState getEgg() {
        return null;
    }

    @Override
    public boolean hasEgg() {
        return false;
    }

    @Override
    public void setHasEgg(boolean b) {

    }
}
