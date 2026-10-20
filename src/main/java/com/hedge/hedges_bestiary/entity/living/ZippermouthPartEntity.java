package com.hedge.hedges_bestiary.entity.living;

import com.hedge.hedges_bestiary.networking.packet.MultipartEntityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public class ZippermouthPartEntity extends PartEntity<ZippermouthEntity> {

    private static final EntityDimensions SIZE = EntityDimensions.fixed(3.56f, 1.6f);

    public ZippermouthPartEntity(ZippermouthEntity parent) {
        super(parent);
        this.blocksBuilding = true;
        this.refreshDimensions();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        ZippermouthEntity parent = this.getParent();
        if (!parent.isInvulnerable()) {
            if (this.level().isClientSide) {
                Entity hurter = source.getEntity();
                amount*=0.5F;
                if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                    amount *= 0.35F;
                }
                PacketDistributor.sendToServer(new MultipartEntityPacket(parent.getId(), hurter != null ? hurter.getId() : -1, amount));
            }

        }
        return false;
    }



    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return this.getParent().mobInteract(player, hand);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return SIZE;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean save(CompoundTag tag) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.getParent().canBeCollidedWith();
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(2.0D, 0.5D, 2.0D);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPickable() {
        return this.getParent().isPickable();
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    public void setPosCenteredY(Vec3 pos) {
        this.setPos(pos.x, pos.y - this.getBbHeight() * 0.5F, pos.z);
    }

    public Vec3 centeredPosition() {
        return this.position().add(0, this.getBbHeight() * 0.5F, 0);
    }

    public Vec3 centeredPosition(float partialTicks) {
        return this.getPosition(partialTicks).add(0, this.getBbHeight() * 0.5F, 0);
    }
}
