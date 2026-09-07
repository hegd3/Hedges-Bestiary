package com.hedge.hedges_bestiary.potion;

import com.hedge.hedges_bestiary.client.particle.VolatileExplosionParticleOptions;
import com.hedge.hedges_bestiary.registry.HBParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class VolatiltyEffect extends MobEffect {
    public VolatiltyEffect() {
        super(MobEffectCategory.HARMFUL, 0xF01F1F);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int pAmplifier) {
        if (!living.level().isClientSide && living.level() instanceof ServerLevel level) {
            level.sendParticles(VolatileExplosionParticleOptions.create(living), living.getX(), living.getY(0.5D), living.getZ(), 0, 0, 0.0D, 0, 0.0D);
        }
        living.hurt(living.damageSources().explosion(null, null), 7 + pAmplifier * 3);

    }

    @Override
    public boolean isDurationEffectTick(int duration, int pAmplifier) {
        return duration == 1;
    }
}
