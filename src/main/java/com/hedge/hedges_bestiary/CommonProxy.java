package com.hedge.hedges_bestiary;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.UUID;

public class CommonProxy {

    public void init() {

    }






    public void blockRenderingEntity(UUID id) {
    }

    public void releaseRenderingEntity(UUID id) {
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFirstPersonPlayer(Entity entity) {
        return false;
    }

}
