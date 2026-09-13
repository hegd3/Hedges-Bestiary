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
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Level level = mob.level();
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        for (int i = x; i < x + entityWidth; ++i) {
            for (int j = y; j < y + entityHeight; ++j) {
                for (int k = z; k < z + entityDepth; ++k) {
                    FluidState fluidstate = level.getFluidState(blockpos$mutableblockpos.set(i, j, k));
                    BlockState blockstate = level.getBlockState(blockpos$mutableblockpos.set(i, j, k));

                    if (fluidstate.isEmpty() && !blockstate.isAir()) {
                        return PathType.BLOCKED;
                    }
                }
            }
        }
        BlockState blockstate1 = level.getBlockState(blockpos$mutableblockpos);
        return blockstate1.isAir() ? PathType.BREACH : !blockstate1.getFluidState().isEmpty() ? PathType.WATER : PathType.BLOCKED;
    }
}
