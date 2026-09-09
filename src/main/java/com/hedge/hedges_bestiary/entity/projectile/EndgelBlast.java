package com.hedge.hedges_bestiary.entity.projectile;

import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.client.particle.EndgelScreamParticleOptions;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EndgelBlast extends HomingProjectile {

    public EndgelBlast(EntityType<? extends EndgelBlast> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    private void explode() {
        if (!this.level().isClientSide) {
            List<LivingEntity> hit = AttackHelpers.projectileZoneHitbox(this, this.position(), 7, 5, 7, 20);
            for (LivingEntity entity : hit) {
                if (!AttackHelpers.blockBreak(entity)) {
                    entity.hurt(this.damageSources().explosion(this.getOwner(), this), this.getDamage() - (float) entity.distanceToSqr(this.position()));
                }
            }
            this.playSound(HBSounds.ENDGEL_EXPLOSION.get(), 1.4F, this.random.nextFloat() * 0.5F + 0.1F);
            this.level().broadcastEntityEvent(this, (byte) 39);
        }
    }

    @Override
    protected float getLerpSpeed() {
        return 0.04F;
    }

    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (!hit.getEntity().isAlliedTo(this)) {
            hit.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), this.getDamage());
            this.explode();
            this.discard();

        }

    }


    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.explode();
        this.discard();
    }

    @Override
    protected void onMaxAge() {
        this.explode();
        super.onMaxAge();
    }

    @Override
    public float getDamage() {
        return 40;
    }


    @Override
    public float getSpeed() {
        return 2f + this.tickCount / 10F;
    }

    @Override
    public int getLifespan() {
        return 40;
    }

    @Override
    public void createTrail() {
        if (this.tickCount % 2 == 0) {
            Vec3 v = this.getDeltaMovement();
            this.level().addParticle(HBParticles.ENDGEL_BLAST_EXPLODE.get(), true, this.getX(),
                    this.getY(), this.getZ(), v.x, v.y, v.z);
        }

    }

    @Override
    protected void defineSynchedData() {
    }





    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 39) {
            this.level().addParticle(new EndgelScreamParticleOptions(-90, 0, 7F), true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            for (int i = 0; i < 360; i+=60) {
                Vec3 v = new Vec3(Mth.cos(i * Mth.DEG_TO_RAD) * 5, 0, Mth.sin(i * Mth.DEG_TO_RAD) * 5);
                Vec3 pos = this.position().add(v);
                this.level().addParticle(HBParticles.ENDGEL_BLAST_EXPLODE.get(), true, pos.x, pos.y, pos.z, v.x * 5, 0, v.z * 5);
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }
}
