package com.hedge.hedges_bestiary.registry;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.particle.EndgelScreamParticleOptions;
import com.hedge.hedges_bestiary.client.particle.SmokeParticleOptions;
import com.hedge.hedges_bestiary.client.particle.VolatileExplosionParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HBParticles {
    public static final DeferredRegister<ParticleType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HedgesBestiary.MODID);

    public static final RegistryObject<SimpleParticleType> ICE_SHOCKWAVE = DEF_REG.register("ice_shockwave", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ICE_SHOCKWAVE_BIG = DEF_REG.register("ice_shockwave_big", ()-> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> MURK_CHARGE = DEF_REG.register("murk_charge", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MURK_CHARGE_SHOOT = DEF_REG.register("murk_charge_shoot", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MURK_EXPLODE = DEF_REG.register("murk_explode", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MURK_IMPACT = DEF_REG.register("murk_impact", ()-> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ELECTRIC_SPARKS = DEF_REG.register("electric_sparks", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LIGHTNING_EXPLODE = DEF_REG.register("lightning_explode", ()-> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FIREBALL = DEF_REG.register("fireball", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FIREBALL_EXPLODE = DEF_REG.register("fireball_explode", ()-> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SLEEP = DEF_REG.register("sleep", ()-> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ENDGEL_TRAIL = DEF_REG.register("endgel_trail", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ENDGEL_EXPLODE = DEF_REG.register("endgel_explode", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ENDGEL_BULLET = DEF_REG.register("endgel_bullet", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ENDGEL_BLAST_EXPLODE = DEF_REG.register("endgel_blast_explode", ()-> new SimpleParticleType(false));

    public static final RegistryObject<ParticleType<VolatileExplosionParticleOptions>> VOLATILE_EXPLODE = DEF_REG.register("volatile_explode", ()-> new ParticleType<>(true, VolatileExplosionParticleOptions.DESERIALIZER) {
        @Override
        public Codec<VolatileExplosionParticleOptions> codec() {
            return VolatileExplosionParticleOptions.CODEC;
        }

    });

    public static final RegistryObject<ParticleType<SmokeParticleOptions>> SMOKE = DEF_REG.register("smoke", ()-> new ParticleType<>(true, SmokeParticleOptions.DESERIALIZER) {
        @Override
        public Codec<SmokeParticleOptions> codec() {
            return SmokeParticleOptions.CODEC;
        }

    });

    public static final RegistryObject<ParticleType<EndgelScreamParticleOptions>> ENDGEL_SCREAM = DEF_REG.register("endgel_scream", ()-> new ParticleType<>(true, EndgelScreamParticleOptions.DESERIALIZER) {
        @Override
        public Codec<EndgelScreamParticleOptions> codec() {
            return EndgelScreamParticleOptions.CODEC;
        }

    });
    public static void register(IEventBus eventbus) {
        DEF_REG.register(eventbus);
    }

}
