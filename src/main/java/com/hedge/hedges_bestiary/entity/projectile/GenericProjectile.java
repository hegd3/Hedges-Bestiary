package com.hedge.hedges_bestiary.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

import java.util.Objects;

public abstract class GenericProjectile extends Projectile {

    Vec3 deltaMovementOld = Vec3.ZERO;

    protected GenericProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (this.getOwner() != null) {
            if (pEntity == this.getOwner()) return true;
            return this.getOwner().isAlliedTo(pEntity);
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy), this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy), this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy)).scale((double)pVelocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)) * 0.45f );
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        this.setOwner(pShooter);
        float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, pVelocity, pInaccuracy);
        Vec3 vec3 = pShooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, pShooter.onGround() ? 0.0D : vec3.y, vec3.z));
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        var owner = getOwner();
        return super.canHitEntity(target) && target != owner && (owner == null || (!owner.isAlliedTo(target)));
    }

    @Override
    public void checkDespawn() {
        if (this.level() instanceof ServerLevel serverLevel && !serverLevel.getChunkSource().chunkMap.getDistanceManager().inEntityTickingRange(this.chunkPosition().toLong())) {
            this.discard();
        }
    }

    @Override
    public void tick() {

        super.tick();
        if (tickCount == 1) {
            deltaMovementOld = getDeltaMovement();
        }
        if (this.level().isClientSide) {
            createTrail();
        } else {
            if (tickCount > this.getLifespan()) {
                this.onMaxAge();
                return;
            }
            handleHitDetection();
        }
        travel();
        deltaMovementOld = getDeltaMovement();
    }


    public void handleHitDetection() {
        HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (result instanceof EntityHitResult entityHitResult) {
            result = new EntityHitResult(entityHitResult.getEntity(), entityHitResult.getEntity().getBoundingBox()
                    .clip(this.position(), this.position().add(this.getDeltaMovement())).orElse(this.position()));
        }
        if (result.getType() != HitResult.Type.MISS && !NeoForge.EVENT_BUS.post(new ProjectileImpactEvent(this, result)).isCanceled()) {
            onHit(result);
        }
    }

    public void travel() {
        setPos(position().add(getDeltaMovement()));
        if (!this.isNoGravity()) {
            Vec3 vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - getDefaultGravity(), vec34.z);
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return super.shouldBeSaved() && !Objects.equals(getRemovalReason(), RemovalReason.UNLOADED_TO_CHUNK);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Age", tickCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tickCount = tag.getInt("Age");
    }

    protected void onMaxAge() {
        this.discard();
    }

    public float getDamage() {
        return 0;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    public int getLifespan() {
        return 300;
    }


    public abstract float getSpeed();

    public abstract void createTrail();

}
