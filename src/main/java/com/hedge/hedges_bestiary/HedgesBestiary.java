package com.hedge.hedges_bestiary;

import com.hedge.hedges_bestiary.blocks.HBBlocks;
import com.hedge.hedges_bestiary.client.ClientProxy;
import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.items.HBCreativeTab;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.menu.HBTamableMenuScreen;
import com.hedge.hedges_bestiary.message.DanceJukeboxMessage;
import com.hedge.hedges_bestiary.message.EntityKeyMessage;
import com.hedge.hedges_bestiary.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

@Mod(HedgesBestiary.MODID)
@Mod.EventBusSubscriber(modid = HedgesBestiary.MODID)
public class HedgesBestiary
{
    public static final String MODID = "hedges_bestiary";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation(MODID + ":main_channel");

    public static final SimpleChannel NETWORK_WRAPPER = NetworkRegistry.ChannelBuilder
            .named(PACKET_NETWORK_NAME)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public HedgesBestiary(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, HBConfig.SPEC, "hedges_bestiary.toml");

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::onModConfigEvent);
        MinecraftForge.EVENT_BUS.register(this);
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

    public static <MSG> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.sendToServer(message);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        int packetsRegistered = 0;
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, DanceJukeboxMessage.class, DanceJukeboxMessage::write, DanceJukeboxMessage::read, DanceJukeboxMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, EntityKeyMessage.class, EntityKeyMessage::write, EntityKeyMessage::read, EntityKeyMessage::handle);

    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(HBMenus.TAMABLE_MENU.get(), HBTamableMenuScreen::new);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
}
