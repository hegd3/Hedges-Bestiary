package com.hedge.hedges_bestiary.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HBPathNavigatorGround extends GroundPathNavigation {
    private final double maxYDrop;
    public HBPathNavigatorGround(Mob pMob, Level pLevel, double maxYDrop) {
        super(pMob, pLevel);
        this.maxYDrop = maxYDrop;
    }

    public HBPathNavigatorGround(Mob pMob, Level pLevel) {
        this(pMob, pLevel, 1.0D);
    }

    @Override
    protected void followThePath() {
        Path path = Objects.requireNonNull(this.path);
        Vec3 entityPos = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() * 0.75F;
        BlockPos next = path.getNextNodePos();
        double x = clampCoord(this.mob.getX(), next.getX()),
                y = Math.abs(this.mob.getY() - next.getY()),
                z = clampCoord(this.mob.getZ(), next.getZ());
        boolean validDist = x < maxDistanceToWaypoint && z < maxDistanceToWaypoint && y < this.maxYDrop;
        if (validDist || this.mob.getPathfindingMalus(this.path.getNextNode().type) >= 0.0F && this.validNextNode(entityPos)) {
            this.path.advance();
        }
        this.doStuckDetection(entityPos);
    }

    private double clampCoord(double coord, double nextCoord) {
        return Math.abs(coord - nextCoord - 0.5);
    }

    private boolean validNextNode(@NotNull Vec3 pos) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        }
        Vec3 next = Vec3.atBottomCenterOf(this.path.getNextNodePos());
        if (!pos.closerThan(next, this.maxDistanceToWaypoint * 1.5)) {
            return false;
        } else {
            Vec3 next2 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
            return next2.subtract(next).dot(pos.subtract(next)) > 0.0;
        }
    }


}
