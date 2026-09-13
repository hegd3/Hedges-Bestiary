package com.hedge.hedges_bestiary.registry;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.particle.EndgelScreamParticleOptions;
import com.hedge.hedges_bestiary.client.particle.SmokeParticle;
import com.hedge.hedges_bestiary.client.particle.SmokeParticleOptions;
import com.hedge.hedges_bestiary.client.particle.VolatileExplosionParticleOptions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HBParticles {
    public static final DeferredRegister<ParticleType<?>> DEF_REG = DeferredRegister.create(Registries.PARTICLE_TYPE, HedgesBestiary.MODID);

    public static final Supplier<SimpleParticleType> ICE_SHOCKWAVE = DEF_REG.register("ice_shockwave", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> ICE_SHOCKWAVE_BIG = DEF_REG.register("ice_shockwave_big", ()-> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> MURK_CHARGE = DEF_REG.register("murk_charge", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> MURK_CHARGE_SHOOT = DEF_REG.register("murk_charge_shoot", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> MURK_EXPLODE = DEF_REG.register("murk_explode", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> MURK_IMPACT = DEF_REG.register("murk_impact", ()-> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> ELECTRIC_SPARKS = DEF_REG.register("electric_sparks", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> LIGHTNING_EXPLODE = DEF_REG.register("lightning_explode", ()-> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> FIREBALL = DEF_REG.register("fireball", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> FIREBALL_EXPLODE = DEF_REG.register("fireball_explode", ()-> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> SLEEP = DEF_REG.register("sleep", ()-> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> ENDGEL_TRAIL = DEF_REG.register("endgel_trail", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> ENDGEL_EXPLODE = DEF_REG.register("endgel_explode", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> ENDGEL_BULLET = DEF_REG.register("endgel_bullet", ()-> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> ENDGEL_BLAST_EXPLODE = DEF_REG.register("endgel_blast_explode", ()-> new SimpleParticleType(false));

    public static final Supplier<ParticleType<VolatileExplosionParticleOptions>> VOLATILE_EXPLODE = DEF_REG.register("volatile_explode", ()-> new ParticleType<>(true) {

        @Override
        public MapCodec<VolatileExplosionParticleOptions> codec() {
            return VolatileExplosionParticleOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, VolatileExplosionParticleOptions> streamCodec() {
            return VolatileExplosionParticleOptions.STREAM_CODEC;
        }

    });

    public static final Supplier<ParticleType<SmokeParticleOptions>> SMOKE = DEF_REG.register("smoke", ()-> new ParticleType<>(true) {
        @Override
        public MapCodec<SmokeParticleOptions> codec() {
            return SmokeParticleOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SmokeParticleOptions> streamCodec() {
            return SmokeParticleOptions.STREAM_CODEC;
        }

    });

    public static final Supplier<ParticleType<EndgelScreamParticleOptions>> ENDGEL_SCREAM = DEF_REG.register("endgel_scream", ()-> new ParticleType<>(true) {
        @Override
        public MapCodec<EndgelScreamParticleOptions> codec() {
            return EndgelScreamParticleOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EndgelScreamParticleOptions> streamCodec() {
            return EndgelScreamParticleOptions.STREAM_CODEC;
        }

    });
    public static void register(IEventBus eventbus) {
        DEF_REG.register(eventbus);
    }

}
