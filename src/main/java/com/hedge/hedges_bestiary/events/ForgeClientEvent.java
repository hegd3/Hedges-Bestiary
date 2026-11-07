package com.hedge.hedges_bestiary.events;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.ClientProxy;
import com.hedge.hedges_bestiary.entity.types.HUDMount;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HedgesBestiary.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)

public class ForgeClientEvent {

    @SubscribeEvent
    public static void preRenderLiving(RenderLivingEvent.Pre event) {
        if (ClientProxy.blockedEntityRenders.contains(event.getEntity().getUUID())) {
            if (!HedgesBestiary.PROXY.isFirstPersonPlayer(event.getEntity())) {
                event.setCanceled(true);
            }
            ClientProxy.blockedEntityRenders.remove(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && player.getVehicle() instanceof HUDMount) {
            if (event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id()) ||
                event.getOverlay().id().equals(VanillaGuiOverlay.MOUNT_HEALTH.id()))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())&& player.getVehicle() instanceof HUDMount mount) {
            event.getGuiGraphics().pose().pushPose();
            mount.renderHUD(event.getGuiGraphics());
            event.getGuiGraphics().pose().popPose();
        }
    }


    @SubscribeEvent
    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {

        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && player.getVehicle() instanceof HUDMount mount && event.getCamera().isDetached()) {
            event.getCamera().move(mount.getCameraOffsetZ(), mount.getCameraOffsetY(), mount.getCameraOffsetX());
        }
    }



}
