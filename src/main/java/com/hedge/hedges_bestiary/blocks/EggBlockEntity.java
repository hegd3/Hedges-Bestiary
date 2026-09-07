package com.hedge.hedges_bestiary.blocks;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.registry.HBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class EggBlockEntity<E extends EntityType<?>> extends BlockEntity {

    protected final RegistryObject<E> toHatch;
    private int ticksTillHatch = 2000;
    private String ownerUUID = "";

    public EggBlockEntity(BlockPos pos, BlockState blockState) {
        super(HBBlockEntities.EGG_BLOCK_ENTITY.get(), pos, blockState);
        this.toHatch = ((EggBlock<E>)blockState.getBlock()).getToHatch();
    }

    public EggBlockEntity(BlockPos pos, BlockState blockState, RegistryObject<E> toHatch) {
        super(HBBlockEntities.EGG_BLOCK_ENTITY.get(), pos, blockState);
        this.toHatch = toHatch;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return super.getModelData();
    }



    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putString("ownerUUID", ownerUUID);
        tag.putInt("ticksTillHatch", ticksTillHatch);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.ownerUUID = tag.getString("ownerUUID");
        this.ticksTillHatch = tag.getInt("ticksTillHatch");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);

        this.ownerUUID = tag.getString("ownerUUID");
        this.ticksTillHatch = tag.getInt("ticksTillHatch");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = this.saveWithFullMetadata();
        tag.putString("ownerUUID", ownerUUID);
        tag.putInt("ticksTillHatch", ticksTillHatch);

        return super.getUpdateTag();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            if(ticksTillHatch-- <= 0 && toHatch != null){
                if (state.getBlock() instanceof MultiEggBlock) {
                    for (int i = 0; i < state.getValue(MultiEggBlock.EGGS); i++) {
                        HBTamableAnimal baby = (HBTamableAnimal) toHatch.get().create(level);
                        baby.setPos(this.getBlockPos().getCenter());
                        baby.setBaby(true);
                        baby.setTame(true);
                        baby.setOwnerUUID(this.getOwnerUUID());
                        baby.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(pos), MobSpawnType.BREEDING, null, null);
                        baby.playSound(SoundEvents.TURTLE_EGG_HATCH);
                        level.addFreshEntity(baby);
                        level.destroyBlock(this.getBlockPos(), false);
                        this.setRemoved();
                    }
                } else {
                    HBTamableAnimal baby = (HBTamableAnimal) toHatch.get().create(level);
                    baby.setPos(this.getBlockPos().getCenter());
                    baby.setBaby(true);
                    baby.setTame(true);
                    baby.setOwnerUUID(this.getOwnerUUID());
                    baby.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(pos), MobSpawnType.BREEDING, null, null);
                    baby.playSound(SoundEvents.TURTLE_EGG_HATCH);
                    level.addFreshEntity(baby);
                    level.destroyBlock(this.getBlockPos(), false);
                    this.setRemoved();
                }
            } else {
                if (this.ticksTillHatch % 100 == 0 && state.getBlock() instanceof EggBlock<?> block) {
                    block.crack(state, level, pos);
                }
            }
            setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        var tag = packet.getTag();
        if (tag != null)
        {
            handleUpdateTag(tag);

            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public UUID getOwnerUUID() {
        if (ownerUUID.isEmpty()) return UUID.randomUUID();
        return UUID.fromString(ownerUUID);
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
        setChanged();
    }
}
