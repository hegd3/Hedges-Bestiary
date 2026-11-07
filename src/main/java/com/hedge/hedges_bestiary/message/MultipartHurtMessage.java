package com.hedge.hedges_bestiary.message;

import com.hedge.hedges_bestiary.entity.types.KeybindUsing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MultipartHurtMessage {

    public int entityId;
    public int hurterId;
    public float damage;

    public MultipartHurtMessage(int entityId, int hurterId, float damage) {
        this.entityId = entityId;
        this.hurterId = hurterId;
        this.damage = damage;
    }


    public MultipartHurtMessage() {
    }

    public static MultipartHurtMessage read(FriendlyByteBuf buf) {
        return new MultipartHurtMessage(buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public static void write(MultipartHurtMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeInt(message.hurterId);
        buf.writeFloat(message.damage);
    }


    public static void handle(MultipartHurtMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            Entity parent = player.level().getEntity(message.entityId);
            Entity hurter = player.level().getEntity(message.hurterId);
            if (parent != null && !parent.isInvulnerable()) {
                parent.hurt(hurter instanceof LivingEntity e ? hurter.damageSources().mobAttack(e) : parent.damageSources().generic(), message.damage);
            }
        });
        context.get().setPacketHandled(true);
    }

}
