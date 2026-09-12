package com.hedge.hedges_bestiary.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class AngledParticle extends TextureSheetParticle {
    private final float xRot;
    private final float yRot;
    protected final SpriteSet sprites;

    public AngledParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites, int lifetime, float quadSize, float alpha, float xRot, float yRot) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.xRot = xRot;
        this.yRot = yRot;

        this.friction = 0.96F;
        this.alpha = alpha;
        this.quadSize = quadSize;
        this.lifetime = lifetime + this.random.nextInt(lifetime);
        this.sprites = pSprites;
        this.setSpriteFromAge(pSprites);

    }

    @Override
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

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.rotateY(-(float) Math.toRadians(yRot)).rotateX(-(float) Math.toRadians(xRot));
        });
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.rotateY(-(float) Math.PI - (float) Math.toRadians(yRot)).rotateX((float) Math.toRadians(xRot));
        });
    }

    private void renderSignal(VertexConsumer consumer, Camera camera, float partialTicks, Consumer<Quaternionf> rots) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        Vector3f vector3f = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0.0F, vector3f.x(), vector3f.y(), vector3f.z());
        rots.accept(quaternionf);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f1 = avector3f[i];
            vector3f1.rotate(quaternionf);
            vector3f1.mul(f3);
            vector3f1.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);
        consumer.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).setUv(f7, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        consumer.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).setUv(f7, f4).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        consumer.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).setUv(f6, f4).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        consumer.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).setUv(f6, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
    }

    @Override
    public int getLightColor(float pPartialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }


    @OnlyIn(Dist.CLIENT)
    public static class IceShockwaveProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public IceShockwaveProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new AngledParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, 7, 3f, 1.0f, -90, 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class IceShockwaveBigProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public IceShockwaveBigProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new AngledParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, 10, 6f, 1.0f, -90, 0);
        }
    }
}
