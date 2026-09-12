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

import java.util.Locale;

public class EndgelScreamParticleOptions implements ParticleOptions {
    private final float xRot;
    private final float yRot;
    private final float quadSize;
    public EndgelScreamParticleOptions(float xRot, float yRot, float quadSize) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.quadSize = quadSize;
    }

    public float getXRot() {
        return this.xRot;
    }
    public float getYRot() {
        return this.yRot;
    }
    public float getQuadSize() {return this.quadSize;}
    public static final Codec<EndgelScreamParticleOptions> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("xRot").forGetter(EndgelScreamParticleOptions::getXRot),
                            Codec.FLOAT.fieldOf("yRot").forGetter(EndgelScreamParticleOptions::getYRot),
                            Codec.FLOAT.fieldOf("quadSize").forGetter(EndgelScreamParticleOptions::getQuadSize)

                            ).apply(instance, EndgelScreamParticleOptions::new)
            );

    public static StreamCodec<? super ByteBuf, EndgelScreamParticleOptions> STREAM_CODEC = StreamCodec.of(
            (buf, option) -> {
                buf.writeFloat(option.xRot);
                buf.writeFloat(option.yRot);
                buf.writeFloat(option.quadSize);
            },
            (buf) -> new EndgelScreamParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat())
    );


    @Override
    public ParticleType<?> getType() {
        return HBParticles.ENDGEL_SCREAM.get();
    }

}
