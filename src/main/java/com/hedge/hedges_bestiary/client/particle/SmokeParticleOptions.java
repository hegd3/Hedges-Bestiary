package com.hedge.hedges_bestiary.client.particle;

import com.hedge.hedges_bestiary.registry.HBParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import org.joml.Vector3f;

import java.util.Locale;

public class SmokeParticleOptions implements ParticleOptions {
    private final Vector3f color;
    private final float size;
    private final int lifetime;


    public SmokeParticleOptions(float size, int lifetime, int color) {

        this.size = size;
        this.lifetime = lifetime;

        float r = Math.min(FastColor.ARGB32.red(color) / 255F, 1);
        float g = Math.min(FastColor.ARGB32.green(color) / 255F, 1);
        float b = Math.min(FastColor.ARGB32.blue(color) / 255F, 1);

        this.color = new Vector3f(r, g, b);

    }

    public SmokeParticleOptions(float size, int lifetime, float r, float g, float b) {
        this.size = size;
        this.lifetime = lifetime;

        this.color = new Vector3f(r, g, b);

    }

    public Vector3f getColor() {
        return this.color;
    }


    public int getLifetime() {
        return this.lifetime;
    }

    public float getSize() {
        return this.size;
    }

    public static final Codec<SmokeParticleOptions> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("size").forGetter(SmokeParticleOptions::getSize),
                            Codec.INT.fieldOf("lifetime").forGetter(SmokeParticleOptions::getLifetime),

                            Codec.FLOAT.fieldOf("r").forGetter(o -> o.getColor().x),
                            Codec.FLOAT.fieldOf("g").forGetter(o -> o.getColor().y),
                            Codec.FLOAT.fieldOf("b").forGetter(o -> o.getColor().z)
                    ).apply(instance, SmokeParticleOptions::new)
            );

    public static StreamCodec<? super ByteBuf, SmokeParticleOptions> STREAM_CODEC = StreamCodec.of(
            (buf, option) -> {
                buf.writeFloat(option.size);
                buf.writeInt(option.lifetime);
                buf.writeFloat(option.color.x);
                buf.writeFloat(option.color.y);
                buf.writeFloat(option.color.z);

            },
            (buf) -> new SmokeParticleOptions(buf.readFloat(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat())
    );


    @Override
    public ParticleType<?> getType() {
        return HBParticles.SMOKE.get();
    }

}
