package com.hedge.hedges_bestiary.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ControlledMountGoal extends Goal {
    private final PathfinderMob mount;
    private LivingEntity rider;
    private final double speed;
    private final boolean strafe;

    public ControlledMountGoal(PathfinderMob mount, double speed) {
        this(mount, speed, true);
    }

    public ControlledMountGoal(PathfinderMob mount, double speed, boolean strafe) {
        this.mount = mount;
        this.speed = speed;
        this.strafe = strafe;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mount.getControllingPassenger() instanceof Player && this.mount.isVehicle()) {
            this.rider = (Player)this.mount.getControllingPassenger();
            return true;
        } else {
            this.mount.setSprinting(false);
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mount.getControllingPassenger() instanceof Player && this.mount.isVehicle()) {
            return true;
        } else {
            this.mount.setSprinting(false);
            return false;
        }
    }

    @Override
    public void start() {
        this.mount.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.mount.getNavigation().stop();
        this.mount.setTarget(null);
        double x = this.mount.getX();
        double y = this.mount.getY();
        double z = this.mount.getZ();
        if (this.strafe) {
            this.mount.xxa = this.rider.xxa * 0.15F;
        }

        if (this.shouldMoveForward() && this.mount.isVehicle()) {
            this.mount.setSprinting(true);
            Vec3 lookVec = this.rider.getLookAngle();
            if (this.shouldMoveBackwards()) {
                lookVec = lookVec.yRot((float)Math.PI);
            }

            x += lookVec.x * (double)10.0F;
            z += lookVec.z * (double)10.0F;
            y += this.modifyYPosition(lookVec.y);
            this.mount.getMoveControl().setWantedPosition(x, y, z, this.speed);
        } else {
            this.mount.setSprinting(false);
        }

    }

    private double modifyYPosition(double lookVecY) {
        return this.mount instanceof Mob ? lookVecY * (double)10.0F : (double)0.0F;
    }

    private boolean shouldMoveForward() {
        return this.rider.zza != 0.0F;
    }

    private boolean shouldMoveBackwards() {
        return this.rider.zza < 0.0F;
    }
}
