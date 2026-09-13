package com.hedge.hedges_bestiary;

import com.hedge.hedges_bestiary.blocks.HBBlocks;
import com.hedge.hedges_bestiary.client.ClientProxy;
import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.items.HBCreativeTab;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.concurrent.Executor;


@Mod(HedgesBestiary.MODID)
@EventBusSubscriber(modid = HedgesBestiary.MODID)
public class HedgesBestiary
{
    public static final String MODID = "hedges_bestiary";

    public static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation(MODID + ":main_channel");
    @OnlyIn(Dist.CLIENT)
    public static final CommonProxy PROXY = new ClientProxy();

    public HedgesBestiary(IEventBus modEventBus,  ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, HBConfig.SPEC, "hedges_bestiary.toml");

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::onModConfigEvent);
        NeoForge.EVENT_BUS.register(this);
        HBEntities.register(modEventBus);
        HBMenus.register(modEventBus);

        HBBlocks.registerBlocks(modEventBus);
        HBBlockEntities.register(modEventBus);
        HBItems.register(modEventBus);
        HBEffects.register(modEventBus);
        HBParticles.register(modEventBus);
        HBSounds.register(modEventBus);
        HBStructures.register(modEventBus);
        HBStructurePieces.register(modEventBus);
        HBCreativeTab.register(modEventBus);
        PROXY.init();

    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == HBConfig.SPEC) {
            HBConfig.bake();
        }
    }


    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
}
