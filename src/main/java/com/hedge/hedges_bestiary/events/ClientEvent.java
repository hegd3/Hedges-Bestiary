package com.hedge.hedges_bestiary.events;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.EntityLayers;
import com.hedge.hedges_bestiary.client.models.*;
import com.hedge.hedges_bestiary.client.particle.*;
import com.hedge.hedges_bestiary.client.renderer.*;
import com.hedge.hedges_bestiary.client.renderer.projectile.WaveRenderer;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBKeyMappings;
import com.hedge.hedges_bestiary.registry.HBParticles;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = HedgesBestiary.MODID, value = Dist.CLIENT)

public class ClientEvent {


    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(HBKeyMappings.MOUNT_ABILITY_KEY);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EntityLayers.BURODON_LAYER, BurodonModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.SPOTTED_STRIKER_LAYER, SpottedStrikerModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.PLOMBO_LAYER, PlomboModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.GURK_LAYER, GurkModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.MURK_LAYER, MurkModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.TEARACUDA_LAYER, TearacudaModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.ZAPPET_LAYER, ZappetModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.GILD_GLIDER_LAYER, GildGliderModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.CHUB_LAYER, ChubModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.FEROCETUS_LAYER, FerocetusModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.WAVE_LAYER, WaveModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.BANSHEE_LAYER, EndgelModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.DAWN_DOVE_LAYER, DawnDoveModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.SKIB_LAYER, SkibModel::createBodyLayer);
        event.registerLayerDefinition(EntityLayers.GENERIC_PROJECTILE_LAYER, CrossedProjectileModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderer(FMLClientSetupEvent event)
    {
        EntityRenderers.register(HBEntities.BURODON.get(), BurodonRenderer::new);
        EntityRenderers.register(HBEntities.SPOTTED_STRIKER.get(), SpottedStrikerRenderer::new);
        EntityRenderers.register(HBEntities.PLOMBO.get(), PlomboRenderer::new);
        EntityRenderers.register(HBEntities.GURK.get(), GurkRenderer::new);
        EntityRenderers.register(HBEntities.MURK.get(), MurkRenderer::new);
        EntityRenderers.register(HBEntities.MURK_SMOKE.get(), ModellessProjectileRenderer::new);
        EntityRenderers.register(HBEntities.TEARACUDA.get(), TearacudaRenderer::new);
        EntityRenderers.register(HBEntities.ZAPPET.get(), ZappetRenderer::new);
        EntityRenderers.register(HBEntities.GILD_GLIDER.get(), GildGliderRenderer::new);
        EntityRenderers.register(HBEntities.CHUB.get(), ChubRenderer::new);
        EntityRenderers.register(HBEntities.FEROCETUS.get(), FerocetusRenderer::new);
        EntityRenderers.register(HBEntities.WAVE.get(), WaveRenderer::new);
        EntityRenderers.register(HBEntities.ENDGEL.get(), EndgelRenderer::new);
        EntityRenderers.register(HBEntities.ENDGEL_BULLET.get(), ModellessProjectileRenderer::new);
        EntityRenderers.register(HBEntities.ENDGEL_BLAST.get(), ModellessProjectileRenderer::new);
        EntityRenderers.register(HBEntities.DAWN_DOVE.get(), DawnDoveRenderer::new);
        EntityRenderers.register(HBEntities.DRAGON_FIREBALL.get(), ModellessProjectileRenderer::new);
        EntityRenderers.register(HBEntities.SKIB.get(), SkibRenderer::new);

        // MenuScreens.register();
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

        event.registerSpriteSet(HBParticles.ICE_SHOCKWAVE.get(), AngledParticle.IceShockwaveProvider::new);
        event.registerSpriteSet(HBParticles.ICE_SHOCKWAVE_BIG.get(), AngledParticle.IceShockwaveBigProvider::new);

        event.registerSpriteSet(HBParticles.MURK_CHARGE.get(), AuraParticle.Provider::new);
        event.registerSpriteSet(HBParticles.MURK_CHARGE_SHOOT.get(), ProjectileTrailParticle.MurkChargeShotProvider::new);
        event.registerSpriteSet(HBParticles.MURK_EXPLODE.get(), DynamicExplosionParticle.MurkExplosionProvider::new);
        event.registerSpriteSet(HBParticles.VOLATILE_EXPLODE.get(), DynamicExplosionParticle.VolatileExplosionProvider::new);

        event.registerSpriteSet(HBParticles.MURK_IMPACT.get(), DynamicExplosionParticle.MurkImpactProvider::new);

        event.registerSpriteSet(HBParticles.SMOKE.get(), SmokeParticle.Provider::new);
        event.registerSpriteSet(HBParticles.ENDGEL_SCREAM.get(), EndgelScreamParticle.Provider::new);

        event.registerSpriteSet(HBParticles.ELECTRIC_SPARKS.get(), AuraParticle.Provider::new);
        event.registerSpriteSet(HBParticles.LIGHTNING_EXPLODE.get(), DynamicExplosionParticle.LightningExplodeProvider::new);

        event.registerSpriteSet(HBParticles.FIREBALL.get(), ProjectileTrailParticle.FireBallProvider::new);
        event.registerSpriteSet(HBParticles.FIREBALL_EXPLODE.get(), DynamicExplosionParticle.FireBallExplodeProvider::new);
        event.registerSpriteSet(HBParticles.SLEEP.get(), SleepParticle.Provider::new);

        event.registerSpriteSet(HBParticles.ENDGEL_TRAIL.get(), AuraParticle.Provider::new);
        event.registerSpriteSet(HBParticles.ENDGEL_EXPLODE.get(), DynamicExplosionParticle.EndgelExplodeProvider::new);
        event.registerSpriteSet(HBParticles.ENDGEL_BULLET.get(), ProjectileTrailParticle.MurkChargeShotProvider::new);
        event.registerSpriteSet(HBParticles.ENDGEL_BLAST_EXPLODE.get(), ProjectileTrailParticle.EndgelBlastExplodeProvider::new);

    }

    @SubscribeEvent
    public static void registerItemRenderers(FMLClientSetupEvent event) {
        ItemProperties.register(
                HBItems.ENDGELIC_JUDGEMENT.get(),
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0.0F;

                    return entity.getUseItem() == stack && entity.isUsingItem()
                            ? 1.0F
                            : 0.0F;
                }
        );

        ItemProperties.register(
                HBItems.ENDGELIC_JUDGEMENT.get(),
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0.0F;

                    return entity.getUseItem() == stack
                            ? (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F
                            : 0.0F;
                }
        );
    }


}