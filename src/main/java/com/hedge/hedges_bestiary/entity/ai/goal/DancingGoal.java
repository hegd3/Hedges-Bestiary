package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DancingGoal extends Goal {
    private final HBTamableAnimal mob;
    private final boolean stopsDancingInWater;
    public DancingGoal(HBTamableAnimal mob) {
        this(mob, true);
    }

    public DancingGoal(HBTamableAnimal mob, boolean stopsDancingInWater) {
        this.mob = mob;
        this.stopsDancingInWater = stopsDancingInWater;
    }


    @Override
    public boolean canUse() {
        return this.mob.isDancing();
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.mob.isSitting() || this.mob.getTarget() != null || this.mob.hasControllingPassenger()) {
            return false;
        }
        if (this.stopsDancingInWater) {
            if (this.mob.isInFluidType()) {
                return false;
            }
        } else {
            if (!this.mob.isInWater()) {
                return false;
            }
        }
        if (this.mob.tickCount % 10 == 0) {
            BlockPos jukebox = this.mob.getJukebox();
            if (jukebox == null) {
                return false;
            }
            BlockState state = this.mob.level().getBlockState(jukebox);
            if (jukebox == null || !state.is(Blocks.JUKEBOX)) {
                return false;
            } else if (!state.getValue(JukeboxBlock.HAS_RECORD) || !jukebox.closerToCenterThan(this.mob.position(), 8D)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void stop() {
        this.mob.setJukeboxPos(null);
        if (this.mob.isDancing()) {
            this.mob.setDancing(false);
        }
    }
}
