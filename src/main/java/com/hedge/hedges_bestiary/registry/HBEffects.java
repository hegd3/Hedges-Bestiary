package com.hedge.hedges_bestiary.registry;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.potion.VolatiltyEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HBEffects {
    public static final DeferredRegister<MobEffect> DEF_REG = DeferredRegister.create(Registries.MOB_EFFECT, HedgesBestiary.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> VOLATILITY = DEF_REG.register("volatility", VolatiltyEffect::new);

    public static void register(IEventBus bus) {
        DEF_REG.register(bus);
    }
}
