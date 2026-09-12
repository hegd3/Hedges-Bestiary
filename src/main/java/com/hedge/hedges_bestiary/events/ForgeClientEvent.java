package com.hedge.hedges_bestiary.events;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.ClientProxy;
import com.hedge.hedges_bestiary.entity.types.HUDMount;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = HedgesBestiary.MODID, value = Dist.CLIENT)

public class ForgeClientEvent {

    @SubscribeEvent
    public static void preRenderLiving(RenderLivingEvent.Pre event) {
        if (ClientProxy.blockedEntityRenders.contains(event.getEntity().getUUID())) {
            if (!HedgesBestiary.PROXY.isFirstPersonPlayer(event.getEntity())) {
                NeoForge.EVENT_BUS.post(new RenderLivingEvent.Post(event.getEntity(), event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
                event.setCanceled(true);
            }
            ClientProxy.blockedEntityRenders.remove(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPreRenderGuiOverlay(RenderGuiLayerEvent.Pre event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && player.getVehicle() instanceof HUDMount) {
            if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR) ||
                event.getName().equals(VanillaGuiLayers.VEHICLE_HEALTH))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPostRenderGuiOverlay(RenderGuiLayerEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR)&& player.getVehicle() instanceof HUDMount mount) {
            event.getGuiGraphics().pose().pushPose();
            mount.renderHUD(event.getGuiGraphics());
            event.getGuiGraphics().pose().popPose();
        }
    }


    /*
    @SubscribeEvent
    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {

        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && player.getVehicle() instanceof HUDMount && event.getCamera().isDetached()) {
            event.getCamera().move(-3, 0.5, 0);
        }
    }

     */



}
