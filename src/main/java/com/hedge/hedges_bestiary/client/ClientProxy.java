package com.hedge.hedges_bestiary.client;

import com.hedge.hedges_bestiary.CommonProxy;
import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = HedgesBestiary.MODID, value = Dist.CLIENT)

public class ClientProxy extends CommonProxy {

    public static List<UUID> blockedEntityRenders = new ArrayList<>();

    @Override
    public void init() {
        IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
    }

    public void blockRenderingEntity(UUID id) {
        blockedEntityRenders.add(id);
    }

    public void releaseRenderingEntity(UUID id) {
        blockedEntityRenders.remove(id);
    }

    public boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity) && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

}
