package com.hedge.hedges_bestiary.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

public class FluidsNodeNavigator extends SwimNodeEvaluator {
    public FluidsNodeNavigator(boolean pAllowBreaching) {
        super(pAllowBreaching);
    }




    @Override
    public PathType getPathType(Mob mob, BlockPos pos) {
        Level level = mob.level();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        boolean hasWater = false;

        int minX = pos.getX();
        int minY = pos.getY();
        int minZ = pos.getZ();

        for (int x = minX; x < minX + entityWidth; x++) {
            for (int y = minY; y < minY + entityHeight; y++) {
                for (int z = minZ; z < minZ + entityDepth; z++) {
                    cursor.set(x, y, z);

                    FluidState fluid = level.getFluidState(cursor);
                    BlockState block = level.getBlockState(cursor);

                    if (fluid.isEmpty() && !block.isAir()) {
                        return PathType.BLOCKED;
                    }

                    if (!fluid.isEmpty()) {
                        hasWater = true;
                    }
                }
            }
        }

        if (hasWater) {
            return PathType.WATER;
        }

        BlockPos below = pos.below();
        return level.getFluidState(below).isEmpty()
                ? PathType.BREACH
                : PathType.WATER;
    }
}
