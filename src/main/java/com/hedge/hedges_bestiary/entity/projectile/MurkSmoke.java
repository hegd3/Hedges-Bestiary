package com.hedge.hedges_bestiary.entity.projectile;


import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import com.hedge.hedges_bestiary.registry.HBEffects;
import com.hedge.hedges_bestiary.registry.HBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MurkSmoke extends GenericProjectile {

    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(MurkSmoke.class, EntityDataSerializers.BOOLEAN);

    public MurkSmoke(EntityType<? extends MurkSmoke> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (!hit.getEntity().isAlliedTo(this)) {
            boolean hurt = hit.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), this.getDamage());
            if (this.isCharged() && hurt && hit.getEntity() instanceof LivingEntity entity) {
                entity.addEffect(new MobEffectInstance(HBEffects.VOLATILITY.get(), 40, 1));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        discard();
    }

    @Override
    public float getDamage() {
        return this.isCharged() ? 8 : 4;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CHARGED, false);
    }

    @Override
    public float getSpeed() {
        return 0.5f + this.tickCount * 0.05f;
    }

    @Override
    public void createTrail() {
        Vec3 v = getDeltaMovement();
        double length = v.length();
        int c = (int)Math.min(8, Math.round(length) * 3) + 1;
        float f = (float)length / c / 2;
        for (int i = 0; i < c; i++) {
            Vec3 rand = EntityHelpers.getRandomVec3(this.random, 0.02);
            Vec3 p = v.scale(f * i);
            this.level().addParticle(this.isCharged() ? HBParticles.MURK_CHARGE_SHOOT.get() : ParticleTypes.BUBBLE, true, this.getX() + rand.x + p.x,
                    this.getY() + rand.y + p.y, this.getZ() + rand.z + p.z, rand.x, rand.z, rand.y);
        }
    }

    private boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean b) {
        this.entityData.set(CHARGED, b);
    }
}
