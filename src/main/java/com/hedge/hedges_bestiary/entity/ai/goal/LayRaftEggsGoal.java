package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.blocks.EggBlockEntity;
import com.hedge.hedges_bestiary.entity.types.EggLayer;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LayRaftEggsGoal<E extends HBTamableAnimal & EggLayer> extends Goal {
    private final E eggLayer;
    private BlockPos pos;

    public LayRaftEggsGoal(E animal) {
        this.eggLayer = animal;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.eggLayer.hasEgg() && this.eggLayer.getRandom().nextInt(10) == 0) {
            BlockPos potential = this.getEggLayPos();
            if (potential != null) {
                this.pos = potential;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return pos != null && this.eggLayer.hasEgg() && this.eggLayer.level().getBlockState(pos).isAir();
    }

    public BlockPos getEggLayPos() {
        for (int i = 0; i < 10; i++) {

            BlockPos offset = this.eggLayer.blockPosition().offset(this.eggLayer.getRandom().nextInt(10) - 5, 10, this.eggLayer.getRandom().nextInt(10) - 5);
            while (this.eggLayer.level().getBlockState(offset.below()).isAir() && offset.getY() > this.eggLayer.level().getMinBuildHeight()) {
                offset = offset.below();
            }
            if (isValidPos(offset)) {
                return offset;
            }
        }
        return null;
    }

    public boolean isValidPos(BlockPos pos) {
        return this.eggLayer.level().getFluidState(pos.below()).is(FluidTags.WATER) && this.eggLayer.level().getBlockState(pos).isAir();
    }

    @Override
    public void stop() {
        this.pos = null;
    }

    @Override
    public void start() {
        this.eggLayer.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1F);
    }

    @Override
    public void tick() {
        if (this.eggLayer.getNavigation().isDone() || this.eggLayer.getNavigation().isStuck() || this.eggLayer.distanceToSqr(Vec3.atBottomCenterOf(pos)) < 2.0F) {
            Level level = this.eggLayer.level();
            this.eggLayer.getNavigation().stop();
            BlockState egg = this.eggLayer.getEgg();
            level.setBlockAndUpdate(pos, egg);
            level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(this.eggLayer, egg));
            if (this.eggLayer.getOwnerUUID() != null && level.getBlockEntity(pos) instanceof EggBlockEntity<?> eggBlockEntity) {
                eggBlockEntity.setOwnerUUID(eggLayer.getOwnerUUID().toString());
            }
            this.eggLayer.setHasEgg(false);
            this.eggLayer.setInLoveTime(600);
        }
    }
}
