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
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

import java.util.Locale;

public class VolatileExplosionParticleOptions implements ParticleOptions {
    private final float size;

    public VolatileExplosionParticleOptions(float size) {
        this.size = size;
    }

    public float getSize() {
        return this.size;
    }

    public static final Codec<VolatileExplosionParticleOptions> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("size").forGetter(VolatileExplosionParticleOptions::getSize)
                    ).apply(instance, VolatileExplosionParticleOptions::new)
            );

    public static StreamCodec<? super ByteBuf, VolatileExplosionParticleOptions> STREAM_CODEC = StreamCodec.of(
            (buf, option) -> {
                buf.writeFloat(option.size);
            },
            (buf) -> new VolatileExplosionParticleOptions(buf.readFloat())
    );
    @Override
    public ParticleType<?> getType() {
        return HBParticles.VOLATILE_EXPLODE.get();
    }



    public static VolatileExplosionParticleOptions create(Entity entity) {
        return new VolatileExplosionParticleOptions(1.5F + entity.getBbWidth());
    }
}
