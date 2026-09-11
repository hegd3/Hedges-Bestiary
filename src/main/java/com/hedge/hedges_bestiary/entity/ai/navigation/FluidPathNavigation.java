package com.hedge.hedges_bestiary.entity.ai.navigation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;

public class FluidPathNavigation extends WaterBoundPathNavigation {
    public FluidPathNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }

    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        super.createPathFinder(pMaxVisitedNodes);
        this.nodeEvaluator = new FluidsNodeNavigator(true);
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }
}
