package com.hedge.hedges_bestiary.networking;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.types.KeybindUsing;
import com.hedge.hedges_bestiary.networking.packet.DanceJukeboxPacket;
import com.hedge.hedges_bestiary.networking.packet.EntityKeyPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {


    public static void handleEntityKeyPacket(EntityKeyPacket entityKeyPacket, IPayloadContext iPayloadContext) {
        Player playerSided = iPayloadContext.player();
        Entity parent = playerSided.level().getEntity(entityKeyPacket.entityId());
        Entity keyPresser = playerSided.level().getEntity(entityKeyPacket.playerId());
        if (parent instanceof KeybindUsing creature && keyPresser instanceof Player) {
            creature.onKeyPacket(keyPresser, entityKeyPacket.key());
        }
    }

    public static void handleDanceJukeboxPacket(DanceJukeboxPacket danceJukeboxPacket, IPayloadContext iPayloadContext) {
        Player player = iPayloadContext.player();
        if (player.level().getEntity(danceJukeboxPacket.entityId()) instanceof HBTamableAnimal dancer) {
            dancer.setDancing(danceJukeboxPacket.dance());
        }

    }
}
