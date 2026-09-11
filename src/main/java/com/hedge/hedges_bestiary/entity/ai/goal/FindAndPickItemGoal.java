package com.hedge.hedges_bestiary.entity.ai.goal;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindAndPickItemGoal extends Goal {
    private final PathfinderMob mob;
    private final Predicate<ItemEntity> item;
    private int attemptTicks;
    public FindAndPickItemGoal(PathfinderMob mob, Predicate<ItemEntity> item) {
        this.mob = mob;
        this.item = item;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!this.mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
        }
        if (this.mob.getRandom().nextInt(reducedTickDelay(10)) != 0) {
            return false;
        } else {
            List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), item);
            return !list.isEmpty() && this.mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.attemptTicks < 200;
    }

    public void tick() {
        this.attemptTicks++;
        List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), item);
        ItemStack itemstack = this.mob.getItemBySlot(EquipmentSlot.MAINHAND);
        if (list.isEmpty()) {
            this.attemptTicks = 200;
            return;
        }
        if (this.mob.distanceTo(list.get(0)) <= this.mob.getBbWidth()) {
            this.mob.setItemSlot(EquipmentSlot.MAINHAND, list.get(0).getItem());
            list.get(0).discard();

        }
        else if (this.mob.getNavigation().isDone() && itemstack.isEmpty()) {
            this.mob.getNavigation().moveTo(list.get(0),1.2F);
        }

    }

    public void start() {
        this.attemptTicks = 0;
        List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), item);
        if (!list.isEmpty()) {
            this.mob.getNavigation().moveTo(list.get(0),1.2F);
        }

    }
}
