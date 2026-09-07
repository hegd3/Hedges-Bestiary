package com.hedge.hedges_bestiary.client.particle;

import com.hedge.hedges_bestiary.registry.HBParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
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

    public static final Deserializer<VolatileExplosionParticleOptions> DESERIALIZER =
            new Deserializer<>() {
                @Override
                public VolatileExplosionParticleOptions fromCommand(
                        ParticleType<VolatileExplosionParticleOptions> type,
                        StringReader reader
                ) throws CommandSyntaxException {
                    reader.expect(' ');
                    float size = reader.readFloat();
                    return new VolatileExplosionParticleOptions(size);
                }

                @Override
                public VolatileExplosionParticleOptions fromNetwork(
                        ParticleType<VolatileExplosionParticleOptions> type,
                        FriendlyByteBuf buf
                ) {
                    return new VolatileExplosionParticleOptions(
                            buf.readFloat()
                    );
                }
            };

    @Override
    public ParticleType<?> getType() {
        return HBParticles.VOLATILE_EXPLODE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.size);

    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size);
    }

    public static VolatileExplosionParticleOptions create(Entity entity) {
        return new VolatileExplosionParticleOptions(1.5F + entity.getBbWidth());
    }
}
