package com.hedge.hedges_bestiary.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AuraParticle extends TextureSheetParticle {

    protected final SpriteSet sprites;

    protected AuraParticle(ClientLevel pLevel, double pX, double pY, double pZ, int lifetime, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.xd += ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.zd += ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);

        this.friction = 0.96F;
        this.quadSize *= 1.6F;
        this.lifetime = lifetime + this.random.nextInt(4);
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
            this.roll += (float)Math.PI * 0.001F;
            if (this.onGround) {
                this.oRoll = this.roll = 0.0F;
            }


            this.move(this.xd, this.yd, this.zd);
            this.yd += 0.006;
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
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new AuraParticle(pLevel, pX, pY, pZ, 7, this.sprites);
        }
    }
}
