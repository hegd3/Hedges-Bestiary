package com.hedge.hedges_bestiary.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class ATMSwimMoveControl<E extends Mob & AdvancedTurner> extends MoveControl {

    private final E entity;
    private static final float FULL_SPEED_TURN_THRESHOLD = 10.0F;
    private static final float STOP_TURN_THRESHOLD = 60.0F;
    private final int maxTurnX;
    private final float inWaterSpeedModifier;
    private final float outsideWaterSpeedModifier;
    private final float turnSpeed;
    public ATMSwimMoveControl(E pMob, int pMaxTurnX, float pInWaterSpeedModifier, float pOutsideWaterSpeedModifier, float turnSpeed) {
        super(pMob);
        this.turnSpeed = turnSpeed;
        this.entity = pMob;
        this.maxTurnX = pMaxTurnX;
        this.inWaterSpeedModifier = pInWaterSpeedModifier;
        this.outsideWaterSpeedModifier = pOutsideWaterSpeedModifier;
    }

    @Override
    public void tick() {

        if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < (double)2.5000003E-7F) {
                this.mob.setZza(0.0F);
            } else {
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                if (this.entity.getTurnType() != AdvancedTurner.TurnType.LOCK) {
                    this.mob.setYRot(this.entity.getTurnType() == AdvancedTurner.TurnType.INSTANT ? f : this.rotlerp(this.mob.getYRot(), f, turnSpeed));
                    this.mob.yBodyRot = this.mob.getYRot();
                    this.mob.yHeadRot = this.mob.getYRot();
                }
                float f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                if (this.mob.isInWaterOrBubble()) {
                    this.mob.setSpeed(f1 * this.inWaterSpeedModifier);
                    double d4 = Math.sqrt(d0 * d0 + d2 * d2);
                    if ((this.entity.getTurnType() != AdvancedTurner.TurnType.LOCK) && (Math.abs(d1) > (double)1.0E-5F || Math.abs(d4) > (double)1.0E-5F)) {
                        float f3 = -((float)(Mth.atan2(d1, d4) * (double)(180F / (float)Math.PI)));
                        f3 = Mth.clamp(Mth.wrapDegrees(f3), (float)(-this.maxTurnX), (float)this.maxTurnX);
                        this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f3, 5.0F));
                    }

                    float f6 = Mth.cos(this.mob.getXRot() * ((float)Math.PI / 180F));
                    float f4 = Mth.sin(this.mob.getXRot() * ((float)Math.PI / 180F));
                    this.mob.zza = f6 * f1;
                    this.mob.yya = -f4 * f1;

                    double y = Mth.clamp(f1 * d1 * 0.01D, f1 * -0.1D, f1 * 0.1D);
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, y, 0.0D));

                } else {
                    float f5 = Math.abs(Mth.wrapDegrees(this.mob.getYRot() - f));
                    float f2 = getTurningSpeedFactor(f5);
                    this.mob.setSpeed(f1 * this.outsideWaterSpeedModifier * f2);
                }

            }
        } else {
            this.mob.setSpeed(0.0F);
            this.mob.setXxa(0.0F);
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }

    private static float getTurningSpeedFactor(float p_249853_) {
        return 1.0F - Mth.clamp((p_249853_ - 10.0F) / 50.0F, 0.0F, 1.0F);
    }
}
