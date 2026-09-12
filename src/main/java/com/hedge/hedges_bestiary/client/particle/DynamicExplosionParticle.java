package com.hedge.hedges_bestiary.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DynamicExplosionParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public DynamicExplosionParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites, int lifetime, float size) {
        super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
        this.sprites = pSprites;
        this.lifetime = lifetime;
        this.quadSize = size;
        this.setSpriteFromAge(sprites);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    public int getLightColor(float pPartialTick) {
        return 15728880;
    }


    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class MurkExplosionProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public MurkExplosionProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new DynamicExplosionParticle(pLevel, pX, pY, pZ, this.sprites, 14, 6);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MurkImpactProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public MurkImpactProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new DynamicExplosionParticle(pLevel, pX, pY, pZ, this.sprites, 7, 2.7f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class VolatileExplosionProvider implements ParticleProvider<VolatileExplosionParticleOptions> {
        private final SpriteSet sprites;

        public VolatileExplosionProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }
        public Particle createParticle(VolatileExplosionParticleOptions options, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            DynamicExplosionParticle particle = new DynamicExplosionParticle(pLevel, pX, pY, pZ, this.sprites, 9, options.getSize());
            particle.pickSprite(this.sprites);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LightningExplodeProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public LightningExplodeProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new DynamicExplosionParticle(pLevel, pX, pY, pZ, this.sprites, 8, 1.0f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EndgelExplodeProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public EndgelExplodeProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new DynamicExplosionParticle(pLevel, pX, pY, pZ, this.sprites, 9, 3.3f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class FireBallExplodeProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public FireBallExplodeProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new DynamicExplosionParticle(pLevel, pX, pY, pZ, this.sprites, 11, 6f);
        }
    }
}
