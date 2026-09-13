package com.hedge.hedges_bestiary.networking.packet;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record DanceJukeboxPacket(int entityId, boolean dance, BlockPos jukeBox) implements CustomPacketPayload {

    public static final Type<DanceJukeboxPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "dance_jukebox_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DanceJukeboxPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            DanceJukeboxPacket::entityId,

            ByteBufCodecs.BOOL,
            DanceJukeboxPacket::dance,

            BlockPos.STREAM_CODEC,
            DanceJukeboxPacket::jukeBox,

            DanceJukeboxPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
