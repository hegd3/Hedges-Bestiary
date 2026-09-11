package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.blocks.EggBlockEntity;
import com.hedge.hedges_bestiary.blocks.MultiEggBlock;
import com.hedge.hedges_bestiary.entity.types.EggLayer;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.types.TamableFlyer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class LayEggsGoal<E extends HBTamableAnimal & EggLayer> extends MoveToBlockGoal {

    private final E eggLayer;
    private final int maxTime;
    private int layEggTimer;

    public LayEggsGoal(E mob, int maxTime, double speed) {
        super(mob, speed, 16);
        this.eggLayer = mob;
        this.maxTime = maxTime;
    }

    @Override
    public void start() {
        super.start();
        this.layEggTimer = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isReachedTarget()) {
            if (this.mob instanceof TamableFlyer flyer && flyer.isFlying()) {
                flyer.setFlying(false);
            }
            this.onReachedTarget();
        }
    }

    protected void onReachedTarget() {
        if (this.layEggTimer++ > this.maxTime) {
            Level level = this.mob.level();
            level.playSound(null, blockPos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
            BlockPos blockpos1 = this.blockPos.above();
            BlockState egg = eggLayer.laysMultipleEggs() ?
                    eggLayer.getEgg().setValue(MultiEggBlock.EGGS, this.mob.getRandom().nextInt(4) + 1) :
                    eggLayer.getEgg();
            level.setBlockAndUpdate(blockpos1, egg);
            level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.mob, egg));
            if (this.eggLayer.getOwnerUUID() != null && level.getBlockEntity(blockpos1) instanceof EggBlockEntity<?> eggBlockEntity) {
                eggBlockEntity.setOwnerUUID(eggLayer.getOwnerUUID().toString());
            }
            this.eggLayer.setHasEgg(false);
            this.eggLayer.setInLoveTime(600);
        } else if (this.layEggTimer % 10 == 0) {
            this.mob.playSound(mob.level().getBlockState(blockPos).getSoundType().getBreakSound());
        }
    }

    @Override
    public boolean canUse() {
        return this.eggLayer.hasEgg() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.eggLayer.hasEgg() && super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.isEmptyBlock(pos.above()) && level.getBlockState(pos).isSolid();
    }

    @Override
    public double acceptedDistance() {
        return Math.ceil(this.mob.getBbWidth()) + 0.5D;
    }

}
