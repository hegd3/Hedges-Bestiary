package com.hedge.hedges_bestiary.networking.packet;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.entity.types.KeybindUsing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.function.Supplier;

public record EntityKeyPacket(int entityId, int playerId, int key) implements CustomPacketPayload {

    public static final Type<EntityKeyPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "entity_key_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityKeyPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            EntityKeyPacket::entityId,

            ByteBufCodecs.INT,
            EntityKeyPacket::playerId,

            ByteBufCodecs.INT,
            EntityKeyPacket::key,

            EntityKeyPacket::new
    );

    public EntityKeyPacket(int entityId, int playerId, int key) {
        this.entityId = entityId;
        this.playerId = playerId;
        this.key = key;
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /*

    public static void handle(EntityKeyPacket message, Supplier<ClientPlayerNetworkEvent> context) {
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
     */

}
