package com.hedge.hedges_bestiary.client;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HBSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HedgesBestiary.MODID);


    public static final RegistryObject<SoundEvent> GURK_AMBIENT = createSoundEvent("gurk_ambient");

    public static final RegistryObject<SoundEvent> GURK_HURT = createSoundEvent("gurk_hurt");

    public static final RegistryObject<SoundEvent> GURK_DIE = createSoundEvent("gurk_die");

    public static final RegistryObject<SoundEvent> BURODON_ROAR = createSoundEvent("burodon_roar");

    public static final RegistryObject<SoundEvent> BURODON_HURT = createSoundEvent("burodon_hurt");

    public static final RegistryObject<SoundEvent> BURODON_DIE = createSoundEvent("burodon_die");

    public static final RegistryObject<SoundEvent> MURK_CLICKS = createSoundEvent("murk_clicks");

    public static final RegistryObject<SoundEvent> MURK_YAWN = createSoundEvent("murk_yawn");

    public static final RegistryObject<SoundEvent> MURK_HURT = createSoundEvent("murk_hurt");

    public static final RegistryObject<SoundEvent> MURK_STOMP = createSoundEvent("murk_stomp");

    public static final RegistryObject<SoundEvent> MURK_ROAR = createSoundEvent("murk_roar");

    public static final RegistryObject<SoundEvent> MURK_DIE = createSoundEvent("murk_die");

    public static final RegistryObject<SoundEvent> FEROCETUS_AMBIENT = createSoundEvent("ferocetus_ambient");

    public static final RegistryObject<SoundEvent> FEROCETUS_HURT = createSoundEvent("ferocetus_hurt");

    public static final RegistryObject<SoundEvent> FEROCETUS_DIE = createSoundEvent("ferocetus_die");

    public static final RegistryObject<SoundEvent> PLOMBO_YAWN = createSoundEvent("plombo_yawn");

    public static final RegistryObject<SoundEvent> PLOMBO_HURT = createSoundEvent("plombo_hurt");

    public static final RegistryObject<SoundEvent> PLOMBO_DIE = createSoundEvent("plombo_die");

    public static final RegistryObject<SoundEvent> ZAP = createSoundEvent("zap");

    public static final RegistryObject<SoundEvent> FIREBALL_SHOOT = createSoundEvent("fireball_shoot");

    public static final RegistryObject<SoundEvent> DAWN_DOVE_AMBIENT = createSoundEvent("dawn_dove_ambient");

    public static final RegistryObject<SoundEvent> DAWN_DOVE_HURT = createSoundEvent("dawn_dove_hurt");

    public static final RegistryObject<SoundEvent> ENDGEL_AMBIENT = createSoundEvent("endgel_ambient");

    public static final RegistryObject<SoundEvent> ENDGEL_SHOOT = createSoundEvent("endgel_shoot");

    public static final RegistryObject<SoundEvent> ENDGEL_EXPLOSION = createSoundEvent("endgel_explosion");


    private static RegistryObject<SoundEvent> createSoundEvent(final String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(HedgesBestiary.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
