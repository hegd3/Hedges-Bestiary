package com.hedge.hedges_bestiary.client;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HBSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, HedgesBestiary.MODID);


    public static final DeferredHolder<SoundEvent, SoundEvent> GURK_AMBIENT = createSoundEvent("gurk_ambient");

    public static final DeferredHolder<SoundEvent, SoundEvent> GURK_HURT = createSoundEvent("gurk_hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> GURK_DIE = createSoundEvent("gurk_die");

    public static final DeferredHolder<SoundEvent, SoundEvent> BURODON_ROAR = createSoundEvent("burodon_roar");

    public static final DeferredHolder<SoundEvent, SoundEvent> BURODON_HURT = createSoundEvent("burodon_hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> BURODON_DIE = createSoundEvent("burodon_die");

    public static final DeferredHolder<SoundEvent, SoundEvent> MURK_CLICKS = createSoundEvent("murk_clicks");

    public static final DeferredHolder<SoundEvent, SoundEvent> MURK_YAWN = createSoundEvent("murk_yawn");

    public static final DeferredHolder<SoundEvent, SoundEvent> MURK_HURT = createSoundEvent("murk_hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> MURK_STOMP = createSoundEvent("murk_stomp");

    public static final DeferredHolder<SoundEvent, SoundEvent> MURK_ROAR = createSoundEvent("murk_roar");

    public static final DeferredHolder<SoundEvent, SoundEvent> MURK_DIE = createSoundEvent("murk_die");

    public static final DeferredHolder<SoundEvent, SoundEvent> FEROCETUS_AMBIENT = createSoundEvent("ferocetus_ambient");

    public static final DeferredHolder<SoundEvent, SoundEvent> FEROCETUS_HURT = createSoundEvent("ferocetus_hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> FEROCETUS_DIE = createSoundEvent("ferocetus_die");

    public static final DeferredHolder<SoundEvent, SoundEvent> PLOMBO_YAWN = createSoundEvent("plombo_yawn");

    public static final DeferredHolder<SoundEvent, SoundEvent> PLOMBO_HURT = createSoundEvent("plombo_hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> PLOMBO_DIE = createSoundEvent("plombo_die");

    public static final DeferredHolder<SoundEvent, SoundEvent> ZAP = createSoundEvent("zap");

    public static final DeferredHolder<SoundEvent, SoundEvent> FIREBALL_SHOOT = createSoundEvent("fireball_shoot");

    public static final DeferredHolder<SoundEvent, SoundEvent> DAWN_DOVE_AMBIENT = createSoundEvent("dawn_dove_ambient");

    public static final DeferredHolder<SoundEvent, SoundEvent> DAWN_DOVE_HURT = createSoundEvent("dawn_dove_hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> ENDGEL_AMBIENT = createSoundEvent("endgel_ambient");

    public static final DeferredHolder<SoundEvent, SoundEvent> ENDGEL_SHOOT = createSoundEvent("endgel_shoot");

    public static final DeferredHolder<SoundEvent, SoundEvent> ENDGEL_EXPLOSION = createSoundEvent("endgel_explosion");


    private static DeferredHolder<SoundEvent, SoundEvent> createSoundEvent(final String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
