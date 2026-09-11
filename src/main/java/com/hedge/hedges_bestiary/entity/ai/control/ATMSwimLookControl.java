package com.hedge.hedges_bestiary.entity.ai.control;

import net.minecraft.world.entity.Mob;

public class ATMSwimLookControl<E extends Mob & AdvancedTurner> extends SemiaquaticLookControl {
    private final E entity;
    private final float turnSpeed;

    public ATMSwimLookControl(E pMob, int pMaxYRotFromCenterWater, float turnSpeed) {
        super(pMob, pMaxYRotFromCenterWater);
        this.turnSpeed = turnSpeed;
        this.entity = pMob;
    }

    public void tick() {
        if (entity.getTurnType() != AdvancedTurner.TurnType.LOCK) {
            this.yMaxRotSpeed = this.turnSpeed;
            if (entity.getTurnType() == AdvancedTurner.TurnType.INSTANT) {
                float targetYRot = this.getYRotD().orElse(this.mob.getYRot());
                float targetXRot = this.getXRotD().orElse(this.mob.getXRot());

                this.mob.setYRot(targetYRot);
                this.mob.yBodyRot = targetYRot;
                this.mob.yHeadRot = targetYRot;
                this.mob.setXRot(targetXRot);

            /*
            this.mob.yBodyRotO = targetYRot;
            this.mob.yHeadRotO = targetYRot;
            this.mob.xRotO = targetXRot;

             */
            } else if (entity.getTurnType() == AdvancedTurner.TurnType.WHOLE_BODY) {
                float targetYRot = this.getYRotD().orElse(this.mob.getYRot());
                float targetXRot = this.getXRotD().orElse(this.mob.getXRot());

                float yRot = this.rotateTowards(this.mob.getYRot(), targetYRot, this.yMaxRotSpeed);
                float bodyYRot = this.rotateTowards(this.mob.yBodyRot, targetYRot, this.yMaxRotSpeed);
                float headYRot = this.rotateTowards(this.mob.yHeadRot, targetYRot, this.yMaxRotSpeed);
                float xRot = this.rotateTowards(this.mob.getXRot(), targetXRot, this.xMaxRotAngle);

                this.mob.setYRot(yRot);
                this.mob.yBodyRot = bodyYRot;
                this.mob.yHeadRot = headYRot;
                this.mob.setXRot(xRot);

            /*
            this.mob.yRotO = yRot;
            this.mob.yBodyRotO = yRot;
            this.mob.yHeadRotO = headYRot;
            this.mob.xRotO = xRot;

             */

            } else {
                super.tick();
            }
        }
    }
}
