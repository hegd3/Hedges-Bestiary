package com.hedge.hedges_bestiary.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ProjectileTrailParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    public ProjectileTrailParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites, int lifetime, float quadSize, float alpha) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.xd += ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.zd += ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.roll = ((this.random.nextFloat() - this.random.nextFloat()) * 20F);
        this.friction = 0.96F;
        this.alpha = alpha;
        this.quadSize = quadSize;
        this.lifetime = lifetime + this.random.nextInt(lifetime);
        this.sprites = pSprites;
        this.setSpriteFromAge(pSprites);

    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);

            this.oRoll = this.roll;
            this.roll += (float)Math.PI * 0.01F;
            if (this.onGround) {
                this.oRoll = this.roll = 0.0F;
            }
            this.xd *= this.friction;
            this.zd *= this.friction;


            this.move(this.xd, this.yd, this.zd);
        }
    }

    @Override
    public int getLightColor(float pPartialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }
    @Override
    public float getQuadSize(float pScaleFactor) {
        return this.quadSize * Mth.clamp(((float)this.age + pScaleFactor) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class MurkChargeShotProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public MurkChargeShotProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ProjectileTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, 5, 0.4f, 1.0f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class FireBallProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public FireBallProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ProjectileTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, 6, 1f, 1.0f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EndgelBlastExplodeProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public EndgelBlastExplodeProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ProjectileTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, 6, 2f, 1.0f);
        }
    }

}
