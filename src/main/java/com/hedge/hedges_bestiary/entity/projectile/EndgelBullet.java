package com.hedge.hedges_bestiary.entity.projectile;

import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.util.AttackHelpers;
import com.hedge.hedges_bestiary.registry.HBParticles;
import com.hedge.hedges_bestiary.util.WorldHelpers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EndgelBullet extends HomingProjectile {


    public EndgelBullet(EntityType<? extends EndgelBullet> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {

    }

    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (hit.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), this.getDamage()) && this.getOwner() != null) {
            ((LivingEntity) this.getOwner()).setLastHurtMob(hit.getEntity());
        }
        this.explode();
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.explode();
        this.discard();
    }

    private void explode() {
        if (!this.level().isClientSide) {
            List<LivingEntity> hit = AttackHelpers.projectileZoneHitbox(this, this.position(), 2, 2, 2, 10);
            for (LivingEntity entity : hit) {
                entity.hurt(this.damageSources().explosion(this.getOwner(), this), this.getDamage() - (float) entity.distanceToSqr(this.position()));
            }
            this.playSound(HBSounds.ENDGEL_EXPLOSION.get(), 1.4F, this.random.nextFloat() * 0.5F + 0.4F);
            this.level().broadcastEntityEvent(this, (byte) 39);
        }
    }

    @Override
    protected void onMaxAge() {
        this.explode();
        super.onMaxAge();
    }

    @Override
    public float getDamage() {
        return 10;
    }

    @Override
    public float getSpeed() {
        return 1f;
    }

    @Override
    public void createTrail() {
        this.level().addParticle(HBParticles.ENDGEL_BULLET.get(), true, this.getX(),
                this.getY(), this.getZ(), 0, 0, 0);


        Vec3 v = getDeltaMovement();
        double length = v.length();
        int c = (int)Math.min(5, Math.round(length)) + 1;
        float f = (float)length / c / 1.5F;
        for (int i = 0; i < c; i++) {
            Vec3 p = v.scale(f * i);
            this.level().addParticle(HBParticles.ENDGEL_BULLET.get(), true, this.getX() + p.x,
                    this.getY() + p.y, this.getZ() + p.z, 0, 0, 0);
        }


    }

    @Override
    public int getLifespan() {
        return 30;
    }



    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 39) {
            this.level().addParticle(HBParticles.ENDGEL_EXPLODE.get(), true, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        } else {
            super.handleEntityEvent(pId);
        }
    }
}
