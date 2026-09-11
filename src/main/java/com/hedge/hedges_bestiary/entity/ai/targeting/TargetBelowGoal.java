package com.hedge.hedges_bestiary.entity.ai.targeting;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class TargetBelowGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public TargetBelowGoal(Mob pMob, Class<T> target, Predicate<LivingEntity> predicate) {
        super(pMob, target, 10, true, true, predicate);
    }

    @Override
    protected AABB getTargetSearchArea(double pTargetDistance) {
        AABB aabb = this.mob.getBoundingBox();
        double newDistance = 2.0F;
        return new AABB(aabb.minX - newDistance, mob.level().getMinBuildHeight() - 5, aabb.minZ - newDistance, aabb.maxX + newDistance, aabb.maxY + 1, aabb.maxZ + newDistance);
    }
}
