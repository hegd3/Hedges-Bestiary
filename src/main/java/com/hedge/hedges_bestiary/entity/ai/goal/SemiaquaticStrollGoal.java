package com.hedge.hedges_bestiary.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class SemiaquaticStrollGoal extends RandomStrollGoal {
    public SemiaquaticStrollGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public boolean canUse() {
        return !this.mob.isInWaterOrBubble() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.isInWaterOrBubble() && super.canContinueToUse();
    }

}
