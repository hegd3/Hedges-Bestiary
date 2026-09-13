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


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



}
