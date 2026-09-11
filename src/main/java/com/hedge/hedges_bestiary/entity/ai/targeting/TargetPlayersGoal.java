package com.hedge.hedges_bestiary.entity.ai.targeting;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.entity.types.SemiFlyer;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class TargetPlayersGoal extends NearestAttackableTargetGoal<Player> {

    private final HBTamableAnimal animal;
    public TargetPlayersGoal(HBTamableAnimal mob) {
        super(mob, Player.class, true);
        this.animal = mob;
    }

    @Override
    public boolean canUse() {
        if (this.animal.getAutoTargetType() > 1) {
            return super.canUse();
        }
        return false;
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
