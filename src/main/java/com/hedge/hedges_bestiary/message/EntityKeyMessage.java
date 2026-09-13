package com.hedge.hedges_bestiary.message;

import com.hedge.hedges_bestiary.entity.types.KeybindUsing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityKeyMessage {

    public int entityId;
    public int playerId;
    public int type;

    public EntityKeyMessage(int entityId, int playerId, int type) {
        this.entityId = entityId;
        this.playerId = playerId;
        this.type = type;
    }


    public EntityKeyMessage() {
    }

    public static EntityKeyMessage read(FriendlyByteBuf buf) {
        return new EntityKeyMessage(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(EntityKeyMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeInt(message.playerId);
        buf.writeInt(message.type);
    }


    public static void handle(EntityKeyMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player playerSided = context.get().getSender();
            //if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            //    playerSided = hedgesBestiary.PROXY.getClientSidePlayer();
            //}
            Entity parent = playerSided.level().getEntity(message.entityId);
            Entity keyPresser = playerSided.level().getEntity(message.playerId);
            if (parent instanceof KeybindUsing creature && keyPresser instanceof Player) {
                creature.onKeyPacket(keyPresser, message.type);
            }
        });
        context.get().setPacketHandled(true);
    }

}
