package com.hedge.hedges_bestiary.entity.types;

import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.menu.HBTamableMenu;
import com.hedge.hedges_bestiary.message.DanceJukeboxMessage;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.util.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class HBTamableAnimal extends TamableAnimal implements AnimStateMob, IdleAnimMob, KeybindUsing {
    public final SmoothAnimationState sitAnimationState = new SmoothAnimationState(0.25f);
    public final SmoothAnimationState napAnimationState = new SmoothAnimationState(0.1f);
    public final SmoothAnimationState idleAnimationState = new SmoothAnimationState(0.1f);
    public final SmoothAnimationState danceAnimationState = new SmoothAnimationState();

    protected static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TAME_COMMAND = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_DANCING = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_NAPPING = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> HAS_HOME = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> AUTO_TARGET_TYPE = SynchedEntityData.defineId(HBTamableAnimal.class, EntityDataSerializers.INT);

    protected int animTicks = 0;
    protected SimpleContainer inventory;
    protected boolean hasDroppedChest = false;
    @Nullable
    private BlockPos jukebox;

    public HBTamableAnimal(EntityType<? extends HBTamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (this.hasInventory()) {
            this.createInventory();
        }
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (this.isTame()) {
            if (pEntity instanceof OwnableEntity e) {
                if (Objects.equals(e.getOwnerUUID(), this.getOwnerUUID())) {
                    return true;
                }
            }
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult type = super.mobInteract(player, hand);

        if (!type.consumesAction() && this.isTamable()) {
            return this.interactTameCommands(player, hand);
        }
        return type;
    }

    public InteractionResult interactTameCommands(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isTame() && player.isCreative() && !this.level().isClientSide() && itemstack.isEmpty()) {
            this.level().broadcastEntityEvent(this, (byte) 7);
            this.tame(player);
            this.heal(this.getMaxHealth());
            return InteractionResult.SUCCESS;
        }
        else if (this.isOwnedBy(player) && !this.isFood(itemstack)) {
            if (this.canOwnerCommand(player)) {
                this.openCustomInventoryScreen(player);
                return InteractionResult.SUCCESS;
            } else if (this.canOwnerMount(player)) {
                if (!level().isClientSide && player.startRiding(this)) {
                    return InteractionResult.CONSUME;
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }


    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (containerId, playerInventory, p) ->
                            new HBTamableMenu(containerId, this),
                    this.getName()
            ), buf -> buf.writeInt(this.getId())
            );

        }
    }

    public boolean canAccessInventory() {
        return false;
    }

    public boolean hasInventory() {
        return false;
    }

    public int getInventorySize() {
        return 27;
    }

    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize()) {
            @Override
            public boolean stillValid(Player pPlayer) {
                return HBTamableAnimal.this.isAlive() && !HBTamableAnimal.this.isInsidePortal;
            }
        };
        if (simplecontainer != null) {
            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

            for(int j = 0; j < i; j++) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

    }


    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory != null) {
            List<ItemStack> items = inventory.removeAllItems();
            items.forEach(item -> {spawnAtLocation(item, 0.0F);});

        }
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        if (this.hasInventory() && this.inventory != null && !this.level().isClientSide) {
            List<ItemStack> items = inventory.removeAllItems();
            items.forEach(item -> {spawnAtLocation(item, 0.0F);});

        }
    }

    public void openInventory(Player playerEntity) {
        if (!this.hasPassenger(playerEntity)) {
            NetworkHooks.openScreen((ServerPlayer) playerEntity, new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                    return ChestMenu.threeRows(p_createMenu_1_, p_createMenu_2_, inventory);
                }

                @Override
                public Component getDisplayName() {
                    return HBTamableAnimal.this.getDisplayName();
                }

            });
        }
    }



    protected abstract boolean canOwnerMount(Player player);

    protected abstract boolean canOwnerCommand(Player player);

    public boolean isTamable() {
        return !HBConfig.TAMING_DISABLED;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickNap();
        if (this.hasDroppedChest && inventory != null && !this.hasInventory()) {
            for (int i = 3; i < 18; i++) {
                if (!inventory.getItem(i).isEmpty()) {
                    if (!this.level().isClientSide) {
                        this.spawnAtLocation(inventory.getItem(i), 1);
                    }
                    inventory.removeItemNoUpdate(i);
                }
            }
            hasDroppedChest = false;
        }
    }

    protected void tickNap() {
        if (this.isNapping()) {
            if (this.level().isClientSide() && this.tickCount % 30 == 0) {
                Vec3 rand = this.getEyePosition().add(EntityHelpers.getRandomVec3(this.getRandom(), 0.6));
                this.level().addParticle(HBParticles.SLEEP.get(), rand.x, rand.y + this.getBbHeight() / 2, rand.z, rand.x, 0.1, rand.z);
            } else if (this.getNavigation().isInProgress()) {
                this.setNapping(false);
            }
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HOME_POS, BlockPos.ZERO);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(IS_SITTING, false);
        this.entityData.define(IS_DANCING, false);
        this.entityData.define(IS_NAPPING, false);
        this.entityData.define(HAS_HOME, false);
        this.entityData.define(AUTO_TARGET_TYPE, 0);
        this.entityData.define(TAME_COMMAND, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSitting(compound.getBoolean("Is_Sitting"));
        this.setNapping(compound.getBoolean("Is_Napping"));
        this.setHasHome(compound.getBoolean("Has_Home"));
        if (this.hasHome()) {
            int i = compound.getInt("HomePosX");
            int j = compound.getInt("HomePosY");
            int k = compound.getInt("HomePosZ");
            this.setHomePos(new BlockPos(i, j, k));
        }
        if (this.isTame()) {
            this.setCommand(compound.getInt("Tame_Command"));
            this.setAutoTargetType(compound.getInt("Auto_Target_Type"));
        }
        if (this.hasInventory()) {
            if (this.inventory != null) {
                ListTag nbttaglist = compound.getList("Items", 10);
                this.createInventory();
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                    int j = CompoundNBT.getByte("Slot") & 255;
                    this.inventory.setItem(j, ItemStack.of(CompoundNBT));
                }
            } else {
                ListTag nbttaglist = compound.getList("Items", 10);
                this.createInventory();
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                    int j = CompoundNBT.getByte("Slot") & 255;
                    this.createInventory();
                    this.inventory.setItem(j, ItemStack.of(CompoundNBT));
                }
            }
        }



    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Is_Sitting", this.isSitting());
        pCompound.putBoolean("Is_Napping", this.isNapping());
        pCompound.putBoolean("Has_Home", this.hasHome());
        if (this.hasHome()) {
            pCompound.putInt("HomePosX", this.getHomePos().getX());
            pCompound.putInt("HomePosY", this.getHomePos().getY());
            pCompound.putInt("HomePosZ", this.getHomePos().getZ());
        }
        if (this.isTame()) {
            pCompound.putInt("Tame_Command", this.getCommand());
            pCompound.putInt("Auto_Target_Type", this.getAutoTargetType());
        }
        if (inventory != null) {
            ListTag nbttaglist = new ListTag();
            for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag CompoundNBT = new CompoundTag();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.save(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            pCompound.put("Items", nbttaglist);
        }
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (pKey == ANIM_STATE) {
            this.animTicks = 0;
        } else {
            super.onSyncedDataUpdated(pKey);
        }
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.getPose() == Pose.STANDING && !this.isDancing()) {
            f = Math.min(pPartialTick * 6f, 1f);
        } else {
            f = 0;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (this.isTame() && this.getOwner() != null && source.getEntity() == this.getOwner() && !this.getOwner().isCrouching()) {
            return true;
        }
        if (source.is(DamageTypes.IN_WALL)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }



    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        if (super.hurt(source, pAmount)) {
            if(!this.level().isClientSide()) {
                if (!this.isOrderedToSit() && this.isSitting()) {
                    this.setSitting(false);
                } else if (this.isNapping()) {
                    this.setNapping(false);
                }
            }
            return true;
        }
        return false;
    }



    @Override
    public void setAnimState(int i) {
        this.entityData.set(ANIM_STATE, i);
    }

    @Override
    public int getAnimState() {
        return this.entityData.get(ANIM_STATE);
    }

    @Override
    public int getAnimTicks() {
        return this.animTicks;
    }

    public void resetAnimState() {
        this.animTicks = 0;
        this.setAnimState(0);
    }

    @Override
    public void setUpAnimStates() {
        this.idleAnimationState.animateWhen(true, this.tickCount);
        this.sitAnimationState.animateWhen(this.isSitting() && !this.isDancing(), this.tickCount);
        this.napAnimationState.animateWhen(this.isNapping(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
    }

    public void setCommand(int i) {
        this.entityData.set(TAME_COMMAND, i);
    }

    public int getCommand() {
        return this.entityData.get(TAME_COMMAND);
    }

    @Override
    public void tame(Player player) {
        super.tame(player);
        if (this.getTarget() == player) {
            this.setTarget(null);
        }
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (this.isTamable() && !this.isTame()) {
            return false;
        }
        return super.canMate(otherAnimal);
    }

    @Nullable
    public BlockPos getJukebox() {
        return this.jukebox;
    }

    public void setJukeboxPos(BlockPos pos) {
        this.jukebox = pos;
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.onClientPlayMusicDisc(this.getId(), pos, playing);
    }

    public void onClientPlayMusicDisc(int entityId, BlockPos pos, boolean dancing) {
        HedgesBestiary.sendMSGToServer(new DanceJukeboxMessage(entityId, dancing, pos));
        if (dancing) {
            this.jukebox = pos;
        } else {
            this.jukebox = null;
        }
    }

    public void setSitting(boolean b) {
        this.entityData.set(IS_SITTING, b);
    }

    public boolean isSitting() {
        return this.entityData.get(IS_SITTING);
    }

    public void setDancing(boolean b) {
        this.entityData.set(IS_DANCING, b);
    }

    public boolean isDancing() {
        return this.entityData.get(IS_DANCING);
    }

    public boolean isNapping() {
        return this.entityData.get(IS_NAPPING);
    }

    public void setNapping(boolean b) {
        this.entityData.set(IS_NAPPING, b);
    }

    public int getAutoTargetType() {
        return this.entityData.get(AUTO_TARGET_TYPE);
    }

    public void setAutoTargetType(int i) {
        this.entityData.set(AUTO_TARGET_TYPE, i);
    }

    public boolean hasHome() {
        return this.entityData.get(HAS_HOME);
    }

    public void setHasHome(boolean b) {
        this.entityData.set(HAS_HOME, b);
    }

    public void setHomePos(BlockPos pos) {
        this.entityData.set(HOME_POS, pos);
    }

    public BlockPos getHomePos() {
        return this.entityData.get(HOME_POS);
    }

    @Override
    public boolean canPlayIdle() {
        return this.getTarget() == null && this.getAnimState() == 0;
    }

    @Override
    public boolean canPlayStaticIdle() {
        return this.canPlayIdle() && this.onGround();
    }

    @Override
    public void playStaticIdle() {
        this.setAnimState(1);
    }

    @Override
    public boolean isStaticIdling() {
        return this.getAnimState() == 1;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return !HBConfig.BREEDING_REQUIRES_TAME || !this.isTamable() || this.isTame();
    }

    protected void addEatingParticles() {
        float radius = this.getBbWidth() * 0.55F;
        float particleCount = (2 + random.nextInt(2)) * radius;
        for (int i1 = 0; i1 < particleCount; i1++) {
            Vec3 v = this.getEyePosition();
            double motionX = (getRandom().nextFloat() - 0.15F) * 0.4D;
            double motionY = getRandom().nextFloat() * -0.7F - 0.1F;
            double motionZ = (getRandom().nextFloat() - 0.15F) * 0.4D;
            float angle = (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getMainHandItem()), true, v.x + extraX, v.y, v.z + extraZ, motionX, motionY, motionZ);
        }
    }

    @Override
    public void finalizeSpawnChildFromBreeding(ServerLevel pLevel, Animal pAnimal, @org.jetbrains.annotations.Nullable AgeableMob pBaby) {
        super.finalizeSpawnChildFromBreeding(pLevel, pAnimal, pBaby);
        if (this.isTame() && pBaby instanceof HBTamableAnimal animal) {
            animal.setOwnerUUID(this.getOwnerUUID());
            animal.setTame(true);
        }
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {

        switch (type) {
            case 0 -> {
                this.setCommand(this.getCommand() == 2 ? 0 : this.getCommand() + 1);
                this.setOrderedToSit(this.getCommand() == 1);
            }
            case 1 -> {
                if (this.hasHome()) {
                    this.setHasHome(false);
                } else {
                    this.setHasHome(true);
                    this.setHomePos(keyPresser.blockPosition());
                }
            }
            case 2 -> this.setAutoTargetType(this.getAutoTargetType() == 3 ? 0 : this.getAutoTargetType() + 1);
            case 3 -> {
                if (keyPresser instanceof Player player) {
                    player.closeContainer();
                    this.openInventory(player);
                }
            }
        }
    }

    public SleepType getSleepType() {
        return SleepType.DIURNAL;
    }


    public enum SleepType {
        DIURNAL {
            public boolean canSleep(long dayTime) {
                return dayTime > 13000 && dayTime < 23992;
            }
        },
        NOCTURNAL {
            public boolean canSleep(long dayTime) {
                return dayTime < 13000 || dayTime > 23000;
            }
        },
        CATHERMAL {
            public boolean canSleep(long dayTime) {
                return (dayTime < 12000 || dayTime > 18000) && dayTime < 23000 && dayTime > 8000;
            }
        },
        MATUTINAL {
            public boolean canSleep(long dayTime) {
                return dayTime < 23000 && dayTime > 1000;
            }
        },
        VESPERTINE {
            public boolean canSleep(long dayTime) {
                return dayTime < 12000 || dayTime > 18000;
            }
        },
        RESTLESS {
            public boolean canSleep(long dayTime) {
                return false;
            }

        };

        public abstract boolean canSleep(long dayTime);
    }

}
