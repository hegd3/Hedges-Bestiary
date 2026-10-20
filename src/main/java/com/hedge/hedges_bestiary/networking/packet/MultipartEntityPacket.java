package com.hedge.hedges_bestiary.networking.packet;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MultipartEntityPacket(int parentId, int playerId, double damage) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MultipartEntityPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "multipart_entity_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MultipartEntityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            MultipartEntityPacket::parentId,

            ByteBufCodecs.INT,
            MultipartEntityPacket::playerId,

            ByteBufCodecs.DOUBLE,
            MultipartEntityPacket::damage,

            MultipartEntityPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
