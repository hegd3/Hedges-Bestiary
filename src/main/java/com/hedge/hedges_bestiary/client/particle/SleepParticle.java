package com.hedge.hedges_bestiary.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SleepParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public SleepParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.xd += ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.zd += ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);

        this.friction = 0.96F;
        this.quadSize = 0.5F;
        this.lifetime = 40 + this.random.nextInt(4);
        this.sprites = pSprites;
        this.setSpriteFromAge(pSprites);

    }




    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }



    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.alpha <= 0.0f || this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);

            this.oRoll = this.roll;
            this.roll += (float)Math.PI * 0.001F;
            if (this.onGround) {
                this.oRoll = this.roll = 0.0F;
            }


            this.move(this.xd, this.yd, this.zd);
            this.yd += 0.006;

            if (this.alpha > 0.00F) {
                this.quadSize-=0.005f;
                this.alpha -= 0.05F;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new SleepParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }

    }
}
