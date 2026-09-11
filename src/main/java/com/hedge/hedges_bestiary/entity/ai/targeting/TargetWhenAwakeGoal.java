package com.hedge.hedges_bestiary.entity.ai.targeting;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.types.SemiFlyer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class TargetWhenAwakeGoal<T extends LivingEntity> extends NonTameRandomTargetGoal<T> {
    private final HBTamableAnimal animal;

    public TargetWhenAwakeGoal(HBTamableAnimal tamableMob, Class<T> toTarget, @Nullable Predicate<LivingEntity> predicate) {
        super(tamableMob, toTarget, true, predicate);
        this.animal = tamableMob;
    }

    @Override
    public boolean canUse() {
        return !this.animal.isNapping() && super.canUse();
    }

    @Override
    protected AABB getTargetSearchArea(double pTargetDistance) {
        if (this.mob instanceof SemiFlyer flyer && flyer.isFlying()) {
            AABB aabb = this.mob.getBoundingBox();
            double newDistance = 2.0F;
            return new AABB(aabb.minX - newDistance, mob.level().getMinBuildHeight() - 5, aabb.minZ - newDistance, aabb.maxX + newDistance, aabb.maxY + 1, aabb.maxZ + newDistance);
        }
        return super.getTargetSearchArea(pTargetDistance);
    }
}
