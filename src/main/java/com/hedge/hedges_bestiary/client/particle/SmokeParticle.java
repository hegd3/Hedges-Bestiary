package com.hedge.hedges_bestiary.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SmokeParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public SmokeParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites, float size, int lifeTime, float r, float g, float b) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = pSprites;
        this.quadSize = size;
        this.lifetime = this.random.nextInt(50) + lifeTime;
        this.gravity = 3.0E-6F;
        this.xd = pXSpeed;
        this.yd = pYSpeed + (double)(this.random.nextFloat() / 500.0F);
        this.zd = pZSpeed;
        this.setColor(r, g, b);
        this.setSpriteFromAge(pSprites);

    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.setSpriteFromAge(sprites);
            this.xd += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.zd += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }

        } else {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SmokeParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SmokeParticleOptions options, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            SmokeParticle p = new SmokeParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites,
                    options.getSize(), options.getLifetime(), options.getColor().x, options.getColor().y, options.getColor().z);
            p.setAlpha(0.95f);
            p.pickSprite(this.sprites);
            return p;
        }

    }



}
